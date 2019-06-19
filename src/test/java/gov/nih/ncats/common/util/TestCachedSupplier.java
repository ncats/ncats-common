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

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * Created by katzelda on 6/19/19.
 */
public class TestCachedSupplier {

    @Test
    public void doesntRunUntilCalled(){
        int[] array = new int[1];
        CachedSupplier.of(()-> array[0]++);

        assertEquals(0, array[0]);
    }

    @Test
    public void callOnce(){
        int[] array = new int[1];
        CachedSupplier.of(()-> array[0]++).get();

        assertEquals(1, array[0]);
    }

    @Test
    public void callOnceSubclass(){
        int[] array = new int[1];
        CachedSupplier.runOnce(()-> array[0]++).get();

        assertEquals(1, array[0]);
    }

    @Test
    public void callTwiceShouldOnlyRunOnce(){
        int[] array = new int[1];
        CachedSupplier<Integer> cachedSupplier = CachedSupplier.of(() -> array[0]++);
        cachedSupplier.get();
        cachedSupplier.get();

        assertEquals(1, array[0]);
    }

    @Test
    public void callTwiceOnRunOnceShouldOnlyRunOnce(){
        int[] array = new int[1];
        CachedSupplier<Integer> cachedSupplier = CachedSupplier.runOnce(() -> array[0]++);
        cachedSupplier.get();
        cachedSupplier.get();

        assertEquals(1, array[0]);
    }

    @Test
    public void reset(){
        int[] array = new int[1];
        CachedSupplier<Integer> cachedSupplier = CachedSupplier.of(() -> array[0]++);
        cachedSupplier.get();
        cachedSupplier.resetCache();
        cachedSupplier.get();

        assertEquals(2, array[0]);
    }

    @Test
    public void resetRunOnceSubclassDoesntDoAnything(){
        int[] array = new int[1];
        CachedSupplier<Integer> cachedSupplier = CachedSupplier.runOnce(() -> array[0]++);
        cachedSupplier.get();
        cachedSupplier.resetCache();
        cachedSupplier.get();

        assertEquals(1, array[0]);
    }

    @Test
    public void resetAllCaches(){
        int[] array = new int[2];
        CachedSupplier<Integer> cachedSupplier = CachedSupplier.of(() -> array[0]++);
        cachedSupplier.get();



        CachedSupplier<Integer> cachedSupplier2 = CachedSupplier.of(() -> array[1]++);

        cachedSupplier2.get();

        CachedSupplier.resetAllCaches();

        cachedSupplier.get();
        cachedSupplier2.get();

        assertEquals(2, array[0]);
        assertEquals(2, array[1]);
    }

    @Test
    public void resetAllCachesDoesntAffectRunOnce(){
        int[] array = new int[2];
        CachedSupplier<Integer> cachedSupplier = CachedSupplier.of(() -> array[0]++);
        cachedSupplier.get();



        CachedSupplier<Integer> cachedSupplier2 = CachedSupplier.runOnce(() -> array[1]++);

        cachedSupplier2.get();

        CachedSupplier.resetAllCaches();

        cachedSupplier.get();
        cachedSupplier2.get();

        assertEquals(2, array[0]);
        assertEquals(1, array[1]);
    }

    @Test
    public void resetOneCache(){
        int[] array = new int[2];
        CachedSupplier<Integer> cachedSupplier = CachedSupplier.of(() -> array[0]++);
        cachedSupplier.get();



        CachedSupplier<Integer> cachedSupplier2 = CachedSupplier.of(() -> array[1]++);

        cachedSupplier2.get();

        cachedSupplier.resetCache();

        cachedSupplier.get();
        cachedSupplier2.get();

        assertEquals(2, array[0]);
        assertEquals(1, array[1]);
    }

    @Test
    public void multiThreaded() throws Exception{
        int[] array = new int[1];
        CachedSupplier<Integer> cachedSupplier = CachedSupplier.of(() -> array[0]++);

        ExecutorService service = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(10);
        for(int i=0; i< 10; i++){
            service.submit(()-> {
                cachedSupplier.get();
                latch.countDown();
            });
        }
        service.shutdown();
        latch.await();



        assertEquals(1, array[0]);
    }

}
