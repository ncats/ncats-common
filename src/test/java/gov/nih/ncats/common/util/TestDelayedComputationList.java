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
import org.junit.Rule;
import org.junit.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class TestDelayedComputationList {
    @Rule
    public FibonacciComputerRule fibonacciComputerRule = new FibonacciComputerRule();

    @Test
    public void callGets(){
        DelayedComputationList<BigInteger> sut = new DelayedComputationList<>();

        add(sut,5);
        add(sut,10);


        assertEquals(BigInteger.valueOf(5), sut.get(0));
        assertEquals(BigInteger.valueOf(55), sut.get(1));
    }

    @Test
    public void callIterator(){
        DelayedComputationList<BigInteger> sut = new DelayedComputationList<>();

        add(sut,5);
        add(sut,10);

        Iterator<BigInteger> iter = sut.iterator();
        assertEquals(BigInteger.valueOf(5), iter.next());
        assertEquals(BigInteger.valueOf(55), iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void callStream(){
        DelayedComputationList<BigInteger> sut = new DelayedComputationList<>();

        add(sut,5);
        add(sut,10);

        Iterator<BigInteger> iter = sut.stream().collect(Collectors.toList()).iterator();
        assertEquals(BigInteger.valueOf(5), iter.next());
        assertEquals(BigInteger.valueOf(55), iter.next());
        assertFalse(iter.hasNext());
    }

    private void add(DelayedComputationList<BigInteger> sut, int value){
        sut.add(()->fibonacciComputerRule.fibonacci(value));
    }

}
