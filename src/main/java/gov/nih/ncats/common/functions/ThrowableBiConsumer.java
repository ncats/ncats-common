package gov.nih.ncats.common.functions;

import java.util.function.BiConsumer;

/**
 * Created by katzelda on 5/30/19.
 */
public interface ThrowableBiConsumer<K, V, E extends Throwable> {

    void accept(K k, V v) throws E;
}
