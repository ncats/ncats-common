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
