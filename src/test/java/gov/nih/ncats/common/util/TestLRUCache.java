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
/*
 * Created on Apr 7, 2009
 *
 * @author dkatzel
 */
package gov.nih.ncats.common.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestLRUCache {

    String first = "first";
    String second = "second";
    String third = "third";
    String fourth = "fourth";
    
    protected Map<String, String> sut;
    
    protected Map<String,String> createLRUCache(int size){
        return Caches.createLRUCache(size);
    }
    @Before
    public void setup(){
        sut = createLRUCache(3);
        sut.put(first, first);
        sut.put(second, second);
        sut.put(third, third);
    }
    @Test
    public void initalState(){
        assertEquals(sut.size(), 3);
        assertTrue(sut.containsKey(first));
        assertTrue(sut.containsKey(second));
        assertTrue(sut.containsKey(third));
        
    }
    @Test
    public void insertOverflowShouldRemoveEldest(){
        sut.put(fourth, fourth);
        assertEquals(sut.size(), 3);
        assertTrue(sut.containsKey(second));
        assertTrue(sut.containsKey(third));
        assertTrue(sut.containsKey(fourth));
        
        assertEquals(second,sut.get(second));
        assertEquals(third,sut.get(third));
        assertEquals(fourth,sut.get(fourth));
    }
    @Test
    public void remove(){
        sut.remove(third);
        sut.put(fourth, fourth);
        assertTrue(sut.containsKey(first));
        assertTrue(sut.containsKey(second));
        assertTrue(sut.containsKey(fourth));
        
        assertEquals(first,sut.get(first));
        assertEquals(second,sut.get(second));
        assertEquals(fourth,sut.get(fourth));
    }
}
