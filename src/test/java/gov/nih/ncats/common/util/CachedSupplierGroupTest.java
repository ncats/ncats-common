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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.Assert.*;
public class CachedSupplierGroupTest {

    @Test
    public void groupOfOne(){
        AtomicInteger counter = new AtomicInteger();

        CachedSupplierGroup group = new CachedSupplierGroup();
        CachedSupplier<Integer> cachedSupplier = group.add( CachedSupplier.of( counter::incrementAndGet));

        assertEquals( 1, cachedSupplier.get().intValue());
        assertEquals( 1, cachedSupplier.get().intValue());
        group.resetAll();

        assertEquals( 2, cachedSupplier.get().intValue());
    }

    @Test
    public void groupOfTwo(){
        AtomicInteger counter1 = new AtomicInteger();

        AtomicInteger counter2 = new AtomicInteger();

        CachedSupplierGroup group = new CachedSupplierGroup();
        CachedSupplier<Integer> cachedSupplier1 = group.add( CachedSupplier.of( counter1::incrementAndGet));

        CachedSupplier<Integer> cachedSupplier2 = group.add( CachedSupplier.of( counter2::incrementAndGet));

        assertEquals( 1, cachedSupplier1.get().intValue());
        assertEquals( 1, cachedSupplier1.get().intValue());
        assertEquals( 1, cachedSupplier2.get().intValue());
        assertEquals( 1, cachedSupplier2.get().intValue());
        group.resetAll();

        assertEquals( 2, cachedSupplier1.get().intValue());
        assertEquals( 2, cachedSupplier2.get().intValue());
    }

    @Test
    public void resetGroupDoesNotResetOutOfGroup(){
        AtomicInteger counter1 = new AtomicInteger();

        AtomicInteger counter2 = new AtomicInteger();

        CachedSupplierGroup group = new CachedSupplierGroup();
        CachedSupplier<Integer> cachedSupplier1 = group.add( CachedSupplier.of( counter1::incrementAndGet));

        CachedSupplier<Integer> cachedSupplier2 = CachedSupplier.of( counter2::incrementAndGet);

        assertEquals( 1, cachedSupplier1.get().intValue());
        assertEquals( 1, cachedSupplier1.get().intValue());
        assertEquals( 1, cachedSupplier2.get().intValue());
        assertEquals( 1, cachedSupplier2.get().intValue());

        group.resetAll();

        assertEquals( 2, cachedSupplier1.get().intValue());
        assertEquals( 1, cachedSupplier2.get().intValue());
    }

    @Test
    public void resetAllCacheStillWorks(){
        AtomicInteger counter1 = new AtomicInteger();

        AtomicInteger counter2 = new AtomicInteger();

        CachedSupplierGroup group = new CachedSupplierGroup();


        AtomicInteger counter3 = new AtomicInteger();

        CachedSupplier<Integer> cachedSupplier1 = group.add( CachedSupplier.of( counter1::incrementAndGet));

        CachedSupplier<Integer> cachedSupplier2 = group.add( CachedSupplier.of( counter2::incrementAndGet));

        CachedSupplier<Integer> cachedSupplier3 = group.add( CachedSupplier.of( counter3::incrementAndGet));


        assertEquals( 1, cachedSupplier1.get().intValue());
        assertEquals( 1, cachedSupplier1.get().intValue());
        assertEquals( 1, cachedSupplier2.get().intValue());
        assertEquals( 1, cachedSupplier2.get().intValue());

        assertEquals( 1, cachedSupplier3.get().intValue());

        CachedSupplier.resetAllCaches();

        assertEquals( 2, cachedSupplier1.get().intValue());
        assertEquals( 2, cachedSupplier2.get().intValue());

        assertEquals( 2, cachedSupplier3.get().intValue());
    }
}
