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

package gov.nih.ncats.common;


import gov.nih.ncats.common.yield.Yield;
import gov.nih.ncats.common.yield.YieldingIterator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class YieldTest {

    private SideEffects ignoreSideEffects = sideEffect -> {};

    static Yield<String> fooBar(SideEffects sideEffects) {
        return Yield.create(yield -> {
            sideEffects.sideEffect(1);
            yield.returning("foo");
            sideEffects.sideEffect(2);
            yield.returning("bar");
            sideEffects.sideEffect(3);
        });
    }

    static Yield<Integer> oneToTenBreakOnSix(SideEffects sideEffects) {
        return yield -> {
            for (int i = 1; i < 10; i++) {
                sideEffects.sideEffect(i);
                if (i == 6){
                    yield.breaking();
                }
                yield.returning(i);
            }
        };
    }

    @Test
    public void shouldHaveExpectedValues() {
        List<String> results = new ArrayList<>();

        for (String result : fooBar(ignoreSideEffects)) {
            results.add(result);
        }

        assertEquals(asList("foo","bar"), results);
    }

    @Test
    public void stream() {
        List<String> results = fooBar(ignoreSideEffects).stream()
                                        .collect(Collectors.toList());


        assertEquals(asList("foo","bar"), results);
    }

    @Test
    public void shouldPerformSideEffectsInOrder() {
        SideEffects sideEffects = mock(SideEffects.class);

        Iterable<String> foos = fooBar(sideEffects);
        verifyZeroInteractions(sideEffects);

        int sideEffectNumber = 1;
        for  (String foo : foos) {
            verify(sideEffects).sideEffect(sideEffectNumber++);
            verifyNoMoreInteractions(sideEffects);
        }
        verify(sideEffects).sideEffect(sideEffectNumber++);
        verifyNoMoreInteractions(sideEffects);

    }

    @Test
    public void shouldBreak() {
        ArrayList<Integer> results = new ArrayList<>();
        for (Integer number : oneToTenBreakOnSix(ignoreSideEffects)) {
            results.add(number);
        }
        assertEquals(asList(1,2,3,4,5), results);
    }


    @Test
    public void shouldInvokeSideEffectsInCorrectOrder() {
        SideEffects sideEffects = mock(SideEffects.class);

        Iterable<Integer> numbers = oneToTenBreakOnSix(sideEffects);
        verifyNoInteractions(sideEffects);

        int sideEffectNumber = 1;
        for  (Integer i : numbers) {
            verify(sideEffects).sideEffect(sideEffectNumber++);
            verifyNoMoreInteractions(sideEffects);
        }
        verify(sideEffects).sideEffect(sideEffectNumber++);
        verifyNoMoreInteractions(sideEffects);
    }

    @Test
    public void iteratorDoesNotRequireHasNextToAdvance() {
        Iterable<String> strings = fooBar(ignoreSideEffects);
        Iterator<String> iterator = strings.iterator();
        assertEquals("foo", iterator.next());
        assertEquals("bar", iterator.next());
    }

    @Test
    public void iteratoreShouldBeStateless() {
        Iterable<String> strings = fooBar(ignoreSideEffects);
        assertEquals("foo", strings.iterator().next());
        assertEquals("foo", strings.iterator().next());
    }


    @Test
    public void autoCloseInfiniteIterator() {

        try (YieldingIterator<Integer> positiveIntegers = positiveIntegers().iterator()) {
            assertEquals(Integer.valueOf(1), positiveIntegers.next());
            assertEquals(Integer.valueOf(2), positiveIntegers.next());
            assertEquals(Integer.valueOf(3), positiveIntegers.next());
        }
    }

    @Test
    public void hasNextWorks() {
        Yield<Integer> y = yield->{
            yield.returning(1);
            yield.returning(2);
            yield.returning(3);
        };
        try (YieldingIterator<Integer> iter =y.iterator()) {
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(1), iter.next());
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(2), iter.next());
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(3), iter.next());
            assertFalse(iter.hasNext());
        }
    }

    @Test
    public void callHasNextMultipleTimesInRowWorksAsExpected() {
        Yield<Integer> y = yield->{
            yield.returning(1);
            yield.returning(2);
            yield.returning(3);
        };
        try (YieldingIterator<Integer> iter =y.iterator()) {
            assertTrue(iter.hasNext());
            assertTrue(iter.hasNext());
            assertTrue(iter.hasNext());
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(1), iter.next());
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(2), iter.next());
            assertTrue(iter.hasNext());
            assertEquals(Integer.valueOf(3), iter.next());
            assertFalse(iter.hasNext());
        }
    }
    @Test
    public void autoCloseInfiniteStream() {
        List<Integer> actual;
        try(Stream<Integer> stream = positiveIntegers().stream()){

            actual =  stream.limit(3)
                            .collect(Collectors.toList());
        }

        assertEquals(Arrays.asList(1,2,3), actual);
    }

    public static Yield<Integer> positiveIntegers() {
        return yield -> {
            int i = 0;
            while (true) yield.returning(++i);
        };
    }

    interface SideEffects {
        void sideEffect(int sequence);
    }

}
