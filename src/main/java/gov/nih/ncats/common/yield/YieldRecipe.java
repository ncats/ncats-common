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

import gov.nih.ncats.common.util.Unchecked;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The recipe to create a {@link Yield}.
 * @param <T>
 */
public class YieldRecipe<T> implements Iterable<T>, YieldingIterator<T> {

    private Optional END_TOKEN = Optional.empty();

    private static Object FLOW_PROCEED = new Object();
    private final SynchronousQueue<Optional<T>> dataChannel = new SynchronousQueue<>();
    private final SynchronousQueue<Object> flowChannel = new SynchronousQueue<>();
    private final AtomicReference<Optional<T>> currentValue = new AtomicReference<>(Optional.empty());
    private List<Runnable> toRunOnClose = new CopyOnWriteArrayList<>();

    private Optional<T> completed(){
        return END_TOKEN;
    }

    /**
     * add the given value to be returned to the Yield next.
     * @param value
     */
    public void returning(T value) {
        publish(value);
        waitUntilNextValueRequested();
    }

    /**
     * Stop the Yield when it gets to this point.  This is useful
     * as a break out condition in an infinite Yield.
     */
    public void breaking() {
        throw new YieldBreakException();
    }

    @Override
    public YieldingIterator<T> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        if(currentValue.get().isPresent()){
            return true;
        }
        calculateNextValue();
        Optional<T> value = Unchecked.uncheck(() -> dataChannel.take());
        if (END_TOKEN == value){
            return false;
        }
        currentValue.set(value);
        return true;
    }

    @Override
    public T next() {
        try {
            if(!currentValue.get().isPresent()){
                hasNext();
            }
            return currentValue.get().get();
        } finally {
            currentValue.set(Optional.empty());
        }
    }

    public void signalComplete() {
        Unchecked.uncheck(() -> this.dataChannel.put(completed()));
    }

    public void waitUntilFirstValueRequested() {
        waitUntilNextValueRequested();
    }

    private void waitUntilNextValueRequested() {
        Unchecked.uncheck(() -> flowChannel.take());
    }

    private void publish(T value) {
        Unchecked.uncheck(() -> dataChannel.put(Optional.of(value)));
    }

    private void calculateNextValue() {
        Unchecked.uncheck(() -> flowChannel.put(FLOW_PROCEED));
    }

    @Override
    public void close() {
        toRunOnClose.forEach(Runnable::run);
    }

    public void onClose(Runnable onClose) {
        this.toRunOnClose.add(onClose);
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
