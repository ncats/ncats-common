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

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author dkatzel
 *
 *
 */
public class TestWeakReferenceLRUCache {

    @Test
    public void valueThatIsWeaklyReachableGetsRemoved() throws InterruptedException{
        
        Map<String, Object> weakCache = Caches.<String,Object>builder()
                                                .setLru(true)
                                                .type(Caches.Type.WEAK)
                                                .build();
        weakCache.put("test", createObject());
        assertEquals(1, weakCache.size());
        assertEquals(Collections.singleton("test"),weakCache.keySet());
        assertTrue(weakCache.containsKey("test"));
        System.gc();
        //need to wait for gc to do stuff
        Thread.sleep(500);
        assertEquals(0,weakCache.size());
    }
    
    @Test
    public void removesLeastRecentlyUsedStrongReference() throws InterruptedException{
        Map<String, Object> weakCache = Caches.createWeakReferencedValueLRUCache(2);
        weakCache.put("test1", createObject());
        weakCache.put("test2", createObject());
        weakCache.put("test3", createObject());
        
        assertEquals(2, weakCache.size());
        assertTrue(weakCache.containsKey("test2"));
        assertTrue(weakCache.containsKey("test3"));
        System.gc();
        //need to wait for gc to do stuff
        Thread.sleep(500);
        assertEquals(0,weakCache.size());
        
    }
    
    private Object createObject(){
        return new Object();
    }
}
