/*
 *     NCATS-COMMON
 *
 *     Written in 2019 by NIH/NCATS
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

package gov.nih.ncats.common.functions;

import java.util.Objects;

@FunctionalInterface
public interface IndexedConsumer<T> {
    /**
     * Consume the given object that was the ith index.
     *
     * @param index the index of the object to be consumed.
     * @param t the object to consume.
     */
    void accept(int index, T t);

    /**
     * Returns a composed {@code BiIntConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code BiIntConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default IndexedConsumer<T> andThen(IndexedConsumer<T> after) {
        Objects.requireNonNull(after);

        return (x, y) -> {
            accept(x, y);
            after.accept(x, y);
        };
    }
}
