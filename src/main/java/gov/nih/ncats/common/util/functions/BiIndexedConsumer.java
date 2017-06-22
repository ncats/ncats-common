package gov.nih.ncats.common.util.functions;

import java.util.Objects;
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

