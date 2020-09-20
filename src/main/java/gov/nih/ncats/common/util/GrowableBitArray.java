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



import gov.nih.ncats.common.functions.ThrowingIntIndexedBooleanConsumer;

import java.util.Arrays;
import java.util.BitSet;
import java.util.stream.IntStream;

/**
 * A {@code GrowableBitArray} is a utility class
 * that wraps an array that will dynamically
 * grow as needed when data is
 * appended, inserted, replaced and removed etc.
 * This is similar to an {@link java.util.ArrayList}
 * or {@link StringBuilder}
 * for primitive bits.
 * This class is not Thread-safe.
 * @author dkatzel
 *
 */
public final class GrowableBitArray {
	/**
	 * The current length of valid data
	 * this is not the same as the length
	 * of the byte array (capacity) since
	 * there still might be room to grow.
	 * There might even be old data in the array
	 * past current length if the array
	 * has been modified via the {@link #remove(int)}
	 * methods.
	 */
	private int currentLength=0;
	/**
	 * Our actual byte array,
	 * the capacity is the size of the array.
	 */
	private boolean[] data;
	/**
	 * Creates a new {@link GrowableBitArray}
	 * with the given initial capacity.
	 * @param initialCapacity the initial size 
	 * of the backing byte array.  When adding
	 * bytes will cause the byte array to overflow,
	 * the backing byte array will automatically
	 * grow larger.
	 * @throws IllegalArgumentException if initialCapacity is <=0.
	 */
	public GrowableBitArray(int initialCapacity){
		if(initialCapacity <=0){
			throw new IllegalArgumentException("initial capacity should be > 0 :"+initialCapacity);
		}
		data = new boolean[initialCapacity];		
	}

    /**
     * Creates a new {@link GrowableBitArray}
     * where the backing byte array is an exact
     * copy of the input BitSet and the initial
     * capacity is set to the BitSet's logical length.
     *
     * @param bs the input BitSet whose "on" values to set
     * to the backing array.
     * @throws NullPointerException if the BitSet is null.
     *
     *
     */
	public GrowableBitArray(BitSet bs){
	    data = new boolean[bs.length()];
        currentLength=data.length;

        for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
            data[i] = true;
            if (i == Integer.MAX_VALUE) {
                break; // or (i+1) would overflow
            }

        }
    }
	/**
	 * Creates a new {@link GrowableBitArray}
	 * where the backing byte array is an exact
	 * copy of the input array and the initial
	 * capacity is set to the array length.
	 * This has similar (although optimized)
	 * functionality to
	 * <pre>
	 * byte[] bytes = ...
	 * GrowableBitArray gba = new GrowableBitArray(bytes.length);
	 * gba.append(bytes);
	 * </pre>
	 * @param bytes the initial byte values to set
	 * to the backing array.
	 * @throws NullPointerException if bytes is null.
	 */
	public GrowableBitArray(boolean[] bytes){
		data = Arrays.copyOf(bytes, bytes.length);
		currentLength=data.length;
	}
	private GrowableBitArray(GrowableBitArray copy){
		data = Arrays.copyOf(copy.data, copy.data.length);
		currentLength = copy.currentLength;
	}
	/**
	 * Create a new instance of GrowableBitArray
	 * that is an exact copy of this instance.
	 * Any future modifications to either the original
	 * instance or the copy will NOT be reflected 
	 * in the other.
	 * @return a new instance of GrowableBitArray
	 * that contains the same data as this instance
	 * currently does.
	 */
	public GrowableBitArray copy(){
		return new GrowableBitArray(this);
	}
	private void assertValidOffset(int offset) {
		if (offset <0 || offset >= currentLength){
		    throw new IndexOutOfBoundsException(
			"Index: "+offset+", Size: "+currentLength);
	    }
	}
	private void assertValidRange(Range range) {
		if (range.getBegin()<0 || range.getEnd() >= currentLength){
		    throw new IndexOutOfBoundsException(
			"range: "+range+", array size: "+currentLength);
	    }
	}
	
	public void reverse(){
		int pivotPoint = currentLength/2;
		for(int i=0; i<pivotPoint;i++){
			boolean temp=data[i];
			int reverseI = currentLength-i-1;
			data[i] = data[reverseI];
			data[reverseI] = temp;
		}
	}
	public int getCurrentLength() {
		return currentLength;
	}

	public void append(boolean value){
		ensureCapacity(currentLength+1);
		data[currentLength++]=value;
	}
	
	public void append(boolean[] values){
		ensureCapacity(currentLength+values.length);
		System.arraycopy(values, 0, data, currentLength, values.length);
		currentLength+=values.length;
	}
	public void append(GrowableBitArray other){
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(other.data, 0, data, currentLength, other.currentLength);
		currentLength+=other.currentLength;
	}
	public boolean get(int offset){
		assertValidOffset(offset);
		return data[offset];
	}
	
	public void prepend(boolean value){
		insert(0,value);
	}
	
	public void prepend(boolean[] values){
		insert(0,values);
	}
	public void prepend(GrowableBitArray other){
		insert(0,other);
	}
	public void replace(int offset, boolean value){
		assertValidOffset(offset);
		data[offset]=value;
	}
	public void insert(int offset, boolean[] values){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+values.length);
		System.arraycopy(data, offset, data, offset + values.length,
				currentLength - offset);
		
		System.arraycopy(values, 0, data, offset, values.length);
		currentLength+=values.length;
		
	}
	
	public void insert(int offset, GrowableBitArray other){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+other.currentLength);
		System.arraycopy(data, offset, data, offset + other.currentLength,
				currentLength - offset);
		
		System.arraycopy(other.data, 0, data, offset, other.currentLength);
		currentLength+=other.currentLength;
		
	}
	public void insert(int offset, boolean value){
		assertValidInsertOffset(offset);
		ensureCapacity(currentLength+1);
		System.arraycopy(data, offset, data, offset + 1,
				currentLength - offset);
		data[offset] = value;
		currentLength++;
	}
	private void assertValidInsertOffset(int offset) {
		//inserts allow offset to be length
		if(offset !=currentLength){
			assertValidOffset(offset);
		}
		
		
	}

	public boolean remove(int offset){
		assertValidOffset(offset);
		boolean oldValue = data[offset];

		int numMoved = currentLength - offset - 1;
		if (numMoved > 0){
		    System.arraycopy(data, offset+1, data, offset,    numMoved);
		}
		currentLength--;
		return oldValue;
	}
	
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = data.length;
		if (minCapacity > oldCapacity) {
		    //algorithm borrowed from ArrayList
		    int newCapacity = (oldCapacity * 3)/2 + 1;
    	    if (newCapacity < minCapacity){
    	    	newCapacity = minCapacity;
    	    }
            // minCapacity is usually close to size, so this is a win:
            data = Arrays.copyOf(data, newCapacity);
		}
    }
	
	public boolean[] toArray(){
		return Arrays.copyOf(data,currentLength);
	}

    /**
     * Get an {@link IntStream} of all the array offsets
     * for the bits that are set to true.
     * @return an IntStream that may be empty if all values are set to false.
     *
     *
     */
	public IntStream onBitsAsStream(){
       GrowableIntArray ons = new GrowableIntArray();
        forEachIndexed((i, v) -> {
            if(v){
                ons.append(i);
            }
        });

       return ons.stream();
    }

    /**
     * Create a new BitSet with the same "on" values
     * as currently stored in this growable array.
     * The returned BitSet is a completely separate and detached object;
     * any changes to this array or the returned BitSet are NOT reflected in the other.
     * @return a new BitSet; will never be null.
     *
     *
     */
    public BitSet asBitSet(){
	    BitSet bs = new BitSet(currentLength);
	    onBitsAsStream().forEach(bs::set);
	    return bs;
    }
    /**
     * Iterate over each element in the array and call the given consumer
     * which captures the offset and the value.
     * @param consumer the consumer of each element; can not be null.
     * @param <E> the Throwable that might be thrown by the consumer.
     * @throws E the Throwable from the consumer.
     *
     *
     */
    public <E extends Throwable> void forEachIndexed(ThrowingIntIndexedBooleanConsumer<E> consumer) throws E{
        for(int i=0; i< currentLength; i++){
            consumer.accept(i, data[i]);
        }
    }

    /**
     * Iterate over the elements in the given range of this array and call the given consumer
     * which captures the offset and the value.
     * @param consumer the consumer of each element; can not be null.
     * @param <E> the Throwable that might be thrown by the consumer.
     * @throws E the Throwable from the consumer.
     *
     *
     */
    public <E extends Throwable> void forEachIndexed(Range range, ThrowingIntIndexedBooleanConsumer<E> consumer) throws E{
        int end = (int) Math.min(currentLength, range.getEnd()+1);
        for(int i=(int) range.getBegin(); i< end; i++){
            consumer.accept(i, data[i]);
        }
    }
}
