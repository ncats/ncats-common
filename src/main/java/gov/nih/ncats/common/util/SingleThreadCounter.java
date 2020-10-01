/*
 * NCATS-COMMON
 *
 * Copyright 2020 NIH/NCATS
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
