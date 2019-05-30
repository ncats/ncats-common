package gov.nih.ncats.common.iter;
import java.io.Closeable;
import java.util.Iterator;
import java.util.Objects;

/**
 * An Iterator that wraps somekind of resource that
 * must be closed.  This can be used in a Java 7 try-with-resource.
 *
 *
 * Created by katzelda on 4/21/16.
 */
public interface CloseableIterator<T> extends Iterator<T>, Closeable{

    /**
     * Wrap an Iterator and make it CloseableIterator that delegates all calls to Iterator#next(), Iterator#hasNext()
     * and Iterator#remove().  The returned object will usually be a new object, but will not always
     * be depending on what type is passed in.
     * <p>
     *     Rules for how to make the returned CloseableIterator instance:
     * </p>
     * <ol>
     *     <li>If the iterator is not closeable, then make a new CloseableIterator instance with an empty close()</li>
     *     <li>If the iterator IS closeable:
     *
     *          <ol>If the iterator already implements CloseableIterator, do not make a new object and just return the passed in object</ol>
     *          <ol>Make a new CloseableIterator that will delegate to the wrapped object's close()</ol>
     *     </li>
     * </ol>
     * If the iterator to wrap
     * is already also Closeable then it's close() will be correctly delegated to.
     * @param iter the iterator to wrap; can not be null.
     * @param <T> the type returned by Iterator calls to next().
     * @param <I> generic mess that is used to make compiler happy casting if the passed in iterator already is closeable.
     * @return a new CloseableIterator object if the passed in iter is not already a CloseableIterator; or
     * iter if it is.
     *
     * @throws NullPointerException if iter is null.
     */
    @SuppressWarnings("unchecked")
    public static <T, I extends Iterator<T> & Closeable> CloseableIterator<T> wrap(Iterator<T> iter){
        Objects.requireNonNull(iter);

        if(iter instanceof Closeable){
            //already closeableIterator just return it as is.
            if(iter instanceof CloseableIterator){
                return (CloseableIterator<T>) iter;
            }
            return new CloseableIteratorImpl.CloseableWrapper<>((I)iter);
        }
        return new CloseableIteratorImpl.Wrapper<>(iter);
    }
}

