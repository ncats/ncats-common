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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DelayedComputationList<T> extends AbstractList<T> implements ResetableCache {

    private final List<CachedSupplier<T>> cachedSupplierList;

    private final CachedSupplierGroup group = new CachedSupplierGroup();

    public DelayedComputationList(){
        cachedSupplierList = new ArrayList<>();
    }
    public DelayedComputationList(int initialSize){
        cachedSupplierList = new ArrayList<>(initialSize);
    }
    @Override
    public T get(int index) {
        CachedSupplier<T> cachedSupplier = getAsCachedSupplier(index);
        return cachedSupplier.get();
    }

    protected void removed( CachedSupplier<T> cachedSupplier){
        //default to no-op
    }

    protected void added( CachedSupplier<T> cachedSupplier){
        //default to no-op
    }
    @Override
    public T remove(int index) {

        CachedSupplier<T> cachedSupplier= removeAsCachedSupplier(index);
        if(cachedSupplier ==null){
            return null;
        }

        return cachedSupplier.get();
    }
    public CachedSupplier<T> removeAsCachedSupplier(int index) {
        CachedSupplier<T> cachedSupplier = cachedSupplierList.remove(index);
        if (cachedSupplier != null) {
            group.remove(cachedSupplier);
            removed( cachedSupplier);
        }
        return cachedSupplier;
    }

    public CachedSupplier<T> getAsCachedSupplier(int index){
        return cachedSupplierList.get(index);
    }

    @Override
    public T set(int index, T element) {
        CachedSupplier<T> newObj = CachedSupplier.ofConstant(element);
        CachedSupplier<T> oldObj = cachedSupplierList.set(index, newObj);
        if(oldObj !=null) {
            group.remove(oldObj);
            removed(oldObj);
        }
        added(newObj);
        return oldObj.get();
    }

    @Override
    public void add(int index, T element) {
        cachedSupplierList.add(index, CachedSupplier.ofConstant(element));
    }
    public boolean add(int index, Supplier<T> supplier){
        CachedSupplier<T> cachedSupplier = group.add(CachedSupplier.of(supplier));
        cachedSupplierList.add(index,cachedSupplier);
        added(cachedSupplier);
        return true;
    }

    public boolean add(Supplier<T> supplier){
        CachedSupplier<T> cachedSupplier = group.add(CachedSupplier.of(supplier));
        cachedSupplierList.add(cachedSupplier);
        added(cachedSupplier);
        return true;
    }

    @Override
    public int size() {
        return cachedSupplierList.size();
    }

    @Override
    public void resetCache() {
        group.resetAll();
    }
}
