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

package gov.nih.ncats.common.util;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Factory class that makes Iterables (or Streams)
 * of Delayed Computations. The element's return value for each iteration
 * might not be computed until it's needed.  Different factory methods
 * exist to make the returned object a Least Recently Used Cache as well.
 */
public final class DelayedComputationIterables {

    public static <T,R> GeneratedIterableBuilder<T,R> builderUsingGenerator(Function<T,R> generatingFunction){
        return new GeneratedIterableBuilder(generatingFunction,  new DelayedComputationList<>());
    }
    public static <T,R> GeneratedIterableBuilder<T,R> builderUsingGenerator(Function<T,R> generatingFunction, int initialSize){
        return new GeneratedIterableBuilder(generatingFunction, new DelayedComputationList<>(initialSize));
    }

    public static <T,R> GeneratedIterableBuilder<T,R> builderUsingGenerator(Function<T,R> generatingFunction, int initialSize, int lruCacheSize){
        return new GeneratedIterableBuilder(generatingFunction, new LruDelayedComputationList(initialSize,lruCacheSize ));
    }

    public static class GeneratedIterableBuilder<T,R>{

        private final Function<T,R> generatingFunction;
        private final DelayedComputationList<R> backingList;
        private GeneratedIterableBuilder(Function<T, R> generatingFunction, DelayedComputationList<R> backingList) {
            this.generatingFunction = Objects.requireNonNull(generatingFunction);
            this.backingList = backingList;
        }

        public GeneratedIterableBuilder<T,R> add(T t){
            this.backingList.add(()->generatingFunction.apply(t));
            return this;
        }
        public GeneratedIterableBuilder<T,R> addAll(Iterable<T> ts){
            ts.forEach(this::add);
            return this;
        }

        /**
         * Create an {@link Iterable}
         * that can iterate over the added items. This iterator
         * can be reused multiple times to iterate through the added
         * items multiple times.
         * This is a finishing step and should only be called once
         * after all the records have been added.
         * @return an Iterable
         */
        public Iterable<R> build(){
            return backingList;
        }
        /**
         * Create an {@link Stream}
         * for the added items.
         * This is a finishing step and should only be called once
         * after all the records have been added.
         * @return a new Stream
         */
        public Stream<R> buildAsStream(){
            return backingList.stream();
        }
    }
}
