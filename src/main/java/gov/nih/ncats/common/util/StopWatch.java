package gov.nih.ncats.common.util;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public final class StopWatch {

    private StopWatch(){
        //can not instantiate
    }

    public static long timeElapsed(Runnable r){
        long start = System.currentTimeMillis();
        r.run();
        long end = System.currentTimeMillis();
        return end-start;

    }

    public static long timeElapsed(Callable<Void> c) throws Exception{
        long start = System.currentTimeMillis();
        c.call();
        long end = System.currentTimeMillis();
        return end-start;
    }

    public static <T> T timeElapsedFetch(Callable<T> c, LongConsumer timeConsumer) throws Exception{
        long start = System.currentTimeMillis();
        try{
            T t= c.call();
            return t;
        }finally{
            long end = System.currentTimeMillis();
            timeConsumer.accept(end-start);
        }
    }

}
