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

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by katzelda on 5/31/19.
 */
public class MemoizerTest {



    @Test
    public void callOnlyOnce() throws Exception{
        Memoizer<Integer, Integer> memoizer = new Memoizer<>();
        AtomicInteger count = new AtomicInteger();

        int result=0;
        for(int j=0; j<10; j++) {
            result = memoizer.computeIfAbsent(5, i -> {
                count.incrementAndGet();
                return i * 2;
            });
        }
        assertEquals(10, result);
        assertEquals(1, count.get());
    }
}
