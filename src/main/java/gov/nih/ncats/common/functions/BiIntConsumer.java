package gov.nih.ncats.common.functions;

import java.util.Objects;
/**
 * Consumer that consumes 2 ints.
 *
 * @author katzelda
 *
 */
@FunctionalInterface
public interface BiIntConsumer {
    /**
     * Performs this operation on the given arguments.
     *
     * @param x the first input argument
     * @param y the second input argument
     */
    void accept(int x, int y);

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
    default BiIntConsumer andThen(BiIntConsumer after) {
        Objects.requireNonNull(after);

        return (x, y) -> {
            accept(x, y);
            after.accept(x, y);
        };
    }
}

