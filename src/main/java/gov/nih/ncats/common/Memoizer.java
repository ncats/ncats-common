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

package gov.nih.ncats.common;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Created by katzelda on 9/30/16.
 */
public class Memoizer<A, V>  {

    private final ConcurrentMap<A, Future<V>> cache = new ConcurrentHashMap<>();

    public V computeIfAbsent(A arg, Function<A, V> computation) throws InterruptedException {

        while(true){

            Future<V> f = cache.get(arg);
            if(f == null) {
                FutureTask<V> ft = new FutureTask<>(() -> computation.apply(arg));
                //this is a double check just in case another thread
                //happened to put it in...
                f = cache.putIfAbsent(arg, ft);
                if (f == null) {
                    //was put because it was absent
                    f = ft;
                    ft.run(); // run in current thread?
                }
            }
            try{
                return f.get();
            }catch(CancellationException e){
                cache.remove(arg, f);
            }catch(ExecutionException e){
                throw new IllegalStateException(e);
            }
        }
    }

    public void clear() {
        cache.clear();
    }
}
