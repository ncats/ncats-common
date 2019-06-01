/*
 *     NCATS-COMMON
 *
 *     Written in 2019 by NIH/NCATS
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package gov.nih.ncats.common.io;

import java.io.*;
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
}