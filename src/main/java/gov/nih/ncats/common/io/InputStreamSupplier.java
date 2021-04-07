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


import gov.nih.ncats.common.functions.ThrowableSupplier;
import gov.nih.ncats.common.sneak.Sneak;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;


/**
 * A Supplier function that can create multiple
 * new {@link InputStream}s from the same source file.
 * Different implementations may be able to handle
 * different file encodings or compressions.
 * 
 *
 */
@FunctionalInterface
public interface InputStreamSupplier {
    /**
     * Create a new {@link InputStream} that starts
     * at the beginning of the file.
     * 
     * @return a new {@link InputStream}; should
     *          never be null but might not have any bytes to read.
     *          
     * @throws IOException if there is a problem creating the {@link InputStream}.
     */
    InputStream get() throws IOException;
  
   
    
    /**
     * Get the {@link File} object
     * that is the source of this inputStream.
     * 
     * @return an {@link Optional} File that may be empty
     * if the file source is not known.
     *
     */
    default Optional<File> getFile(){
        return Optional.empty();
    }
    /**
     * Create a new {@link InputStreamSupplier} for the given {@link File}
     * and try to correctly automatically decompress it.
     * 
     * The first few bytes of the given file are parsed to see if 
     * it is one of the few compressed file formats that have
     * built-in JDK InputStream implementations.
     * 
     * Currently the only supported formats are:
     * <ul>
     * <li>uncompressed</li>
     * <li>zip - single entry only</li>
     * <li>gzip</li>
     * </ul>
     * 
     * If the file is not one of these types, then it is assumed
     * to be uncompressed
     * will be returned.
     * 
     * File encoding is determined by the actual contents
     * of the file.  The file name is not examined at all
     * so input files may use any file name extension conventions without
     * worrying about this method misinterpreting.
     * 
     * 
     * @param f the {@link File} object to create an {@link InputStreamSupplier} for;
     * can not be null, must exist,must be readable and should continue to exist for the lifetime
     * of this {@link InputStreamSupplier}.
     * 
     * @return a new {@link InputStreamSupplier}; will never be null.
     * @throws IOException if there is a problem reading this file.
     * @throws NullPointerException if f is null.
     */
    static InputStreamSupplier forFile(File f) throws IOException{
       IOUtil.verifyIsReadable(f);
       
       //check that file isn't empty
       //if the file is empty then there's no magic number
       if(f.length() ==0){
    	   return new RawFileInputStreamSupplier(f);
       }
       
       byte[] magicNumber;
       try(MagicNumberInputStream magicNumInputStream = new MagicNumberInputStream(f)){
           magicNumber= magicNumInputStream.peekMagicNumber();
       }
       
       if (magicNumber[0] == (byte)0x50 && magicNumber[1] == (byte)0x4B && magicNumber[2] == (byte)0x03 && magicNumber[3]== (byte) 0x04){
           //zipped
           return new BasicZipInputStreamSupplier(f);
       }
       if( magicNumber[0] == (byte) 0x1F && magicNumber[1] == (byte)0x8B){
           //gzip
           return new GZipInputStreamSupplier(f);
       }
       
        return new RawFileInputStreamSupplier(f);
    }

    /**
     * Create a new single use {@link InputStreamSupplier} for the given {@link InputStream}
     * and try to correctly automatically decompress it.
     *
     * The first few bytes of the given file are parsed to see if
     * it is one of the few compressed file formats that have
     * built-in JDK InputStream implementations.
     *
     * Currently the only supported formats are:
     * <ul>
     * <li>uncompressed</li>
     * <li>zip - single entry only</li>
     * <li>gzip</li>
     * </ul>
     *
     * If the data is not one of these types, then it is assumed
     * to be uncompressed
     * will be returned.
     *
     * Encoding is determined by the actual contents
     * of the file.
     *
     *
     * @param in the {@link InputStream} object to create an {@link InputStreamSupplier} for;
     * can not be null.  The client code should not call any methods on this inputstream
     *           after passing it to this method as this inputstream will have bytes read and then unread
     *           from it while trying to determine the compression format. Any concurrent reads from this inputstream
     *           will mess that up.
     *
     * @return a new {@link InputStreamSupplier}; will never be null.
     * @throws IOException if there is a problem reading this file.
     * @throws NullPointerException if f is null.
     *
     * @Since 0.3.6
     */
    static InputStreamSupplier forInputStream(InputStream in) throws IOException{
        byte[] magicNumber = new byte[4];
        PushbackInputStream pushbackInputStream = new PushbackInputStream(in, 4);
        int bytesRead = pushbackInputStream.read(magicNumber);
        if(bytesRead < 1){
            return new SupplierInputStreamSupplier(() -> new ByteArrayInputStream(new byte[0]));
        }
        if(bytesRead <4){
            int read=0;
            do {
                read = pushbackInputStream.read(magicNumber, bytesRead, 4 - bytesRead);
                bytesRead += read;
            }while(read >0 && bytesRead < 4);

        }
        pushbackInputStream.unread(magicNumber,0, bytesRead);
        if (magicNumber[0] == (byte)0x50 && magicNumber[1] == (byte)0x4B && magicNumber[2] == (byte)0x03 && magicNumber[3]== (byte) 0x04){
            //zipped
            return new SupplierInputStreamSupplier(()-> {
                ZipInputStream zip = new ZipInputStream(new BufferedInputStream(pushbackInputStream));
                try {
                    zip.getNextEntry();
                } catch (IOException e) {
                    Sneak.sneakyThrow(e);
                }
                return zip;
            });
        }
        if( magicNumber[0] == (byte) 0x1F && magicNumber[1] == (byte)0x8B){
            //gzip
            return new SupplierInputStreamSupplier(()-> {
                try {
                    return new GZIPInputStream(new BufferedInputStream(pushbackInputStream));
                } catch (IOException e) {
                    return  Sneak.sneakyThrow(e);
                }
            });
        }

        return new SupplierInputStreamSupplier(()->pushbackInputStream);
    }

    static InputStreamSupplier forResourse(URL url) throws IOException{
        Objects.requireNonNull(url, "url can not be null");
        ThrowableSupplier<InputStream, IOException> supplier = ()->url.openStream();
        try(InputStream in = supplier.get()){
            if(in ==null){
                throw new FileNotFoundException("could not find resource with url '"+url +"'");
            }
            byte[] magicNumber = new MagicNumberInputStream(in).peekMagicNumber();
            if (magicNumber[0] == (byte)0x50 && magicNumber[1] == (byte)0x4B && magicNumber[2] == (byte)0x03 && magicNumber[3]== (byte) 0x04){
                //zipped
                return new SupplierInputStreamSupplier(()->{
                    try{
                        ZipInputStream zip =new ZipInputStream(new BufferedInputStream(supplier.get()));
                        //assume first record is the entry we care about?
                        zip.getNextEntry();
                        return in;
                    }catch(Throwable t){
                        return Sneak.sneakyThrow(t);
                    }
                });
            }
            if( magicNumber[0] == (byte) 0x1F && magicNumber[1] == (byte)0x8B){
                //gzip
                return new SupplierInputStreamSupplier(()->{
                    try{
                        return new GZIPInputStream(new BufferedInputStream(supplier.get()));
                    }catch(IOException e){
                        return Sneak.sneakyThrow(e);
                    }
                });
            }

            return new SupplierInputStreamSupplier(()->{
                try{
                    return new BufferedInputStream(supplier.get());
                }catch(Throwable t){
                    return Sneak.sneakyThrow(t);
                }
            });
        }
    }
}
