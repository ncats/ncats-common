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

import org.junit.Rule;
import org.junit.Test;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
public class TestDelayedComputationMap {

    @Rule
    public FibonacciComputerRule fibonacciComputerRule = new FibonacciComputerRule();


    @Test
    public void dontComputeUntilGetCalled(){
        DelayedComputationMap<String, BigInteger> sut = new DelayedComputationMap<>();


        sut.put("fib(5)", ()-> fibonacciComputerRule.fibonacci(5));
        sut.put("fib(15)", ()-> fibonacciComputerRule.fibonacci(15));
        sut.put("fib(20)", ()-> fibonacciComputerRule.fibonacci(20));

        assertFalse(sut.getAsCachedSupplier("fib(5)").hasRun());
        assertFalse(sut.getAsCachedSupplier("fib(15)").hasRun());
        assertFalse(sut.getAsCachedSupplier("fib(20)").hasRun());

        assertEquals(BigInteger.valueOf(5), sut.get("fib(5)"));
        assertEquals(BigInteger.valueOf(610), sut.get("fib(15)"));
        assertEquals(BigInteger.valueOf(6765), sut.get("fib(20)"));
    }

    @Test
    public void lruMap(){
        LruDelayedComputationMap<String, Integer> sut = new LruDelayedComputationMap<>(26, 5);

        AtomicInteger counter = new AtomicInteger();

        for(int i=0; i< 26; i++){
            char letter = (char)('A' +i);
            String key = Character.toString(letter);
            sut.put(key, ()->counter.getAndIncrement());
            assertFalse(sut.getAsCachedSupplier(key).hasRun());
        }

        assertEquals(0, sut.get("A").intValue());
        assertEquals(1, sut.get("B").intValue());
        //cached
        assertEquals(0, sut.get("A").intValue());
        assertEquals(1, sut.get("B").intValue());
        //keep going
        assertEquals(2, sut.get("C").intValue());
        assertEquals(3, sut.get("D").intValue());

        //last before we reset a cachedSupplier
        assertEquals(4, sut.get("E").intValue());
        assertEquals(5, sut.get("F").intValue());
        //should be reset now...
        assertFalse(sut.getAsCachedSupplier("A").hasRun());
    }

    @Test
    public void lruMapRemoveKeyShouldntGetResetAnymore(){
        LruDelayedComputationMap<String, Integer> sut = new LruDelayedComputationMap<>(26, 5);

        AtomicInteger counter = new AtomicInteger();

        for(int i=0; i< 26; i++){
            char letter = (char)('A' +i);
            String key = Character.toString(letter);
            sut.put(key, ()->counter.getAndIncrement());
            assertFalse(sut.getAsCachedSupplier(key).hasRun());
        }

        assertEquals(0, sut.get("A").intValue());
        assertEquals(1, sut.get("B").intValue());
        //cached
        assertEquals(0, sut.get("A").intValue());
        assertEquals(1, sut.get("B").intValue());
        //keep going
        assertEquals(2, sut.get("C").intValue());
        assertEquals(3, sut.get("D").intValue());

        //last before we reset a cachedSupplier
        assertEquals(4, sut.get("E").intValue());
        CachedSupplier<Integer> removed = sut.removeAsCachedSupplier("A");

        assertEquals(5, sut.get("F").intValue());
        //should not reset
        assertTrue(removed.hasRun());

        assertEquals(6, sut.get("G").intValue());
        //should reset B now
        assertTrue(removed.hasRun());
        assertFalse(sut.getAsCachedSupplier("B").hasRun());
    }
}
