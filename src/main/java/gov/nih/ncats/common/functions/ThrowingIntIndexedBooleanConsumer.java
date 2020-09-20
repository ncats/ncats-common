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

package gov.nih.ncats.common.functions;
/**
 * Functional interface that takes 2 parameters, a primitive index (offset)
 * and the boolean value at that offset.
 * @author dkatzel
 *
 * @param <E> the exception that could be thrown.
 */
@FunctionalInterface
public interface ThrowingIntIndexedBooleanConsumer<E extends Throwable> {

    void accept(int index, boolean value) throws E;
}
