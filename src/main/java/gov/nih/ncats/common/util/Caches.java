/*
 * NCATS-COMMON
 *
 * Copyright 2019 NIH/NCATS
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


import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <code>Caches</code> is a utility class which contains various 
 * implementations caches. Some implementations use a Least-Recently-Used
 * policy (LRU) some implementations use {@link SoftReference}s or {@link WeakReference}s
 * to avoid memory leaks.  
 * 
 * 
 * @author dkatzel
 * 
 */
public final class Caches
{
    

    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    /**
     * The value of this constant is {@value}.
     */
    public static final int DEFAULT_CAPACITY = 16;
    
    private Caches(){
    	//can not instantiate
    }
    
    /**
     * Creates an LRUCache of default capacity.  Entries are held
     * in the map until capacity is exceeded.
     * @param <K> the (strongly reference) key type
     * @param <V> the (strongly reference) value type
     * @return a new Map instance with default capacity {@value #DEFAULT_CAPACITY}.
     */
    public static <K,V> Map<K,V> createLRUCache(){
        return createLRUCache(DEFAULT_CAPACITY);
    }
    /**
     * Creates an LRUCache of the given max capacity.  Entries are held
     * in the map until capacity is exceeded.
     * @param <K> the (strongly reference) key type
     * @param <V> the (strongly reference) value type
     * @param maxcapacity the max size of this cache before it should start removing
     * the least recently used.
     * @return a new Map instance with given capacity
     */
    public static <K,V> Map<K,V> createLRUCache(int maxcapacity){
       
        return new LRUCache<K,V>(maxcapacity);
    }
    /**
     * Create a map of strong references with an initial default capacity
     * of {@value #DEFAULT_CAPACITY} which <strong>CAN GROW</strong>  if more than
     * that number of entries is inserted.  This should have
     * the same semantics as {@code new HashMap<K,V>() }.
     * @param <K> the (strongly reference) key type
     * @param <V> the (strongly reference) value type
     * @return a new Map instance with default capacity that can grow
     * if more entries are added just like a normal Map.
     */
    public static <K,V> Map<K,V> createMap(){
        return createMap(DEFAULT_CAPACITY);
    }
    /**
     * Create a map of strong references with an initial given capacity
     * which <strong>CAN GROW</strong> if more than
     * that number of entries is inserted.  This should have
     * the same semantics as {@code new HashMap<K,V>() }.
     * @param <K> the (strongly reference) key type
     * @param <V> the (strongly reference) value type
     * @param initialSize the initial size of the map which can later
     * grow in size as more entries are added.
     * @return a new Map instance with the given capacity that can grow
     * if more entries are added just like a normal Map.
     */
    public static <K,V> Map<K,V> createMap(int initialSize){
        return new LinkedHashMap<K,V>(initialSize);
    }
    /**
     * Creates an LRUCache of default capacity where the VALUES in the map
     * are each wrapped with a {@link SoftReference}.  Entries can
     * be removed by 3 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if its value
     * is only weakly reachable AND the garbage collector
     * wants to reclaim memory.</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createSoftReferencedValueLRUCache(){
        return createSoftReferencedValueLRUCache(DEFAULT_CAPACITY);
    }
    
    /**
     * Creates an Map of default capacity {@value #DEFAULT_CAPACITY}
     *  where the VALUES in the map
     * are each wrapped with a {@link SoftReference}. The size 
     * of this map <strong>CAN GROW</strong> if more
     * entries are inserted.  Entries can
     * be removed by 2 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createSoftReferencedValueCache(){
        return createSoftReferencedValueCache(DEFAULT_CAPACITY);
    }
   
    /**
     * Creates an Map using the given capacity where the VALUES in the map
     * are each wrapped with a {@link SoftReference}. The size 
     * of this map <strong>CAN GROW</strong> if more
     * entries are inserted.  Entries can
     * be removed by 2 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @param initialCapacity the initialCapacity of this cache which is used
     * to calculate when the map should grow and be re-hashed.
     * @return a new Map instance with the given capacity.
     */
    public static <K,V> Map<K,V> createSoftReferencedValueCache(int initialCapacity){
        return new SoftReferenceCache<K, V>(initialCapacity);
    }
    
    /**
     * Creates an LRUCache where the VALUES in the map
     * are each wrapped with a {@link SoftReference}.  Entries can
     * be removed by 3 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if its value
     * is only weakly reachable AND the garbage collector
     * wants to reclaim memory.</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @param maxSize the max size of this cache before it should start removing
     * the least recently used.
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createSoftReferencedValueLRUCache(int maxSize){
        return new SoftReferenceLRUCache<K, V>(maxSize,null);
    }
    
    /**
     * Creates an LRUCache with max capacity of {@value #DEFAULT_CAPACITY} where the VALUES in the map
     * are each wrapped with a {@link WeakReference}.  Entries can
     * be removed by 3 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if its value
     * is only weakly reachable</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @return a new Map instance with default capacity.
     */
    public static <K,V> Map<K,V> createWeakReferencedValueLRUCache(){
        return createWeakReferencedValueLRUCache(DEFAULT_CAPACITY);
    }
    /**
     * Creates an LRUCache where the VALUES in the map
     * are each wrapped with a {@link WeakReference}.  Entries can
     * be removed by 3 different ways:
     * <ol>
     * <li> If capacity is exceeded, then the least recently used
     * entry is removed to make room</li>
     * <li> Any entries may be removed if its value
     * is only weakly reachable</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @param maxSize the max size of this cache before it should start removing
     * the least recently used.
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createWeakReferencedValueLRUCache(int maxSize){
        return new WeakReferenceLRUCache<K,V>(maxSize, null);
    }
    
    /**
     * Creates an LRUCache where the VALUES in the map
     * are each wrapped with a {@link WeakReference}.  The size 
     * of this map <strong>CAN GROW</strong> if more
     * entries are inserted.  Entries can
     * be removed by 2 different ways:
     * <ol>
     * <li> if its the value is only weakly reachable.</li>
     * <li> Any entries may be removed if the {@link Map#remove(Object)}
     * is called.</li>
     * </ol> 
     * @param <K> the (strongly reference) key type
     * @param <V> the softly referenced value type
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createWeakReferencedValueCache(){
        return createWeakReferencedValueLRUCache(DEFAULT_CAPACITY);
    }
    /**
     * Creates an LRUCache where the VALUES in the map
     * are each wrapped with a {@link WeakReference}.  This will
     * allow the map to remove any entries if its value
     * is only weakly reachable.
     * @param <K> the (strongly reference) key type
     * @param <V> the weakly referenced value type
     * @param maxSize the max size of this cache before it should start removing
     * the least recently used.
     * @return a new Map instance with default capacity
     */
    public static <K,V> Map<K,V> createWeakReferencedValueCache(int maxSize){
        return new WeakReferenceCache<K,V>(maxSize);
    }
    
    private static <K,V> Map<K,V> createNonLRUMap(int maxSize){
    	return new LinkedHashMap<K, V>(maxSize);
    }
    /**
     * This uses the Java-native implementation of
    * a {@link LinkedHashMap} with last-access ordering and capacity limitation
    * to remove the element which was least recently accessed via the 
    * {@link #get(Object)} method.  This removal only occurs once the capacity
    * is reached.
    * <p>
    * This has the handy effect of creating a simple cache.  The greatest 
    * benefits when using this cache are seen when elements are accessed in
    * clusters, since they will generate large numbers of cache hits followed
    * by steadily dropping out of the cache.
    */
    private static final class LRUCache<K,V> extends LinkedHashMap<K, V>{
 
            private static final long serialVersionUID = -9015747210650112857L;
        private final int maxAllowedSize;

        private final Consumer<Entry<K, V>> removedConsumer;

        protected LRUCache(int maxAllowedSize, float loadFactor, Consumer<Entry<K, V>> removedConsumer)
        {
            super(MapUtil.computeMinHashMapSizeWithoutRehashing(maxAllowedSize, loadFactor),loadFactor, true);
            this.maxAllowedSize = maxAllowedSize;
            this.removedConsumer = removedConsumer==null? e->{} : removedConsumer;
        }
    
        protected LRUCache(int maxAllowedSize)
        {
            this(maxAllowedSize, Caches.DEFAULT_LOAD_FACTOR,e->{});
        }
    
        
        @Override
        protected boolean removeEldestEntry(Entry<K, V> eldest) {
            if(this.size() > this.maxAllowedSize){
                removedConsumer.accept(eldest);
                return true;
            }
            return false;
        }
    
    }
    /**
     * {@code AbstractReferencedLRUCache} is an adapter so we can make an LRUCache
     * but the values are not strong Java References.  This will allow
     * the JVM to remove entries from the cache if we need more memory.
     * @author dkatzel
     *
     *
     */
    private abstract static class AbstractReferencedCache<K,V,R extends Reference<V>> extends AbstractMap<K,V>{
        
        
        private final Map<K, R> cache;
        private final ReferenceQueue<V> referenceQueue = new ReferenceQueue<V>();
        private final Map<Reference<? extends V>, K> referenceKeyMap;
        /**
         * Creates a new AbstractReferencedCache instance using the given map
         * @param map the map of {@link Reference}s mapped by a Key.
         * @param initialCapacity the initial size of the references.
         */
        AbstractReferencedCache(Map<K,R> map, int initialCapacity) {
            cache = map;
            int mapSize = MapUtil.computeMinHashMapSizeWithoutRehashing(initialCapacity);
            referenceKeyMap = new HashMap<Reference<? extends V>, K>(mapSize);
        }
        protected abstract R createReferenceFor(V value,final ReferenceQueue<V> referenceQueue);
        
        /**
         * Remove any entries in the 
         * cache that have had their values
         * garbage collected.  The GC could have collected any of the values
         * we still have keys for so poll our registered references
         * to see what was collected and remove them from our cache.
         */
        private synchronized void removeAnyGarbageCollectedEntries(){
            Reference<? extends V> collectedReference;
            while((collectedReference = referenceQueue.poll()) !=null){
               
                K key =referenceKeyMap.remove(collectedReference);
                cache.remove(key);                
            }
        }

        @Override
        public synchronized int size() {
            removeAnyGarbageCollectedEntries();
            return cache.size();
        }

        @Override
        public synchronized boolean isEmpty() {
            removeAnyGarbageCollectedEntries();
            return cache.isEmpty();
        }

        @Override
        public synchronized boolean containsKey(Object key) {
            removeAnyGarbageCollectedEntries();
            return cache.containsKey(key);
        }

        @Override
        public synchronized V get(Object key) {
            removeAnyGarbageCollectedEntries();
            R softReference= cache.get(key);
            return getReference(softReference);
        }

        @Override
        public synchronized V put(K key, V value) {
            removeAnyGarbageCollectedEntries();
            R newReference = createReferenceFor(value,referenceQueue);
            R oldReference= cache.put(key, newReference);
            referenceKeyMap.put(newReference, key);
            return getReference(oldReference);
            
        }


        @Override
        public synchronized V remove(Object key) {
            removeAnyGarbageCollectedEntries();
            R oldReference= cache.remove(key);
            referenceKeyMap.remove(oldReference);
            return getReference(oldReference);
        }

        private V getReference(R ref){
            if(ref ==null){
                return null;
            }
            return ref.get();
        }

        @Override
        public synchronized void clear() {
            removeAnyGarbageCollectedEntries();
            cache.clear();
            referenceKeyMap.clear();
        }
        @Override
        public synchronized Set<K> keySet() {
            removeAnyGarbageCollectedEntries();
            return cache.keySet();
        }

        @Override
        public synchronized Collection<V> values() {
            removeAnyGarbageCollectedEntries();
            Collection<R> softValues =cache.values();
            List<V> actualValues = new ArrayList<V>(softValues.size());
            for(R softValue : softValues){
                if(softValue !=null){
                    actualValues.add(softValue.get());
                }
            }
            return actualValues;
        }



        /**
        * {@inheritDoc}
        */
        @Override
        public synchronized Set<Entry<K, V>> entrySet() {
            removeAnyGarbageCollectedEntries();
            Set<Entry<K,V>> result = new LinkedHashSet<Entry<K, V>>();
            for(final Entry<K,R> entry : cache.entrySet()){
                final K key = entry.getKey();
                final V value =entry.getValue().get();
                if(value !=null){
                    //we still have it
                    result.add(new Entry<K, V>() {

                        @Override
                        public K getKey() {
                            return key;
                        }

                        @Override
                        public V getValue() {
                            return value;
                        }

                        @Override
                        public V setValue(V newValue) {
                            entry.setValue(createReferenceFor(newValue,referenceQueue));
                            return value;
                        }
                        
                    });
                }
            }
            return result;
        }
        
    }
    
    /**
     * {@code SoftReferenceLRUCache} creates an LRUCache which uses
     * {@link SoftReference}s for the values.
     * @author dkatzel
     * @see SoftReference
     *
     */
    private static class SoftReferenceCache<K,V> extends AbstractReferencedCache<K,V, SoftReference<V>>{
       
       
        /**
         * Create a new SoftReferenceCache with the given capacity.
         * @param initialCapacity the number of references to store in the map;
         * should be >=1.
         */
        public SoftReferenceCache(int initialCapacity) {
        	super(Caches.<K,SoftReference<V>>createNonLRUMap(initialCapacity), initialCapacity);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected SoftReference<V> createReferenceFor(V value, ReferenceQueue<V> referenceQueue) {
            return new SoftReference<V>(value, referenceQueue);
        }

      

    }
    /**
     * {@code SoftReferenceLRUCache} creates an LRUCache which uses
     * {@link SoftReference}s for the values.
     * @author dkatzel
     * @see SoftReference
     *
     */
    private static class SoftReferenceLRUCache<K,V> extends AbstractReferencedCache<K,V, SoftReference<V>>{


        /**
         * Create a new SoftReferenceLRUCache with the given max capacity.
         * If the map ever grows beyond the max capacity, then the least
         * recently used element will be removed to make room.
         * @param maxSize the max number of references to store in the map;
         * should be >=1.
         */
        SoftReferenceLRUCache(int maxSize, Consumer<Entry<K,V>> biConsumer) {
            this(maxSize, DEFAULT_LOAD_FACTOR, biConsumer);
        }
        /**
         * Create a new SoftReferenceLRUCache with the given max capacity.
         * If the map ever grows beyond the max capacity, then the least
         * recently used element will be removed to make room.
         * @param maxSize the max number of references to store in the map;
         * should be >=1.
         */
        SoftReferenceLRUCache(int maxSize, float loadFactor, Consumer<Entry<K,V>> biConsumer) {
          super(
        		  new LRUCache<K,SoftReference<V>>(
        				  maxSize, 
        				  loadFactor,
                          biConsumer ==null? e->{}:
                                  e-> biConsumer.accept(new AbstractMap.SimpleEntry<K,V>(e.getKey(), e.getValue().get()))
                          ),
				  maxSize);
        }

        /**
        * {@inheritDoc}
        */
        @Override
        protected SoftReference<V> createReferenceFor(V value, ReferenceQueue<V> referenceQueue) {
            return new SoftReference<V>(value, referenceQueue);
        }

      

    }
    /**
     * {@code WeakReferenceLRUCache} creates an LRUCache which uses
     * {@link WeakReference}s for the values.
     * @author dkatzel
     * @see WeakReference
     *
     */
    private static class WeakReferenceLRUCache<K,V> extends AbstractReferencedCache<K,V, WeakReference<V>>{

        /**
         * Create a new WeakReferenceLRUCache with the given max capacity.
         * If the map ever grows beyond the max capacity, then the least
         * recently used element will be removed to make room.
         * @param maxSize the max number of references to store in the map;
         * should be >=1.
         */
        public WeakReferenceLRUCache(int maxSize, Consumer<Entry<K,V>> consumer) {
            this(maxSize, DEFAULT_LOAD_FACTOR, consumer);
        }
    	 /**
         * Create a new WeakReferenceLRUCache with the given max capacity.
         * If the map ever grows beyond the max capacity, then the least
         * recently used element will be removed to make room.
         * @param maxSize the max number of references to store in the map;
         * should be >=1.
         */
        public WeakReferenceLRUCache(int maxSize, float loadFactor, Consumer<Entry<K,V>> consumer) {
            super(new LRUCache<K,WeakReference<V>>(maxSize, loadFactor,
                    consumer ==null? e->{}:
                            e-> consumer.accept(new AbstractMap.SimpleEntry<K,V>(e.getKey(), e.getValue().get()))
            ), maxSize);
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        protected WeakReference<V> createReferenceFor(V value, ReferenceQueue<V> referenceQueue) {
            return new WeakReference<V>(value,referenceQueue);
        }

    }
    
    /**
     * {@code WeakReferenceCache} creates an Cache which uses
     * {@link WeakReference}s for the values.
     * @author dkatzel
     * @see WeakReference
     *
     */
    private static class WeakReferenceCache<K,V> extends AbstractReferencedCache<K,V, WeakReference<V>>{
        
        
    	/**
         * Create a new WeakReferenceCache with the given capacity.
         * @param initialCapacity the number of references to store in the map;
         * should be >=1.
         */
        public WeakReferenceCache(int initialCapacity) {
        	super(Caches.<K,WeakReference<V>>createNonLRUMap(initialCapacity), initialCapacity);
        }
        
        /**
        * {@inheritDoc}
        */
        @Override
        protected WeakReference<V> createReferenceFor(V value, ReferenceQueue<V> referenceQueue) {
            return new WeakReference<V>(value,referenceQueue);
        }

    }
    public enum Type{
        STRONG{
            @Override
            <K, V> Map<K, V> create(int initialCapacity, float loadFactor) {
                return new LinkedHashMap<K,V>(initialCapacity, loadFactor);
            }

            @Override
            <K, V> Map<K, V> createLru(int size,  float loadFactor, Consumer<Entry<K, V>> removedEldestEntryConsumer) {
                return new LRUCache<K,V>(size, loadFactor, removedEldestEntryConsumer);
            }
        },
        SOFT{
            @Override
            <K, V> Map<K, V> create(int initialCapacity, float loadFactor) {
                return new SoftReferenceCache<K, V>(initialCapacity);
            }
            @Override
            <K, V> Map<K, V> createLru(int size,  float loadFactor, Consumer<Entry<K, V>> removedEldestEntryConsumer) {
                return new SoftReferenceLRUCache<>(size, loadFactor, removedEldestEntryConsumer);
            }
        },
        WEAK{
            @Override
            <K, V> Map<K, V> create(int initialCapacity, float loadFactor) {
                return new WeakReferenceCache<>(initialCapacity);
            }
            @Override
            <K, V> Map<K, V> createLru(int size,  float loadFactor, Consumer<Entry<K, V>> removedEldestEntryConsumer) {
                return new WeakReferenceLRUCache<>(size, loadFactor, removedEldestEntryConsumer);
            }
        };
        abstract <K,V> Map<K,V> create(int initialCapacity, float loadFactor);

        abstract <K,V> Map<K,V> createLru(int size, float loadFactor, Consumer<Entry<K, V>> removedEldestEntryConsumer);

        private <K,V> Map<K,V> createCache(int initialCapacity, float loadFactor, boolean lruFlag, Consumer<Entry<K, V>> removedEldestEntryConsumer){
            if(lruFlag){
                return createLru(initialCapacity, loadFactor, removedEldestEntryConsumer);
            }else{
                return create(initialCapacity,loadFactor);
            }
        }
    }

    public static <K,V> Builder<K,V> builder(){
        return new Builder<>();
    }
    public static class Builder<K,V>{
        private Type type = Type.STRONG;
        private boolean lruFlag = false;
        private float loadFactor = DEFAULT_LOAD_FACTOR;

        private int capacity = DEFAULT_CAPACITY;
        private Consumer<Entry<K,V>> removedEldestEntryConsumer;

        public Builder<K,V> type(Type type){
            this.type = type==null? Type.STRONG:type;
            return this;
        }
        public Builder<K,V> capacity(int capacity){
            if(capacity < 1){
                throw new IllegalArgumentException("capacity can not be < 1");
            }
            this.capacity = capacity;
            return this;
        }
        public Builder<K,V> loadFactor(float loadFactor){
            if(loadFactor <=0){
                throw new IllegalArgumentException("loadFactor must be > 0");
            }
            this.loadFactor = loadFactor;
            return this;
        }
        public Builder<K,V> setLru(boolean isLru){
            this.lruFlag=isLru;
            return this;
        }
        public Builder<K,V> setLru(Consumer<Entry<K,V>> removedEldestEntryConsumer){
            this.lruFlag=true;
            this.removedEldestEntryConsumer = removedEldestEntryConsumer;
            return this;
        }

        public Map<K,V> build(){
            return type.createCache(capacity,loadFactor,lruFlag, removedEldestEntryConsumer);
        }
    }
}
