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

import gov.nih.ncats.common.stream.StreamUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



public class StreamUtilTest {



    @Test
    public void ensureIteatorStreamUtilForListIsEquivalentToStreamFromList(){
        List<String> names = new ArrayList<String>();
        names.add("ABC");
        names.add("DEF");
        names.add("ABC");
        names.add("1234");
        Iterator<String> it=names.iterator();

        assertEquals(names, StreamUtil.forIterator(it).collect(Collectors.toList()));

    }

    @Test
    public void ensureGeneratorUtilForListIsEquivalentToStreamFromList(){
        List<String> names = new ArrayList<String>();
        names.add("ABC");
        names.add("DEF");
        names.add("ABC");
        names.add("1234");
        Iterator<String> it=names.iterator();

        assertEquals(names, StreamUtil.forGenerator(()->(it.hasNext())?Optional.of(it.next()):Optional.empty()).collect(Collectors.toList()));

    }

    @Test
    public void ensureNullableGeneratorUtilForListIsEquivalentToStreamFromList(){
        List<String> names = new ArrayList<String>();
        names.add("ABC");
        names.add("DEF");
        names.add("ABC");
        names.add("1234");
        Iterator<String> it=names.iterator();

        assertEquals(names, StreamUtil.forNullableGenerator(()->(it.hasNext())?it.next():null).collect(Collectors.toList()));

    }
    @Test
    public void ensureEnumerationUtilForListIsEquivalentToStreamFromList(){
        List<String> names = new ArrayList<String>();
        names.add("ABC");
        names.add("DEF");
        names.add("ABC");
        names.add("1234");
        Enumeration<String> en=EnumFromIterator.of(names.iterator());
        assertEquals(names, StreamUtil.forEnumeration(en).collect(Collectors.toList()));
    }

    public static class EnumFromIterator<T> implements Enumeration<T>{

        Iterator<T> it;
        public EnumFromIterator(Iterator<T> it){
            this.it=it;
        }

        @Override
        public boolean hasMoreElements() {
            return it.hasNext();
        }

        @Override
        public T nextElement() {
            return it.next();
        }

        public static <T> Enumeration<T> of(Iterator<T> it){
            return new EnumFromIterator(it);
        }

    }

}
