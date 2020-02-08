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

package gov.nih.ncats.common.functions;

import gov.nih.ncats.common.sneak.Sneak;

import java.util.function.Function;

/**
 * Created by katzelda on 5/30/19.
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Throwable> {

    R apply(T t) throws E;

    static <T, E extends Throwable> ThrowableFunction<T,T,E> identity(){
        return t-> t;
    }


    static <T, R, E extends Throwable> ThrowableFunction<T,R,E> wrap(Function<T,R> function){
        return t-> function.apply(t);
    }

    default Function<T, R> asFunction(){
        return t-> {
            try{
                return apply(t);
            }catch(Throwable ex){
                Sneak.sneakyThrow(ex);
                return null;
            }
        };
    }
}
