package gov.nih.ncats.common.functions;

/**
 * Created by katzelda on 5/30/19.
 */
public interface ThrowableConsumer<T, E extends Throwable>{
    void accept(T t) throws E;
}
