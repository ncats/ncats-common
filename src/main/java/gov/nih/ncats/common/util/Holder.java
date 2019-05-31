package gov.nih.ncats.common.util;

/*
 * Created by katzelda on 6/20/17.
 */

/**
 * Simple holder wrapper to hold an object this
 * can be used to make something "effectively final".
 *
 * @param <T> the type of the object being held.
 */
public class Holder<T> {

    private volatile T held;

    public static <T> Holder<T> hold( T obj){
        return new Holder<>(obj);
    }

    public Holder(T held) {
        this.held = held;
    }

    public T get(){
        return held;
    }

    public Holder set( T held){
        this.held = held;
        return this;
    }
}
