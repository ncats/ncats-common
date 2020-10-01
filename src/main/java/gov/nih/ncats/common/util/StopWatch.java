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

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public final class StopWatch {

    private StopWatch(){
        //can not instantiate
    }

    public static long timeElapsed(Runnable r){
        long start = System.currentTimeMillis();
        r.run();
        long end = System.currentTimeMillis();
        return end-start;

    }

    public static long timeElapsed(Callable<Void> c) throws Exception{
        long start = System.currentTimeMillis();
        c.call();
        long end = System.currentTimeMillis();
        return end-start;
    }

    public static <T> T timeElapsedFetch(Callable<T> c, LongConsumer timeConsumer) throws Exception{
        long start = System.currentTimeMillis();
        try{
            T t= c.call();
            return t;
        }finally{
            long end = System.currentTimeMillis();
            timeConsumer.accept(end-start);
        }
    }

}
