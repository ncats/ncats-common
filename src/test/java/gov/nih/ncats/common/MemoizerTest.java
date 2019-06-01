/*
 *     NCATS-COMMON
 *
 *     Written in 2019 by NIH/NCATS
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
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
