package gov.nih.ncats.common.util;

/**
 * Created by katzelda on 6/20/17.
 */
public final class SingleThreadCounter {

    private long count;

    public SingleThreadCounter(){

    }

    public SingleThreadCounter(long initialValue){
        this.count = initialValue;
    }

    public SingleThreadCounter increment(){
        count++;
        return this;
    }
    public SingleThreadCounter increment(long amount){
        count+=amount;
        return this;
    }

    public SingleThreadCounter decrement(){
        count--;
        return this;
    }
    public SingleThreadCounter decrement(long amount){
        count-=amount;
        return this;
    }

    public int getAsInt(){
        return (int) count;
    }

    public long getAsLong(){
        return count;
    }

    @Override
    public String toString() {
        return "SingleThreadCounter{" +
                "count=" + count +
                '}';
    }
}
