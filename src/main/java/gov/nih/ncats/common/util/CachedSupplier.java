/*
 * NCATS-COMMON
 *
 * Copyright 2019 NIH/NCATS
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package gov.nih.ncats.common.util;

import gov.nih.ncats.common.sneak.Sneak;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

/**
 * Memoized supplier. Caches the result of the supplier
 * to be used after. Useful for expensive calls.
 *
 * @author peryeata
 * @param <T>
 */
public class CachedSupplier<T> implements Supplier<T>, Callable<T>, ResetableCache {

    @FunctionalInterface
    public interface SupplierInvocationListener{
        void invoked();


    }
    private static AtomicLong generatedVersion= new AtomicLong();




    /**
     * Flag to signal all CachedSupplier instances
     * to regenerate from their suppliers on the next call.
     */
    public static void resetAllCaches(){
        CachedSupplier.generatedVersion.incrementAndGet();
    }

    private final Supplier<T> c;
    private T cache;
    private volatile boolean runAtLeastOnce=false;
    private AtomicBoolean run=new AtomicBoolean(false);
    private long generatedWithVersion;
    //most cached suppliers won't have listeners so initialize to 0
    private List<SupplierInvocationListener> listeners = new ArrayList<>(0);
    public CachedSupplier(final Supplier<T> c){
        this.c= Objects.requireNonNull(c);
    }

    public void removeListener(SupplierInvocationListener l){
        listeners.remove(Objects.requireNonNull(l));
    }
    public void addListener(SupplierInvocationListener l){
        listeners.add(Objects.requireNonNull(l));
    }

    private void fireListeners(){
        listeners.forEach(SupplierInvocationListener::invoked);
    }
    /**
     * Delegates to {@link #get()}
     */
    @Override
    public T call() throws Exception{
        return get();
    }


    @Override
    public T get() {
        if(hasRun()) {
            return this.cache;
        }else{
            synchronized(this){
                if(hasRun()){
                    return this.cache;
                }
                this.generatedWithVersion=CachedSupplier.generatedVersion.get();
                this.cache=directCall();
                this.run.set(true);
                runAtLeastOnce=true;
                return this.cache;
            }
        }
    }

    protected T directCall(){

        T ret= this.c.get();
        fireListeners();
        return ret;
    }


    /**
     * An explicitly synchronized form of {@link #get()}
     * @return
     */
    public synchronized T getSync() {
        return get();
    }

    public boolean hasRun(){
        return this.run.get() && !cacheHasBeenReset();
    }


    protected boolean cacheHasBeenReset(){
        return runAtLeastOnce && this.generatedWithVersion!=CachedSupplier.generatedVersion.get();
    }
    /**
     * Flag to signal this instance to recalculate from its
     * supplier on next call.
     */
    @Override
    public synchronized void resetCache(){
        this.run.set(false);
        this.cache=null;
    }

    /**
     * Make a CachedSupplier that will only run once;
     * any calls to {@link #resetCache()} or {@link #resetAllCaches()}
     * does not affect THIS returned instance.
     *
     * @param supplier the supplier to run only once; can not be null;
     * @param <T> the type returned by the Supplier.
     * @return a new CachedSupplier instance.
     * @throws NullPointerException if supplier is null.
     */
    public static <T> CachedSupplier<T> runOnce(final Supplier<T> supplier){
        return new UnResettableCachedSupplier<>(supplier);
    }
    /**
     * Make a CachedSupplier that will only run once;
     * any calls to {@link #resetCache()} or {@link #resetAllCaches()}
     * does not affect THIS returned instance.
     *
     * @param result the constant result;
     * @param <T> the type returned by the Supplier.
     * @return a new CachedSupplier instance.
     */
    public static <T> CachedSupplier<T> ofConstant( T result){
        return new UnResettableCachedSupplier<>(()-> result);
    }

    public static <T> CachedSupplier<T> runOnceCallable(final Callable<T> callable){
        return runOnce(()->{
            try{
                return callable.call();
            }catch(final Exception e){
                throw new IllegalStateException(e);
            }
        });
    }
    public static <T> CachedSupplier<T> of(final Supplier<T> supplier){
        return new CachedSupplier<T>(supplier);
    }

    /**
     * Wrap the provided callable as a cached supplier
     * @param callable
     * @return
     */
    public static <T> CachedSupplier<T> ofCallable(final Callable<T> callable){
        return of(()->{
            try{
                return callable.call();
            }catch(final Exception e){
                throw new IllegalStateException(e);
            }
        });
    }

    public static <T> CachedThrowingSupplier<T> ofThrowing(final Callable<T> callable){
        return  CachedThrowingSupplier.createFromSupplier(()->{
            try{
                return callable.call();
            }catch(final Throwable e){
                return Sneak.sneakyThrow(e);
            }
        });
    }

    /**
     * An extension of a {@link CachedSupplier} which will catch any
     * throwable thrown during the initial {@link Supplier#get()} call,
     * and cache it as well, returning <code>null</code> for the value
     * cache. Calling {@link #getThrown()} will return an {@link Optional}
     * of a {@link Throwable}, which is empty if there was nothing
     * thrown during the execution.
     * @author peryeata
     *
     * @param <T>
     */
    public static class CachedThrowingSupplier<T> extends CachedSupplier<Optional<T>>{

        public Throwable thrown=null;

        public static <T> CachedThrowingSupplier<T> createFromSupplier(Supplier<T> consumer){
            return new CachedThrowingSupplier(() -> Optional.ofNullable(consumer.get()));
        }
        public static <T> CachedThrowingSupplier<T> createFromSupplierOfOptionals(Supplier<Optional<T>> consumer){
            return new CachedThrowingSupplier(consumer);
        }

        private CachedThrowingSupplier(Supplier<Optional<T>> c) {
            super(c);
        }

        @Override
        protected Optional<T> directCall(){
            try{
                return super.directCall();
            }catch(Throwable e){
                setThrown(e);
                return Optional.empty();
            }
        }

        private void setThrown(Throwable t){
            this.thrown=t;
        }

        /**
         * Calls the supplier (if necessary), and returns an {@link Optional}
         * of anything thrown by that supplier.
         * @return
         */
        public Optional<Throwable> getThrown(){
            this.get();
            return Optional.ofNullable(thrown);
        }

    }

    private static class UnResettableCachedSupplier<T> extends CachedSupplier<T>{

        public UnResettableCachedSupplier(Supplier<T> c) {
            super(c);
        }

        @Override
        protected boolean cacheHasBeenReset() {
            return false;
        }

        @Override
        public void resetCache() {
            //no-op
        }
    }
}