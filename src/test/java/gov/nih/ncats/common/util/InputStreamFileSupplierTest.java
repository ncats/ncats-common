/*
 * NCATS-COMMON
 *
 * Copyright 2021 NIH/NCATS
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

import gov.nih.ncats.common.io.IOUtil;
import gov.nih.ncats.common.io.InputStreamSupplier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.Assert.*;

public class InputStreamFileSupplierTest {

    private static final String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam facilisis, diam sit amet efficitur pellentesque, ante tellus sodales ex, at tincidunt neque metus et quam. In hac habitasse platea dictumst. Vivamus sed gravida sapien, nec lacinia eros. Ut pretium, ex vel pretium blandit, sapien tellus pellentesque orci, id ullamcorper lectus risus a est. Pellentesque et eros ante. Quisque sit amet mauris vestibulum, auctor est nec, maximus urna. Cras in lorem eget tellus viverra tempor. Donec ac magna egestas, luctus urna nec, laoreet tortor. Donec rhoncus augue in finibus tempor. Sed facilisis nunc nec felis laoreet, egestas rutrum arcu cursus. Etiam lobortis enim a risus blandit molestie. Suspendisse vel dolor neque. Nulla facilisi. Sed ultricies massa vel metus dapibus feugiat. Donec eleifend semper accumsan. Ut mattis lorem enim. ";



    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();


    @Test
    public void uncompressedFile() throws Exception{

        File f = writeUncompressed();

        InputStreamSupplier sut =  InputStreamSupplier.forFile(f);
        assertEquals(f, sut.getFile().get());
        assertReadLoremText(sut);
    }

    @Test
    public void uncompressedInputStream() throws Exception{

        File f = writeUncompressed();

        InputStreamSupplier sut =  InputStreamSupplier.forInputStream(new FileInputStream(f));
        assertFalse(sut.getFile().isPresent());
        assertReadLoremText(sut);
        assertEquals("should not be able to read stream again",  -1, sut.get().read());
    }

    @Test
    public void gzipFile() throws Exception{

        File f = writeGZipped();

        InputStreamSupplier sut =  InputStreamSupplier.forFile(f);
        assertEquals(f, sut.getFile().get());
        assertReadLoremText(sut);
    }
    @Test
    public void gzipInputStream() throws Exception{

        File f = writeGZipped();

        InputStreamSupplier sut =  InputStreamSupplier.forInputStream(new FileInputStream(f));
        assertFalse(sut.getFile().isPresent());
        assertReadLoremText(sut);
        try {
             sut.get().read();
            fail("should throw EOFException when trying to re-read");
        }catch(EOFException expected){
            //end of file
        }
    }

    @Test
    public void gzipFileReadMultipleTimes() throws Exception{

        File f = writeGZipped();

        InputStreamSupplier sut =  InputStreamSupplier.forFile(f);
        assertEquals(f, sut.getFile().get());
        assertReadLoremText(sut);
        assertReadLoremText(sut);
        assertReadLoremText(sut);
    }
    @Test
    public void zipFile() throws Exception{

        File f = writeZipped();

        InputStreamSupplier sut =  InputStreamSupplier.forFile(f);
        assertEquals(f, sut.getFile().get());
        assertReadLoremText(sut);
    }
    @Test
    public void zipFileReadMultipleTimes() throws Exception{

        File f = writeZipped();

        InputStreamSupplier sut =  InputStreamSupplier.forFile(f);
        assertEquals(f, sut.getFile().get());
        assertReadLoremText(sut);
    }
    @Test
    public void zipInputStream() throws Exception{

        File f = writeZipped();

        InputStreamSupplier sut =  InputStreamSupplier.forInputStream(new FileInputStream(f));
        assertFalse(sut.getFile().isPresent());
        assertReadLoremText(sut);
        assertEquals("should not be able to read stream again",  -1, sut.get().read());
    }
    private static void assertReadLoremText(InputStreamSupplier sut) throws IOException {
        assertEquals(lorem, new String(IOUtil.toByteArray(sut.get())));
    }

    @Test
    public void uncompressedFileReadMultipleTimes() throws Exception{

        File f = writeUncompressed();

        InputStreamSupplier sut =  InputStreamSupplier.forFile(f);
        assertEquals(f, sut.getFile().get());
        assertReadLoremText(sut);
        assertReadLoremText(sut);
        assertReadLoremText(sut);
    }

    private File writeUncompressed() throws IOException {
        File f = tmpDir.newFile();
        try(PrintWriter writer = new PrintWriter(f)){
            writer.print(lorem);
        }
        return f;
    }

    private File writeGZipped() throws IOException {
        File f = tmpDir.newFile();
        try(GZIPOutputStream out = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(f)))){
            out.write(lorem.getBytes());
        }
        return f;
    }

    private File writeZipped() throws IOException {
        File f = tmpDir.newFile();
        try(ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(f)))){
            ZipEntry entry = new ZipEntry("entry");
            out.putNextEntry(entry);

            out.write(lorem.getBytes());
            out.closeEntry();
            out.finish();
        }
        return f;
    }

}
