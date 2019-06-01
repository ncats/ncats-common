/*
 *     NCATS-COMMON
 *
 *     Written in 2019 by NIH/NCATS
 *
 *     To the extent possible under law, the author(s) have dedicated all copyright and related and neighboring rights to this software to the public domain worldwide. This software is distributed without any warranty.
 *
 *     You should have received a copy of the CC0 Public Domain Dedication along with this software. If not, see <http://creativecommons.org/publicdomain/zero/1.0/>.
 */

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
