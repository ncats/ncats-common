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

package gov.nih.ncats.common.yield;

/**
 * Utility class of pulled out reusable methods used by {@link Yield} since
 * Java 8 doesn't allow private methods in interfaces.
 */
final class YieldUtil {

    private YieldUtil(){
        //can not instantiate
    }

   static  <T> Runnable createRunnableFor(Yield<T> yield, YieldRecipe<T> yieldRecipe) {
        return ()-> {
            yieldRecipe.waitUntilFirstValueRequested();
            try {
                yield.execute(yieldRecipe);
            } catch (YieldBreakException e) {
            } catch (Throwable e) {
                //if we were in a block and our thread got interrupted
                //(possibly from closing a blocking stream)
                //then it will throw an interrupt exception
                //except we wrap most of our waiting with Unchecked
                //so that interruptedException will probably be wrapped in a RuntimeException
                Throwable cause = e.getCause();

                if (!(e instanceof InterruptedException) && !(cause != null && cause instanceof InterruptedException)) {
                    //not an interrupt so throw the exception because it's real.
                    throw e;
                }
                //we were interrupted hide error keep going
            }
            yieldRecipe.signalComplete();
        };
    }
}
