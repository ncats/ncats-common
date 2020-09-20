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

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by katzelda on 5/17/16.
 */
public final class IOUtil {

    private IOUtil(){
        //can not instantiate
    }


    public static Charset UTF_8 = Charset.forName("UTF-8");

    public static void deleteRecursivelyQuitely(File dir) {
        try {
            deleteRecursively(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printDirectoryStructure(File dir) throws IOException{
        if(!dir.exists()){
            return;
        }
        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {


            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir,
                                                      IOException exc)
                    throws IOException{

                FileVisitResult fvr= super.postVisitDirectory(dir, exc);
                File dirfile=dir.toFile();
                try{
                    System.out.println("Visiting  :" + dirfile.getAbsolutePath());
                }catch(Exception e){
                    System.out.println("unable to visit:" + e.getMessage());
                }
                return fvr;
            }

        });
    }

    /**
     * Create this new directory and any parent directories as needed.
     * @param dir the directory to create; if {@code null} or already exists,
     *            then will not
     *            create anything.
     * @throws IOException if there are any problems creating this directory.
     */
    public static void mkdirs(File dir) throws IOException{
        if(dir ==null){
            return;
        }
        //use new Java 7 method
        //which will throw a meaningful IOException if there are permission or file problems
        //and checks if already exists and doesn't do anything if it already exists.
        Files.createDirectories(dir.toPath());
    }

    public static void deleteRecursively(File dir) throws IOException {
        if(!dir.exists()){
            return;
        }
        Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {


            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                //we've have NFS problems where there are lock
                //objects that we can't delete
                //should be safe to keep them and delete every other file.
                if(		!file.toFile().getName().startsWith(".nfs")
                    //&& !file.toFile().getName().endsWith(".cfs")
                        ){
                    //use new delete method which throws IOException
                    //if it can't delete instead of returning flag
                    //so we will know the reason why it failed.
                    try{
                        //System.out.println("Deleting:" + file);
                        Files.delete(file);
                    }catch(Exception e){
                        System.out.println(e.getMessage());
                    }
                }


                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir,
                                                      IOException exc)
                    throws IOException{

                FileVisitResult fvr= super.postVisitDirectory(dir, exc);
                File dirfile=dir.toFile();
                try{
                    //System.out.println("Deleting:" + dirfile);
                    Files.delete(dir);
                }catch(Exception e){
                    System.out.println("unable to delete:" + e.getMessage());
                }
                return fvr;
            }

        });
    }

    /**
     * Close the given closeable with supress any errors that are
     * thrown.
     * @param c the closeable to close; if null, then do nothing.
     */
    public static void closeQuietly(Closeable c) {
        if(c ==null){
            return;
        }
        try {
            c.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedOutputStream newBufferedOutputStream(File outputFile) throws IOException {
        File parent = outputFile.getParentFile();
        if(parent !=null){
            Files.createDirectories(parent.toPath());
        }
        return new BufferedOutputStream(new FileOutputStream(outputFile));
    }


    private static final int EOF = -1;

    public static void closeQuitely(Closeable c){
        if(c ==null){
            return;
        }
        try{
            c.close();
        }catch(IOException e){
            //ignore
        }
    }
    /**
     * Copy the contents of the given inputStream to the given
     * outputStream.  This method buffers internally so there is no
     * need to use a {@link BufferedInputStream}.  This method
     * <strong>does not</strong> close either stream
     * after processing.
     * @param in the inputStream to read.
     * @param out the outputStream to write to.
     * @return the number of bytes that were copied.
     * @throws IOException if there is a problem reading or writing
     * the streams.
     * @throws NullPointerException if either stream is null.
     */
    public static long copy(InputStream in, OutputStream out) throws IOException{
        byte[] buf = new byte[2048];
        long numBytesCopied=0;
        while(true){
            int numBytesRead =in.read(buf);
            if(numBytesRead ==EOF){
                break;
            }
            numBytesCopied+=numBytesRead;
            out.write(buf, 0, numBytesRead);
            out.flush();
        }
        return numBytesCopied;
    }
    /**
     * Copy the contents of the given {@link InputStream}
     * and return it as a byte[].
     * @param input the inputStream to convert into a byte[].
     * This stream is not closed when the method finishes.
     * @return a new byte array instance containing all the bytes
     * from the given inputStream.
     * @throws IOException if there is a problem reading the Stream.
     */
    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);

        return output.toByteArray();
    }
    /**
     * Copy the numberOfBytesToRead of the given {@link InputStream}
     * and return it as a byte[].
     *
     * @param in the inputStream to convert into a byte[].
     * This stream is not closed when the method finishes.
     * @param numberOfBytesToRead the number of bytes to read from the stream;
     * if there aren't enough bytes, then this method will block until
     * more bytes are available or until the stream reaches end of file
     * (which will cause an IOException to be thrown).
     *
     * @return a new byte array instance containing all the bytes
     * from the given inputStream.
     * @throws EOFException if the end of the file is reached before
     * the given number of bytes.
     * @throws IOException if there is a problem reading the inputStream.
     */
    public static byte[] toByteArray(InputStream in, int numberOfBytesToRead) throws IOException {
        byte[] array = new byte[numberOfBytesToRead];
        blockingRead(in,array,0,numberOfBytesToRead);

        return array;
    }


    /**
     * Reads {@code buf.length} bytes of the given inputStream and
     * puts them into the given byte array starting at the given offset.
     * Will keep reading until length number of bytes have been read (possibly blocking).
     * This is the same as {@link #blockingRead(InputStream, byte[], int, int) blockingRead(in,buf,0, buf.length)}
     * @param in the inputStream to read; can not be null.
     * @param buf the byte array to write the data from the stream to; can not be null.
     * @throws EOFException if EOF is unexpectedly reached.
     * @throws IOException if there is a problem reading the stream.
     * @throws NullPointerException if either inputStream  or buf are null.
     * @throws IllegalArgumentException if either offset  or length are negative.
     * @see #blockingRead(InputStream, byte[], int, int)
     */
    public static void blockingRead(InputStream in, byte[] buf) throws IOException{
        blockingRead(in, buf, 0, buf.length);
    }


    /**
     * Reads up to length number of bytes of the given inputStream and
     * puts them into the given byte array starting at the given offset.
     * Will keep reading until length number of bytes have been read (possibly blocking).
     * @param in the inputStream to read; can not be null.
     * @param buf the byte array to write the data from the stream to; can not be null.
     * @param offset the offset into the byte array to begin writing
     * bytes to must be {@code >= 0}.
     * @param length the maximum number of bytes to read, must be {@code >= 0}.
     * This number of bytes will be read unless the inputStream ends prematurely
     * (which will throw an IOException).
     * @throws EOFException if EOF is unexpectedly reached.
     * @throws IOException if there is a problem reading the stream.
     * @throws NullPointerException if either inputStream  or buf are null.
     * @throws IllegalArgumentException if either offset  or length are negative.
     */
    public static void blockingRead(InputStream in, byte[] buf, int offset, int length) throws IOException{
        checkBlockingReadInputsAreOK(in, buf, offset, length);
        int currentBytesRead=0;
        int totalBytesRead=0;
        while((currentBytesRead =in.read(buf, offset+totalBytesRead, length-totalBytesRead))>0){
            totalBytesRead+=currentBytesRead;
            if(totalBytesRead == length){
                break;
            }
        }
        if(currentBytesRead ==EOF){
            throw new EOFException(String.format("end of file after only %d bytes read (expected %d)",totalBytesRead,length));
        }
    }

    /**
     * Skip <code>numberOfBytes</code> in the {@link InputStream}
     * and block until those bytes have been skipped. {@link InputStream#skip(long)}
     * will only skip as many bytes as it can without blocking.
     * @param in InputStream to skip.
     * @param numberOfBytes number of bytes to skip.
     * @throws IOException if there is a problem reading the inputstream
     * or if the end of file is reached before the number of bytes to skip
     * has been reached.
     */
    public static void blockingSkip(InputStream in, long numberOfBytes) throws IOException{

        long leftToSkip = numberOfBytes;
        while(leftToSkip >0){
            //need to do a read() to see if we
            //are at EOF yet. otherwise we loop forever
            //since skip will return 0.
            //this also is the reason for the -1 and +1
            //sprinkled around the leftToSkip computation.
            int value =in.read();
            if(value == EOF){
                throw new IOException("end of file reached before entire block was skipped");
            }
            leftToSkip -= in.skip(leftToSkip-1) +1;
        }

    }
    /**
     * Checks to make sure the given file is readable
     * and throws an descriptive IOException if it's not.
     *
     * @param f the File to verify; can not be null
     * @throws NullPointerException if f is null.
     * @throws FileNotFoundException if the file does not exist.
     * @throws IOException if the file is not readable.
     */
    public static void verifyIsReadable(File f) throws IOException {
        if (f == null) {
            throw new NullPointerException("file can not be null");
        }
        if (!f.exists()) {
            throw new FileNotFoundException("file must exist : " + f.getAbsolutePath());
        }
        if (!f.canRead()) {
            throw new IOException("file is not readable: " + f.getAbsolutePath());
        }
    }
    private static void checkBlockingReadInputsAreOK(InputStream in,
                                                     byte[] buf, int offset, int length) {
        if(buf ==null){
            throw new NullPointerException("byte array can not be null");
        }
        if(in ==null){
            throw new NullPointerException("inputstream can not be null");
        }
        if(offset <0){
            throw new IllegalArgumentException("offset must be >= 0");
        }
        if(length <0){
            throw new IllegalArgumentException("length must be >= 0");
        }
    }
}
