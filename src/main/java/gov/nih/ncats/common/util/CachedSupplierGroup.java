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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An object that groups a bunch of {@link CachedSupplier}s together so
 * clients can do bulk resets by group.
 */
public class CachedSupplierGroup {

    private final List<CachedSupplier> cachedSuppliers = new ArrayList<>();

    public CachedSupplierGroup(){

    }

    /**
     * Reset all {@link CachedSupplier}s in this group (and only this group).
     *
     */
    public void resetAll(){
        for(CachedSupplier c: cachedSuppliers){
            c.resetCache();
        }
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

}
