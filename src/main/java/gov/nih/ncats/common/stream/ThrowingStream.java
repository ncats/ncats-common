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

package gov.nih.ncats.common.stream;

import gov.nih.ncats.common.functions.ThrowableConsumer;
import gov.nih.ncats.common.sneak.Sneak;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * An extension of {@link Stream} but with additional methods
 * that can throw checked exceptions, usually named {@code throwingXXX()}.
 *
 * @author dkatzel
 *
 * @param <T> the type of element in the stream.
 */
public interface ThrowingStream<T> extends Stream<T>{
    /**
     * Wrap the given Stream in a ThrowingStream to get the additional
     * throwing methods.
     * @param stream the {@link Stream} to wrap; can not be null.
     * @return a new {@link ThrowingStream}; will never be null.
     *
     * @throws NullPointerException if stream is null.
     */
    public static <T> ThrowingStream<T> asThrowingStream( Stream<T> stream){
        return new ThrowingStreamImpl<>(stream);
    }

    /**
     * Iterate over each element remaining in the stream and call the given
     * ThrowingConsumer, which may throw an Exception E.
     * @param action the consumer to consume for each element.
     * @throws E the Exception the throwing consumer might throw.
     */
    default <E extends Exception> void throwingForEach(ThrowableConsumer<? super T, E> action) throws E {
        forEach(t-> {
            try {
                action.accept(t);
            } catch (Throwable ex) {
                 Sneak.sneakyThrow(ex);
            }
        });
    }
    /**
     * Iterate over each element remaining in the stream in order and call the given
     * ThrowingConsumer, which may throw an Exception E.
     * @param action the consumer to consume for each element.
     * @throws E the Exception the throwing consumer might throw.
     */
    default <E extends Exception> void throwingForEachOrdered(ThrowableConsumer<? super T, E> action) throws E{
        forEachOrdered(t-> {
            try {
                action.accept(t);
            } catch (Throwable ex) {
                 Sneak.sneakyThrow(ex);
            }
        });

    }

    @Override
    ThrowingStream<T> sequential();
    @Override
    ThrowingStream<T> parallel();
    @Override
    ThrowingStream<T> unordered();
    @Override
    ThrowingStream<T> onClose(Runnable closeHandler);

    @Override
    ThrowingStream<T> filter(Predicate<? super T> predicate);
    @Override
    <R> ThrowingStream<R> map(Function<? super T, ? extends R> mapper);

    @Override
    <R> ThrowingStream<R> flatMap(
            Function<? super T, ? extends Stream<? extends R>> mapper);

    @Override
    ThrowingStream<T> distinct();
    @Override
    ThrowingStream<T> sorted();
    @Override
    ThrowingStream<T> sorted(Comparator<? super T> comparator);
    @Override
    ThrowingStream<T> peek(Consumer<? super T> action);
    @Override
    ThrowingStream<T> limit(long maxSize);
    @Override
    ThrowingStream<T> skip(long n);

    static <T> ThrowingStream<T> createFrom(Spliterator<T> spliterator, boolean parallel) {
        return asThrowingStream(StreamSupport.stream(spliterator, parallel));
    }




}

