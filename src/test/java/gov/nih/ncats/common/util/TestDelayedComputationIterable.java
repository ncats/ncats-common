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
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
public class TestDelayedComputationIterable {

    @Rule
    public FibonacciComputerRule fibonacciComputerRule = new FibonacciComputerRule();

    @Test
    public void buildUsingSameGenerator(){
        Iterable<BigInteger> sut = DelayedComputationIterables.builderUsingGenerator(fibonacciComputerRule::fibonacci, 10)
                                    .add(5)
                                    .add(10)
                                    .add(20)
                                    .add(35)
                                    .build();

        Iterator<BigInteger> iter = sut.iterator();
        assertTrue(iter.hasNext());

        assertEquals(BigInteger.valueOf(5), iter.next());
        assertEquals(BigInteger.valueOf(55), iter.next());

        assertEquals(BigInteger.valueOf(6765), iter.next());
        assertEquals(BigInteger.valueOf(9227465), iter.next());
        assertFalse(iter.hasNext());
    }
    @Test
    public void buildUsingSameGeneratorCanBeCalled2x(){
        Iterable<BigInteger> sut = DelayedComputationIterables.builderUsingGenerator(fibonacciComputerRule::fibonacci, 10)
                .add(5)
                .add(10)
                .add(20)
                .add(35)
                .build();

        Iterator<BigInteger> iter = sut.iterator();
        assertTrue(iter.hasNext());

        assertEquals(BigInteger.valueOf(5), iter.next());
        assertEquals(BigInteger.valueOf(55), iter.next());

        assertEquals(BigInteger.valueOf(6765), iter.next());
        assertEquals(BigInteger.valueOf(9227465), iter.next());
        assertFalse(iter.hasNext());

        iter = sut.iterator();
        assertTrue(iter.hasNext());

        assertEquals(BigInteger.valueOf(5), iter.next());
        assertEquals(BigInteger.valueOf(55), iter.next());

        assertEquals(BigInteger.valueOf(6765), iter.next());
        assertEquals(BigInteger.valueOf(9227465), iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void buildUsingSameGeneratorStream(){
        Stream<BigInteger> sut = DelayedComputationIterables.builderUsingGenerator(fibonacciComputerRule::fibonacci, 10)
                .add(5)
                .add(10)
                .add(20)
                .add(35)
                .buildAsStream();

        assertEquals(Arrays.asList(BigInteger.valueOf(5),
                BigInteger.valueOf(55),
                BigInteger.valueOf(6765),
                BigInteger.valueOf(9227465)
                ),
                sut.collect(Collectors.toList()));


    }
}
