/*
 * NCATS-COMMON
 *
 * Copyright 2020 NIH/NCATS
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gov.nih.ncats.common.util;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class DelayedComputationMap<K,V> extends AbstractMap<K,V> {

    private final Map<K,CachedSupplier<V>> supplierMap;

    public DelayedComputationMap(){
        supplierMap = new HashMap<>();
    }

    public DelayedComputationMap(int initialCapacity){
        supplierMap = new HashMap<>(initialCapacity);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AdaptedEntrySet(supplierMap.entrySet());
    }

    protected void added(K key, CachedSupplier<V> value){
        //no-op
    }
    protected void removed(K key, CachedSupplier<V> value){
        //no-op
    }
    public CachedSupplier<V> put(K key, Supplier<V> value) {
        CachedSupplier<V> newObj = CachedSupplier.of(value);
        CachedSupplier<V> old=supplierMap.put(key,newObj);
        if(old!=null){
            removed(key, old);
        }

        added(key, newObj);
        return old;
    }
    @Override
    public V put(K key, V value) {
        CachedSupplier<V> newObj = CachedSupplier.of(() -> value);
        CachedSupplier<V> old=supplierMap.put(key, newObj);
         if(old==null){
             added(key, newObj);
             return null;
         }
         removed(key, old);
         added(key, newObj);
         return old.get();
    }

    @Override
    public boolean remove(Object key, Object value) {
        CachedSupplier<V> cachedSupplier = supplierMap.get(key);
        if(cachedSupplier ==null){
            return false;
        }
        if(Objects.equals(value, cachedSupplier.get())){
            remove(key);
            return true;
        }
        return false;
    }

    public CachedSupplier<V> getAsCachedSupplier(K key){
        return supplierMap.get(key);
    }
    @Override
    public V remove(Object key) {
        CachedSupplier<V> cachedSupplier = supplierMap.remove(key);
        if(cachedSupplier ==null){
            return null;
        }
        V oldValue = cachedSupplier.get();
        removed((K) key, cachedSupplier);
        return oldValue;

    }

    public CachedSupplier<V> removeAsCachedSupplier(K key) {
        CachedSupplier<V> cachedSupplier = supplierMap.remove(key);
        if(cachedSupplier ==null){
            return null;
        }
        removed( key, cachedSupplier);
        return cachedSupplier;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        //Removes the entry for the specified key
        //only if it is currently mapped to the specified value.
        CachedSupplier<V> cachedSupplier = supplierMap.get(key);
        if(cachedSupplier ==null){
            return false;
        }
        if(Objects.equals(newValue, cachedSupplier.get())){
            replace(key,newValue);
            return true;
        }
        return false;
    }

    @Override
    public V replace(K key, V value) {
        CachedSupplier<V> newObj = CachedSupplier.ofConstant(value);
        CachedSupplier<V> oldValue = supplierMap.replace(key, newObj);
        if(oldValue ==null){
            added(key, newObj);
            return null;
        }
        //we get it first incase it's still cached
        //which might get reset once we fire the added and removed methods
        V oldGottenValue= oldValue.get();
        removed(key, oldValue);
        added(key, newObj);
        return oldGottenValue;
    }

    public void putAndIgnoreReturn(K key, V value) {
        supplierMap.put(key, CachedSupplier.of(()->value));
    }



    private class AdaptedEntrySet implements Set<Entry<K,V>>{
        private final Set<Entry<K,CachedSupplier<V>>> delegate;

        public AdaptedEntrySet(Set<Entry<K, CachedSupplier<V>>> delegate) {
            this.delegate = delegate;
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return delegate.contains(o);
        }

        @Override
        public Iterator<Entry<K, V>> iterator() {
            return delegate.stream()
                    .map(this::callCachedEntry)
                    .iterator();
        }

        //TODO handle toArray correctly
        @Override
        public Object[] toArray() {
            return delegate.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return delegate.toArray(a);
        }

        public boolean add(Entry<K, V> entry) {
            //pull the value out so we can GC the entry if needed
            V value = entry.getValue();
            return delegate.add(new AbstractMap.SimpleEntry<>(entry.getKey(), CachedSupplier.ofConstant(value)));
        }


        @Override
        public boolean remove(Object o) {
           //o is an Entry!
            if(!(o instanceof Map.Entry)){
                return false;
            }
            Map.Entry<K, V> entry = (Map.Entry<K,V>) o;
            CachedSupplier<V> cachedSupplier = getAsCachedSupplier(entry.getKey());
            boolean ret= delegate.remove(o);
            if(ret) {
                removed(entry.getKey(), cachedSupplier);
            }
            return ret;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return delegate.containsAll(c);
        }

        public boolean addAll(Collection<? extends Entry<K, V>> c) {
            //simple implementation for now just call add a lot
            boolean added=false;
            for(Entry<K,V> entry : c){
                if(add(entry)){
                    added=true;
                }
            }
            return added;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            //TODO implement
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            //TODO implement
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            //TODO implement
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            return delegate.equals(o);
        }

        @Override
        public int hashCode() {
            return delegate.hashCode();
        }

        @Override
        public Spliterator<Entry<K, V>> spliterator() {
            return Spliterators.spliterator(stream().iterator(),
                    delegate.size(),
                    Spliterator.SIZED);
        }

        protected Iterator<Entry<K,CachedSupplier<V>>> cachedSupplierIterator(){
            return delegate.iterator();
        }
        public boolean removeIf(Predicate<? super Entry<K,V>> filter) {
            Iterator<Entry<K,CachedSupplier<V>>> iter = cachedSupplierIterator();
            boolean removed=false;
            while(iter.hasNext()){
                Entry<K,CachedSupplier<V>> entry = iter.next();
                CachedSupplier<V> value = entry.getValue();
                if(filter.test(new AbstractMap.SimpleEntry<>(entry.getKey(), value.get()))){
                    iter.remove();
                    removed= true;
                    removed(entry.getKey(), value);
                }
            }
            return removed;

        }

        @Override
        public Stream<Entry<K, V>> stream() {
            return delegate.stream().map( this::callCachedEntry);
        }

        @Override
        public Stream<Entry<K, V>> parallelStream() {
            return delegate.parallelStream().map( this::callCachedEntry);
        }

        private Entry<K,V> callCachedEntry(Entry<K, CachedSupplier<V>> entry){
            return new AbstractMap.SimpleEntry<K,V>(entry.getKey(), entry.getValue().get());
        }
        public void forEach(Consumer<? super Entry<K, V>> action) {
            delegate.forEach(e-> action.accept(callCachedEntry(e)));
        }
    }
}
