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

package gov.nih.ncats.common.iter;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

public final class IteratorUtil {

    private IteratorUtil(){
        //can not instantiate
    }

    public static <T> Iterator<T> createEmptyIterator() {
        return EmptyIterator.INSTANCE;
    }


    private static <T,R> Iterator<R> map(Iterator<T> orig, Function<T, R> mapperFunction){
        Objects.requireNonNull(orig);
        Objects.requireNonNull(mapperFunction);
        return new Iterator<R>() {
            private Iterator<T> iter = orig;
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public R next() {
                return mapperFunction.apply(iter.next());
            }

            @Override
            public void remove() {
                iter.remove();
            }
        };
    }

    /**
     * {@code EmptyIterator} is a NullObject implementation
     * of {@link Iterator}; an EmptyIterator will never
     * have any elements to iterate over.
     * @author dkatzel
     *
     *
     */
    private static final class EmptyIterator<E> implements Iterator<E> {
        /**
         * Singleton instance of Empty iterator that can be shared
         * by all.
         */
        @SuppressWarnings("rawtypes")
        static final EmptyIterator INSTANCE  = new EmptyIterator();
        /**
         * Private constructor so no one can subclass.
         */
        private EmptyIterator(){}
        /**
         * Never has a next.
         * @return {@code false}
         */
        @Override
        public boolean hasNext() {
            return false;
        }
        /**
         * Will always throw an NoSuchElementException.
         * @throws NoSuchElementException because there will never be a next.
         */
        @Override
        public E next() {
            throw new NoSuchElementException("no elements in empty iterator");
        }
        /**
         * Does nothing.
         */
        @Override
        public void remove() {
            //no-op
        }

    }
}
