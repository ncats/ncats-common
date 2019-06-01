/*
 *     NCATS-COMMON
 *
 *     Written in 2019 by NIH/NCATS
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package gov.nih.ncats.common.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.Callable;

/**
 * Created by katzelda on 4/20/17.
 */
public class Unchecked {
    public interface ThrowingRunnable<E extends Exception>{
        void run() throws E;
    }

    public static void ioException(ThrowingRunnable<? super IOException> runnable){
        try{
            runnable.run();
        }catch(IOException e){
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <V> V uncheck(Callable<V> callable){
        try{
            return callable.call();
        }catch(IOException e){
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
