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
 * Utility class for working with {@link java.util.Map}s.
 * @author dkatzel
 *
 */
public final class MapUtil {
	
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	private MapUtil(){
		//can not instantiate
	}
	/**
	 * Computes the smallest possible 
	 * size of a {@link java.util.HashMap} using the default
	 * load factor that will not cause the map to 
	 * perform an expensive rehashing operation (which also
	 * doubles the number of buckets used).
	 * @param expectedNumberOfEntries the expected number
	 * of entries that will be put into the map.
	 * This type is long to help clients avoid
	 * casting but the max value allowed is {@link Integer#MAX_VALUE}.
	 * Passing in a value greater than {@link Integer#MAX_VALUE} will
	 * throw an Exception.  (This is better than having
	 * clients downcast and possibly truncate the value without any error).
	 * @return the initial size value to pass to the HashMap constructor.
	 * @throws IllegalArgumentException if expectedNumberOfEntries is &lt; 0 or
	 * &gt; {@link Integer#MAX_VALUE}.
	 */
	public static int computeMinHashMapSizeWithoutRehashing(long expectedNumberOfEntries){
		return computeMinHashMapSizeWithoutRehashing(expectedNumberOfEntries, DEFAULT_LOAD_FACTOR);
	}
	
	/**
	 * Computes the smallest possible 
	 * size of a {@link HashMap} using the default
	 * load factor that will not cause the map to 
	 * perform an expensive rehashing operation (which also
	 * doubles the number of buckets used).
	 * 
	 * @param expectedNumberOfEntries the expected number
	 * of entries that will be put into the map.
	 * This type is long to help clients avoid
	 * casting but the max value allowed is {@link Integer#MAX_VALUE}.
	 * Passing in a value greater than {@link Integer#MAX_VALUE} will
	 * throw an Exception.  (This is better than having
	 * clients downcast and possibly truncate the value without any error).
	 * 
	 * @param loadFactor is a measure of how full the hash table is 
	 * allowed to get before its capacity is automatically increased.
	 * 
	 * @return the initial size value to pass to the HashMap constructor.
	 * 
	 * @throws IllegalArgumentException if expectedNumberOfEntries is &lt; 0 or
	 * &gt; {@link Integer#MAX_VALUE}.
	 */
	public static int computeMinHashMapSizeWithoutRehashing(long expectedNumberOfEntries, float loadFactor){
		if(expectedNumberOfEntries <0){
			throw new IllegalArgumentException("number of entries must be >=0");
		}
		if(expectedNumberOfEntries > Integer.MAX_VALUE){
			throw new IllegalArgumentException("number of entries must be <= Integer.MAX_VALUE");
		}
		if(loadFactor <0 || loadFactor> 1){
			throw new IllegalArgumentException("invalid load factor, must be between 0 and 1 : "+ loadFactor);
		}
		return (int)(expectedNumberOfEntries/loadFactor +1);
	}
}
