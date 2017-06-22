package gov.nih.ncats.common.util.functions;

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
