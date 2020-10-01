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

package gov.nih.ncats.common.functions;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Created by katzelda on 5/30/19.
 */
@FunctionalInterface
public interface ThrowableConsumer<T, E extends Throwable>{
    void accept(T t) throws E;

    /**
     * Wrap the given Consumer in a {@link ThrowableConsumer}
     * @param consumer the consumer to wrap; can not be null.
     * @param <T>
     * @param <E>
     * @return a new {@link ThrowableConsumer} will never be null.
     * @throws NullPointerException if consumer is null.
     */
    static <T, E extends Throwable> ThrowableConsumer<T,E> wrap(Consumer<T> consumer){
        Objects.requireNonNull(consumer);
        return t-> consumer.accept(t);
    }
}
