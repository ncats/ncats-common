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
 * Common interface for any object that caches as result
 * of some computation that can be reset so that
 * the next time it will re-compute that computation instead
 * of relying on the cached value.
 *
 * @author katzelda
 */
public interface ResetableCache {
    /**
     * Reset the cache so that the next time it will re-compute that computation instead
     * of relying on the cached value.
     */
    void resetCache();
}