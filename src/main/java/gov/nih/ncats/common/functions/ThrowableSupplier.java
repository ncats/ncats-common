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

import java.util.function.Supplier;

/**
 * Created by katzelda on 6/4/19.
 */
@FunctionalInterface
public interface ThrowableSupplier<T, E extends Throwable> {

    T get() throws E;

    static <T, E extends Throwable>  ThrowableSupplier<T,E> wrap(Supplier<T> supplier){
        return ()-> supplier.get();
    }

    /**
     * Wraps this Throwing Supplier in a normal supplier that
     * will Sneakily throw any Throwable thown during the get call.
     * @return a new Supplier that wraps this ThrowableSupplier.
     */
    default Supplier<T> asSupplier(){
        return ()-> {
            try{
                return get();
            }catch(Throwable t){
                return Sneak.sneakyThrow(t);
            }
        };
    }
}
