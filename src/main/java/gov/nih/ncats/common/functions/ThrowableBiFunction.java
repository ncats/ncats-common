package gov.nih.ncats.common.functions;

/**
 * Created by katzelda on 5/30/19.
 */
public interface ThrowableBiFunction<A,B, R, E extends Throwable> {

    R apply(A a, B b) throws E;
}
