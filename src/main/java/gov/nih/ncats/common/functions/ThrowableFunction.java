package gov.nih.ncats.common.functions;

/**
 * Created by katzelda on 5/30/19.
 */
public interface ThrowableFunction<T, R, E extends Throwable> {

    R apply(T t) throws E;
}
