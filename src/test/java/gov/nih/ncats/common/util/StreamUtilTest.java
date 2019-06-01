/*
 *     NCATS-COMMON
 *
 *     Written in 2019 by NIH/NCATS
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
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
