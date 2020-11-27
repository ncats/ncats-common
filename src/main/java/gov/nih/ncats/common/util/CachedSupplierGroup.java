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
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Supplier;

/**
 * An object that groups a bunch of {@link CachedSupplier}s together so
 * clients can do bulk resets by group.
 */
public class CachedSupplierGroup implements ResetableCache{

    private final Deque<ResetableCache> cachedSuppliers = new ConcurrentLinkedDeque<>();


    public CachedSupplierGroup(){

    }

    /**
     * Reset all {@link CachedSupplier}s in this group.
     *
     */
    public void resetAll(){
        for(ResetableCache c: cachedSuppliers){
            c.resetCache();
        }
    }

    @Override
    public void resetCache() {
            resetAll();
    }

    /**
     * Add the given CachedSupplier to this group.
     * @param cachedSupplier the cachedSupplier to add - can not be null.
     * @param <T> the return type of the cachedSupplier.
     * @return the cachedSupplier passed in.
     * @throws NullPointerException if cachedSupplier is null.
     *
     * @implNote This method returns the passed in cachedSupplier
     * so you can chain adding it to the group to the setting of a field like this:
     * <pre>
     * {@code
     *  CachedSupplierGroup group = new CachedSupplierGroup();
     *
     *  CachedSupplier<T> cachedSupplier = group.add( CachedSupplier.of( ... ));
     * }
     * </pre>
     */
    public <T> CachedSupplier<T> add(CachedSupplier<T> cachedSupplier){
        cachedSuppliers.add(Objects.requireNonNull(cachedSupplier));
        return cachedSupplier;
    }

    public <T> boolean remove(CachedSupplier<T> cachedSupplier){
        return cachedSuppliers.remove(cachedSupplier);
    }

    /**
     * Wrap the given {@link Supplier} in a CachedSupplier and add it to this group.
     * @param supplier the cachedSupplier to add - can not be null.
     * @param <T> the return type of the cachedSupplier.
     * @return the newly created CachedSupplier for the passed in Supplier.
     * @throws NullPointerException if supplier is null.
     *
     * @implNote This this is the same as but more convient way to write:
     * <pre>
     * {@code
     *  Supplier<T> supplier = ...
     *
     *  add(CachedSupplier.of(supplier);
     * }
     * </pre>
     */
    public <T> CachedSupplier<T> add(Supplier<T> supplier){
        //cachedSupplier constructor does null check so we don't have to
        CachedSupplier<T> cachedSupplier = CachedSupplier.of(supplier);
        cachedSuppliers.add(cachedSupplier);
        return cachedSupplier;
    }
    /**
     * Add the given CachedSupplierGroup to this group.
     * @param otherGroup the CachedSupplierGroup to add - can not be null.
     *
     * @throws NullPointerException if CachedSupplierGroup is null.
     */
    public void add(CachedSupplierGroup otherGroup){
        cachedSuppliers.add(Objects.requireNonNull(otherGroup));
    }

}
