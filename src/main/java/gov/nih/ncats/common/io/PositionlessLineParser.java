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

import java.io.*;

/**
 * A {@link LineParser} implementation that
 * doesn't keep track of the offset position.
 */
public class PositionlessLineParser implements LineParser{

    private final BufferedReader reader;
    
    private String nextLine;
    private boolean isClosed;
    
    public PositionlessLineParser(File inputFile) throws IOException{
        this(InputStreamSupplier.forFile(inputFile));
    }
    public PositionlessLineParser(InputStream in) throws IOException{
        reader = new BufferedReader(new InputStreamReader(in,IOUtil.UTF_8));
        updateNextLine();
    }
    public PositionlessLineParser(InputStreamSupplier inputStreamSupplier) throws IOException{
        reader = new BufferedReader(new InputStreamReader(inputStreamSupplier.get(),IOUtil.UTF_8));
        updateNextLine();
    }
    
    private void updateNextLine() throws IOException{
        nextLine = reader.readLine();
    }
    
    
    
    @Override
    public boolean hasNextLine() {

        return nextLine!=null;
    }

    @Override
    public String peekLine() {
        return nextLine;
    }

    @Override
    public long getPosition() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tracksPosition() {
        return false;
    }

    @Override
    public String nextLine() throws IOException {
        if(isClosed){
            throw new IOException("closed");
        }
        String oldLine = nextLine;
        updateNextLine();
        return oldLine;
    }

    @Override
    public void close() throws IOException {
        reader.close();
        nextLine=null;
        isClosed = true;
    }

}
