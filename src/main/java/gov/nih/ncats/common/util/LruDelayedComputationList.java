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


import java.util.HashMap;
import java.util.Map;

public class LruDelayedComputationList<T> extends DelayedComputationList<T> {
    private Map<CachedSupplier<T>, Boolean> lruCache;
    private Map<CachedSupplier<T>, CachedSupplier.SupplierInvocationListener> listeners = new HashMap<>();


    public LruDelayedComputationList(int initialSize, int cacheSize) {
        super(initialSize);
        this.lruCache = Caches.<CachedSupplier<T>, Boolean>builder()
                                .capacity(cacheSize)
                                .setLru(e-> e.getKey().resetCache())
                                .build();

    }


    @Override
    protected void removed(CachedSupplier<T> cachedSupplier) {
        this.lruCache.remove(cachedSupplier);
        CachedSupplier.SupplierInvocationListener listener = listeners.remove(cachedSupplier);
        if(listener !=null) {
            cachedSupplier.removeListener(listener);
        }
    }

    @Override
    protected void added(CachedSupplier<T> cachedSupplier) {
        CachedSupplier.SupplierInvocationListener listener = ()-> lruCache.put(cachedSupplier, Boolean.TRUE);
       cachedSupplier.addListener(listener);
       listeners.put(cachedSupplier, listener);
    }




}
