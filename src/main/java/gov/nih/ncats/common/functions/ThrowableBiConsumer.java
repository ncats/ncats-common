package gov.nih.ncats.common.functions;

import java.util.function.BiConsumer;

/**
 * Created by katzelda on 5/30/19.
 */
public interface ThrowableBiConsumer<K, V> extends BiConsumer<K, V> {
    public void throwing(K k, V v) throws Exception;

    public default void accept(K k, V v) {
        try {
            this.throwing(k, v);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
