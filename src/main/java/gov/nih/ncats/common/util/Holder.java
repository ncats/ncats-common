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

    public static <T> Holder<T> empty(){
        return new Holder<>(null);
    }
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
