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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


/**
 * {@code MagicNumberInputStream} is an
 * {@link InputStream} implementation
 * that reads ahead and saves the magic number
 * so clients can determine a course of
 * action by peeking at the magic number
 * before they have to read any bytes 
 * from the inputStream. 
 * 
 * @author dkatzel
 *
 *
 */
public final class MagicNumberInputStream extends InputStream{

    private final InputStream in;
    private final byte[] magicNumber;
    private int numberOfBytesRead=0;
    /**
     * Convenience constructor which defaults to a magic number
     * size of 4 bytes.  This is the same as
     * {@link #MagicNumberInputStream(InputStream, int) new MagicNumberInputStream(in,4)}.
     * @param in the InputStream to wrap.
     * @throws IOException if there is a problem reading 
     * the inputStream for {@code sizeOfMagicNumber} bytes.
     * @see #MagicNumberInputStream(InputStream, int)
     */
    public MagicNumberInputStream(InputStream in) throws IOException{
        this(in,4);
    }
    /**
     * Convenience constructor which defaults to a magic number
     * size of 4 bytes. 
     * @param file the File to read as an InputStream.
     * @throws IOException if there is a problem reading 
     * the inputStream for {@code sizeOfMagicNumber} bytes.
     * @see #MagicNumberInputStream(InputStream, int)
     */
    public MagicNumberInputStream(File file) throws IOException{
        this(new BufferedInputStream(new FileInputStream(file)),4);
    }
    /**
     * Wraps the given {@link InputStream} and reads the 
     * first {@code sizeOfMagicNumber} bytes as the magic number. 
     * Will block until the entire magic number has been read.
     * @param in the InputStream to wrap.
     * @param sizeOfMagicNumber the number of bytes the magic number
     * should be.
     * @throws IOException if there is a problem reading 
     * the inputStream for {@code sizeOfMagicNumber} bytes.
     */
    public MagicNumberInputStream(InputStream in,int sizeOfMagicNumber) throws IOException {
        magicNumber =IOUtil.toByteArray(in, sizeOfMagicNumber);
        this.in = in;
    }
    /**
     * Gets the entire magic number without advancing the
     * {@link InputStream}.  If the stream has already read past
     * the magic number, it is cached for later retrieval.
     * @return the magic number of this inputStream as a byte array.
     */
    public byte[] peekMagicNumber(){
        return Arrays.copyOf(magicNumber, magicNumber.length);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public synchronized int read() throws IOException {
        if(numberOfBytesRead< magicNumber.length){            
            return magicNumber[numberOfBytesRead++];
        }
        return in.read();
    }

}

