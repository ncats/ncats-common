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

public class LruDelayedComputationMap<K,V> extends DelayedComputationMap<K,V>{
    private Map<CachedSupplier<V>, Boolean> lruCache;
    private Map<CachedSupplier<V>, CachedSupplier.SupplierInvocationListener> listeners = new HashMap<>();


    public LruDelayedComputationMap(int initialSize, int cacheSize) {
        super(initialSize);
        this.lruCache = Caches.<CachedSupplier<V>, Boolean>builder()
                .capacity(cacheSize)
                .setLru(e->
                    e.getKey().resetCache()
                )
                .build();
    }


    @Override
    protected void added(K key, CachedSupplier<V> value) {
        CachedSupplier.SupplierInvocationListener listener = ()-> lruCache.put(value, Boolean.TRUE);
        value.addListener(listener);
        listeners.put(value, listener);
    }

    @Override
    protected void removed(K key, CachedSupplier<V> value) {
        this.lruCache.remove(value);
        CachedSupplier.SupplierInvocationListener listener = listeners.remove(value);
        if(listener !=null) {
            value.removeListener(listener);
        }
    }


}
