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

package gov.nih.ncats.common.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

/**
 * {@code ProcessStreamReader}
 * is a  class that takes an {@link InputStream}
 * from a {@link Process} (usually {@link Process#getInputStream()}
 * and {@link Process#getErrorStream()} ) and reads their contents
 * in a background thread.  Depending on the native
 * system {@link Process}
 * being executed is running on and the amount of data output
 * to STDOUT and STDERR, not draining these streams
 * concurrently while the {@link Process} is executing
 * could
 * block or even cause deadlock (see {@link Process} javadoc more more details).
 * @author dkatzel
 * @see Process
 *
 */
public final class ProcessStreamReader implements Runnable{

    private final InputStream in;
    private StringBuilder buffer;
    private final Charset charSet;
    /**
     * {@link CountDownLatch} to tell us
     * if we have read the entire contents
     * of the Stream.
     * @see #await()
     * @see #run()
     */
    private final CountDownLatch latch = new CountDownLatch(1);

    /**
     * Create a new {@link ProcessStreamReader} instance
     * using the given {@link InputStream} and assume
     * the {@link Charset} is UTF-8.
     * The contents of the {@link InputStream} will be read
     * but not saved; {@link #getCurrentContentsAsString()}
     * will return null.
     * @param in the {@link InputStream} to read.
     * @return a new {@link ProcessStreamReader} instance.
     */
    public static ProcessStreamReader createAndIgnoreOutput(InputStream in){
        return createAndIgnoreOutput(in, IOUtil.UTF_8);
    }
    /**
     * Create a new {@link ProcessStreamReader} instance
     * using the given {@link InputStream}.
     * The contents of the {@link InputStream} will be read
     * but not saved; {@link #getCurrentContentsAsString()}
     * will return null.
     * @param in the {@link InputStream} to read.
     * @param charSet the {@link Charset} the data in the {@link InputStream}
     * is encoded with.
     * @return a new {@link ProcessStreamReader} instance.
     */
    public static ProcessStreamReader createAndIgnoreOutput(InputStream in, Charset charSet){
        ProcessStreamReader reader = new ProcessStreamReader(in, charSet,false);
        new Thread(reader).start();
        return reader;
    }
    /**
     * Create a new {@link ProcessStreamReader} instance
     * using the given {@link InputStream} and assume
     * the {@link Charset} is UTF-8.
     * The contents of the {@link InputStream}
     * can be retrieved as a String using {@link #getCurrentContentsAsString()}.
     * @param in the {@link InputStream} to read.
     * @return a new {@link ProcessStreamReader} instance.
     */
    public static ProcessStreamReader create(InputStream in){
        return create(in, IOUtil.UTF_8);
    }
    /**
     * Create a new {@link ProcessStreamReader} instance
     * using the given {@link InputStream} and {@link Charset}.
     * The contents of the {@link InputStream}
     * can be retrieved as a String using {@link #getCurrentContentsAsString()}.
     * @param in the {@link InputStream} to read.
     * @param charSet the {@link Charset} the data in the {@link InputStream}
     * is encoded with.
     * @return a new {@link ProcessStreamReader} instance.
     */
    public static ProcessStreamReader create(InputStream in, Charset charSet){
        ProcessStreamReader reader = new ProcessStreamReader(in, charSet,true);
        new Thread(reader).start();
        return reader;
    }

    private ProcessStreamReader(InputStream in, Charset charSet, boolean captureOutput) {
        this.in = in;
        this.charSet = charSet;
        if(captureOutput){
            buffer = new StringBuilder();
        }else{
            buffer=null;
        }
    }
    /**
     * Has any data been written to this buffer yet.
     * @return {@code true} if no data has been written
     * to this buffer; {@code false} otherwise.
     */
    public boolean isEmpty(){
        return buffer.length() ==0;
    }
    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, charSet));
        String line =null;
        try {
            while((line = br.readLine()) !=null){
                if(buffer !=null){
                    buffer.append(line).append('\n');
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        latch.countDown();
    }
    /**
     * Causes the current thread to block
     * until the entire contents of
     * the Process Stream has been read.
     * <p> If all of the data has been read,
     * then this method will return immediately.
     * </p>
     *
     * <p>If there is still more data in the stream, then the current
     * thread becomes disabled for thread scheduling purposes and lies
     * dormant until one of two things happen:
     * <ul>
     * <li>The entire stream has been read; or
     * <li>Some other thread {@linkplain Thread#interrupt interrupts}
     * the current thread.
     * </ul>
     *
     * <p>If the current thread:
     * <ul>
     * <li>has its interrupted status set on entry to this method; or
     * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
     * </ul>
     * then {@link InterruptedException} is thrown and the current thread's
     * interrupted status is cleared.
     *
     * @throws InterruptedException if the current thread is interrupted
     *         while waiting
     */
    public void await() throws InterruptedException{
        latch.await();
    }
    /**
     * Return the contents of the stream read
     * so far as a String; or null if output was
     * ignored.
     * @return contents of the stream read
     * so far as a String; or null if output was
     * ignored.
     */
    public String getCurrentContentsAsString(){
        if(buffer ==null){
            return null;
        }
        return buffer.toString();
    }
}


