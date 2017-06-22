package gov.nih.ncats.common.util;

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
     * A {@link java.util.function.Consumer} that can throw an exception.
     * @author dkatzel
     *
     * @param <T> the type the consumer accepts.
     * @param <E> the exception that can be thrown.
     */
    interface ThrowingConsumer<T, E extends Exception>{
        void accept(T t) throws E;

    }
    /**
     * Iterate over each element remaining in the stream and call the given
     * ThrowingConsumer, which may throw an Exception E.
     * @param action the consumer to consume for each element.
     * @throws E the Exception the throwing consumer might throw.
     */
    default <E extends Exception> void throwingForEach(ThrowingConsumer<? super T, E> action) throws E {
        forEach(t-> {
            try {
                action.accept(t);
            } catch (Throwable ex) {
                throw Sneak.sneakyThrow(ex);
            }
        });
    }
    /**
     * Iterate over each element remaining in the stream in order and call the given
     * ThrowingConsumer, which may throw an Exception E.
     * @param action the consumer to consume for each element.
     * @throws E the Exception the throwing consumer might throw.
     */
    default <E extends Exception> void throwingForEachOrdered(ThrowingConsumer<? super T, E> action) throws E{
        forEachOrdered(t-> {
            try {
                action.accept(t);
            } catch (Throwable ex) {
                throw Sneak.sneakyThrow(ex);
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

