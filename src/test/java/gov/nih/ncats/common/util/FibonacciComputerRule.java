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

import org.junit.Before;
import org.junit.rules.ExternalResource;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class FibonacciComputerRule extends ExternalResource {

    private Map<Integer, BigInteger> fibMap = new HashMap<>();

    @Override
    protected void before() throws Throwable {
        fibMap.clear();
    }


    public BigInteger fibonacci(Integer x) {
        if(x <=1){
            return BigInteger.valueOf(x);
        }
        if (!fibMap.containsKey(x)) {
            fibMap.put(x, fibonacci(x - 2).add(fibonacci(x - 1)));
        }
        return fibMap.get(x);
    }
}
