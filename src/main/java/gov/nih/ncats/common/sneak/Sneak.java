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

package gov.nih.ncats.common.sneak;

/**
 * Implementation of sneakyThrow originally created by Reinier Zwitserloot
 *
 * @see <a href="http://www.mail-archive.com/javaposse@googlegroups.com/msg05984.html">Original Java Posse Post by Reinier Zwitserloot</a>
 * @author dkatzel
 *
 */
public class Sneak {
    public  static <T> T sneakyThrow(Throwable t) {
        if (t == null) {
            throw new NullPointerException("t");
        }
        Sneak.sneakyThrow0(t);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void sneakyThrow0(Throwable t) throws T {
        throw (T) t;
    }
}

