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
package gov.nih.ncats.common.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Reads lines from an {@link java.io.InputStream}.  The main
 * differences between {@link LineParser} and other similar JDK classes are: 
 * <ul>
 * <li>may keep track of how many bytes have been read to allow random seek access later</li>
 * <li>some implementations include end of line characters in {@link #nextLine()} call</li>
 * <ul>
 * Most JDK classes chop off these characters and the few classes
 * that could could be configured to include these characters are slow.
 * This class considers a line to be terminated by either '\n',
 * (UNIX format) or '\r\n' (Windows/DOS) or '\r' (Apple family until Mac OS 9). 
 * <p/>
 * This class is not Thread-safe
 * @author dkatzel
 *
 *
 */
public interface LineParser extends Closeable{

    /**
     * Does the inputStream have another line
     * to read.  If there are no more lines to read,
     * then {@link #nextLine()} will return {@code null}.
     * @return {@code true} if there are more lines to be read;
     * {@code false} otherwise.
     * @see #nextLine()
     */
    boolean hasNextLine();

    /**
     * Get the next line (including end of line characters)
     * but without advancing the position into the 
     * stream.  Calling this method multiple times without
     * calling {@link #nextLine()} in between will
     * return the same String.
     * @return the String that will be returned by 
     * {@link #nextLine()} without actually advancing
     * to the next line.
     * 
     */
    String peekLine();
    
    /**
     * Get the number of bytes returned by
     * {@link #nextLine()} so far.
     * The value returned is not affected
     * by how much looking ahead or 
     * buffering has been done to the
     * underlying input stream.
     * @return a number >=0.
     */
    long getPosition();
    
    boolean tracksPosition();

    /**
     * Get the next line (including end of line characters)
     * as a String.
     * @return a the next line; or {@code null} if there are no
     * more lines.
     * @throws IOException if there is a problem reading the next
     * line.
     */
    String nextLine() throws IOException;

    /**
     * 
     * {@inheritDoc}
     */
    void close() throws IOException;

}
