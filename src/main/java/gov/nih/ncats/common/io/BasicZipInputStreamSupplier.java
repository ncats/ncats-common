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
import java.util.zip.ZipInputStream;
/**
 * {@link InputStreamSupplier} that assumes the wrapped zip file
 * only contains a single entry.
 * 
 */
class BasicZipInputStreamSupplier extends AbstractFileInputStreamSupplier {

    public BasicZipInputStreamSupplier(File file) {
        super(file);
    }

    @Override
    public InputStream get() throws IOException {
        ZipInputStream in =new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
        //assume first record is the entry we care about?
        in.getNextEntry();
        return in;
    }

}

