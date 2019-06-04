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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * An {@link InputStreamSupplier}
 * that creates {@link InputStream}s from 
 * normal files (not compressed etc).
 * 
 * 
 * @author dkatzel
 *
 */
class RawFileInputStreamSupplier implements InputStreamSupplier {
    private final File file;
    
    RawFileInputStreamSupplier(File file){
        //assume since this class is package private
        //that the file is not null, exists and is readable.
        this.file = file;
    }
    
    @Override
    public InputStream get() throws IOException {
        return new BufferedInputStream(new FileInputStream(file));
    }
    
    
    @Override
    public Optional<File> getFile() {
        return Optional.of(file);
    }
    

}

