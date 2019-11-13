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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Objects;

/**
 * Wraps a BufferedReader so that it can "unread" lines.  Unread lines
 * are pushed into a LIFO stack.
 *
 * This class is NOT threadsafe.
 *
 * @author katzelda
 */
public class PushbackBufferedReader implements AutoCloseable{

    private final BufferedReader reader;

    private final ArrayDeque<String> nextLine = new ArrayDeque<>();

    /**
     * Create a new PushbackBufferedReader wrapping the given {@link BufferedReader}.
     * @param reader the reader to wrap; can not be null.
     * @throws NullPointerException if reader is null.
     */
    public PushbackBufferedReader(BufferedReader reader) {
        this.reader = Objects.requireNonNull(reader);
    }

    /**
     * Read the next line.  This will either be the most
     * recently pushed back line OR the next line in the wrapped
     * BufferedReader.
     * @return the next line as a String or {@code null}
     * if there are no more lines in the wrapped reader.
     * @throws IOException if there is a problem reading the next line.
     */
    public String readLine() throws IOException {
        if(nextLine.isEmpty()){
            return reader.readLine();
        }
        return nextLine.pop();

    }

    /**
     * Push the given line back into the reader.
     * If this method is called multiple times
     * the last line pushed back is the first line
     * returned by the next {@link #readLine()} (LIFO).
     * @param line the line to push back; can not be null.
     *
     * @throws NullPointerException if line is null.
     */
    public void pushBack(String line){

        nextLine.push(line);
    }

    /**
     * Read the next line and then push it back onto the stack.
     * This is the same as:
     * <pre>
     *     {@code
     *         String line = readLine();
     *         if(line !=null) {
     *             pushBack(line);
     *         }
     *         return line;
     *         }
     * </pre>
     * @return the next line or {@code null} if EOF.
     * @throws IOException there is a problem reading then next line.
     */
    public String peekLine() throws IOException{
        String line = readLine();
        //line could be null if we happen to do a peek at EOF
        if(line !=null) {
            pushBack(line);
        }
        return line;
    }

    @Override
    public void close() throws IOException {
        reader.close();
        nextLine.clear();
    }
}
