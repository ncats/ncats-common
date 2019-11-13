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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;
public class PushbackBufferedReaderTest {

    @Test
    public void pushbackWithNoPushes() throws IOException {
        String s = "foo\nbar\nbaz";
        BufferedReader reader = createBufferedReaderFor(s);
        PushbackBufferedReader sut = new PushbackBufferedReader(createBufferedReaderFor(s));

        String expected;
        while( (expected = reader.readLine()) !=null){
            assertEquals(expected, sut.readLine());
        }
        assertNull(sut.readLine());
    }

    @Test
    public void pushback() throws IOException {
        String s = "foo\nbar\nbaz";
        PushbackBufferedReader sut = new PushbackBufferedReader(createBufferedReaderFor(s));

        assertEquals("foo", sut.readLine());
        sut.pushBack("foo2");

        assertEquals("foo2", sut.readLine());
        assertEquals("bar", sut.readLine());
        assertEquals("baz", sut.readLine());
        assertNull(sut.readLine());
    }

    @Test
    public void pushbackConsecutiveTimes() throws IOException {
        String s = "foo\nbar\nbaz";
        PushbackBufferedReader sut = new PushbackBufferedReader(createBufferedReaderFor(s));

        assertEquals("foo", sut.readLine());
        sut.pushBack("foo2");
        sut.pushBack("foo3");

        assertEquals("foo3", sut.readLine());
        assertEquals("foo2", sut.readLine());
        assertEquals("bar", sut.readLine());
        assertEquals("baz", sut.readLine());
        assertNull(sut.readLine());
    }

    @Test
    public void pushbackNonConsecutiveTimes() throws IOException {
        String s = "foo\nbar\nbaz";
        PushbackBufferedReader sut = new PushbackBufferedReader(createBufferedReaderFor(s));

        assertEquals("foo", sut.readLine());
        sut.pushBack("foo2");


        assertEquals("foo2", sut.readLine());
        assertEquals("bar", sut.readLine());

        sut.pushBack("foo3");
        assertEquals("foo3", sut.readLine());

        assertEquals("baz", sut.readLine());
        assertNull(sut.readLine());
    }

    private BufferedReader createBufferedReaderFor(String s) {
        return new BufferedReader(new StringReader(s));
    }
}
