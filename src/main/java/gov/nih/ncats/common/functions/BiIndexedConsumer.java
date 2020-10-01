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

/**
 * A Consumer that also includes an x,y coordinate.  This is helpful
 * when consuming sparse matrices.
 * @param <T>
 */
@FunctionalInterface
public interface BiIndexedConsumer<T> {
    /**
     * Performs this operation on the given arguments.
     *
     * @param x the first index argument
     * @param y the second index argument
     * @param t the object
     */
    void accept(int x, int y, T t);

    /**
     * Returns a composed {@code BiIndexedConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code BiIndexedConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default BiIndexedConsumer<T> andThen(BiIndexedConsumer<T> after) {
        Objects.requireNonNull(after);

        return (x, y, t) -> {
            accept(x, y, t);
            after.accept(x, y, t);
        };

    }
}

