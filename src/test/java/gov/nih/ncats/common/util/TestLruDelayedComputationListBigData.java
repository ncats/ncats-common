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

import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.Random;
import static org.junit.Assert.*;
public class TestLruDelayedComputationListBigData {
    DelayedComputationList<byte[]> sut;

    private static int CACHE_SIZE= 5;
    Random rand = new Random();
    @Before
    public void  setup(){
        sut = new LruDelayedComputationList<>(100, CACHE_SIZE);

        for(int i=0; i< 100; i++){
            sut.add(()-> {
                byte[] array= new byte[8_000_000];
                rand.nextBytes(array);
                return array;
            });  //~8 MB each
        }
    }

    @Test
    public void iterateAndCached(){
        for(int i=0; i< sut.size(); i++){
            sut.get(i);
            assertTrue(sut.getAsCachedSupplier(i).hasRun());
        }
    }
    @Test
    public void streamingThroughDoesntTakeUpTooMuchMemory(){

        Iterator<byte[]> iter = sut.iterator();

        while(iter.hasNext()){
            iter.next();
        }

        for(int i=0; i< sut.size()-CACHE_SIZE; i++){
           assertFalse(i + " offset has run when it shouldn't", sut.getAsCachedSupplier(i).hasRun());
        }
        for(int i= sut.size() - CACHE_SIZE; i< sut.size(); i++){
            assertTrue(i + " offset has NOT run when it should", sut.getAsCachedSupplier(i).hasRun());
        }

    }
}
