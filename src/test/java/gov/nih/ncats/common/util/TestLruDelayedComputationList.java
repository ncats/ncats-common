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

import org.junit.Test;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
public class TestLruDelayedComputationList {

    @Test
    public void addingToEndOnlyCachedEverything(){
        LruDelayedComputationList<Integer> list = new LruDelayedComputationList<>(10, 20);

        AtomicInteger counter = new AtomicInteger();
        for(int i=0; i< 10; i++){
            list.add(()->counter.getAndIncrement());

        }

        for(int i=0; i< 10; i++){
            assertEquals(Integer.valueOf(i), list.get(i));
        }
        //do it again should have cached value
        for(int i=0; i< 10; i++){
            assertEquals(Integer.valueOf(i), list.get(i));
        }
    }
    @Test
    public void addingToEndOnlyResetBeforeCalledAgain(){
        LruDelayedComputationList<Integer> list = new LruDelayedComputationList<>(10, 5);

        AtomicInteger counter = new AtomicInteger();
        for(int i=0; i< 10; i++){
            list.add(()->counter.getAndIncrement());

        }

        for(int i=0; i< 10; i++){
            assertEquals(Integer.valueOf(i), list.get(i));
        }
        //second time through we have reset the caches
        for(int i=0; i< 10; i++){
            assertEquals(Integer.valueOf(10+ i), list.get(i));
        }
    }

    @Test
    public void addingToMiddleThenGettingShouldResetLeastRecentlyUsed(){
        LruDelayedComputationList<Integer> list = new LruDelayedComputationList<>(10, 10);

        AtomicInteger counter = new AtomicInteger();
        for(int i=0; i< 10; i++){
            list.add(()->counter.getAndIncrement());

        }
        //nothing reset yet until we get something
        for(int i=0; i< list.size(); i++){
            assertFalse(list.getAsCachedSupplier(i).hasRun());
        }
        for(int i=0; i< 10; i++){
            assertEquals(Integer.valueOf(i), list.get(i));
        }
        list.add(4, ()->counter.getAndIncrement());



        assertEquals(Integer.valueOf(10), list.get(4));
        //this has been reset
        assertFalse(list.getAsCachedSupplier(0).hasRun());
        //others in list haven't been reset yet
        for(int i=1; i< list.size(); i++){
            assertTrue(list.getAsCachedSupplier(i).hasRun());
        }
    }
}
