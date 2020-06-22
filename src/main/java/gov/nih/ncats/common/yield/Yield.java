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

import gov.nih.ncats.common.stream.StreamUtil;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A Yield like structure from other languages such a C#
 * that allow one to iterate over a collection of
 * elements computing each element only as needed.
 * It is possible to also make a Yield produce an infinite
 * number of elements.
 *
 * The code in this package is based on the
 *  <a href="https://github.com/benjiman/yield-java">Yield-Java</a> code
 * written by Benji Weber using MIT license.
 * A copy of that license
 * is located in derivedWorkLicenses/Yield-Java-LICENSE.txt
 *
 * @param <T> the type returned from each computation.
 *
 *
 * @author katzelda
 * @author Benji Weber
 */
@FunctionalInterface
public interface Yield<T> extends Iterable<T> {
    /**
     * Helper function to create {@link Yield}s
     * in the event that a bare lambda is not sufficient to pass around.
     *
     * @param yieldRecipe
     * @param <T>
     * @return
     */
    static <T> Yield<T> create(Consumer<YieldRecipe<T>> yieldRecipe){
       return yieldRecipe::accept;
    }

    /**
     * Execute the given {@link YieldRecipe} this method
     * is the functional interface method that is invoked to invoke the yield mechanic.
     * @param yieldRecipe the {@link YieldRecipe} to perform callbacks on; will never be null.
     */
    void execute(YieldRecipe<T> yieldRecipe);

    /**
     * Convert this Yield into a {@link Stream}
     * which may block while waiting for the next element;
     * the returned Stream must be properly closed.
     * Please note that care should be taken to either
     * not convert infinite yields into streams or to ensure
     * that a Stream that may be infiite is turned finite by calling
     * {@link Stream#limit(long)}.
     * @return a new Stream that needs to be closed
     * when done.
     */
    default Stream<T> stream() {
        return StreamUtil.forIterator(iterator());
    }
    /**
     * Convert this Yield into a {@link Stream}
     * running in a thread from the givne {@link YieldExecutor}
     * which may block while waiting for the next element.
     * The returned Stream must be properly closed.
     * Please note that care should be taken to either
     * not convert infinite yields into streams or to ensure
     * that a Stream that may be infiite is turned finite by calling
     * {@link Stream#limit(long)}.
     * @return a new Stream that needs to be closed
     * when done.
     */
    default Stream<T> stream(YieldExecutor yieldExecutor) {
        return StreamUtil.forIterator(iterator(yieldExecutor));
    }
    /**
     * Convert this Yield into a {@link YieldingIterator}
     * that runs in a thread from the given {@link YieldExecutor};
     * NOTE: this iterator which may block while waiting for the next element;
     * the returned YieldingIterator must be properly closed.
     * @return a new {@link YieldingIterator} that needs to be closed
     * when done.
     */
    default YieldingIterator<T> iterator(YieldExecutor yieldExecutor) {

        YieldRecipe<T> yieldRecipe = new YieldRecipe<>();
        Future<?> future = yieldExecutor.submit(YieldUtil.createRunnableFor(this, yieldRecipe));
        yieldRecipe.onClose(()->future.cancel(true));
        return yieldRecipe.iterator();
    }

    /**
     * Convert this Yield into a {@link YieldingIterator}
     * which may block while waiting for the next element;
     * the returned YieldingIterator must be properly closed.
     * @return a new {@link YieldingIterator} that needs to be closed
     * when done.
     */
    default YieldingIterator<T> iterator() {
        YieldRecipe<T> yieldRecipe = new YieldRecipe<>();
        Thread collectorThread = new Thread(YieldUtil.createRunnableFor(this, yieldRecipe));
        collectorThread.setDaemon(true);
        collectorThread.start();
        yieldRecipe.onClose(collectorThread::interrupt);
        return yieldRecipe.iterator();
    }
}



