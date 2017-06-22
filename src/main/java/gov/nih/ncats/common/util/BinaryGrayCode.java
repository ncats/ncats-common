package gov.nih.ncats.common.util;

import java.util.*;
import java.util.function.Consumer;

/**
 * Simpliified implementation of GrayCode
 * using BitSets.  This can only store
 * binary gray code where the values
 * are either on or off, not the
 * more generic n-ary.
 *
 *
 * Created by katzelda on 6/16/17.
 */
public class BinaryGrayCode {
    private final List<BitSet> list;

    /**
     * Create a new Binary Gray Code with the given
     * number of bits.
     * @param numBits the number of bits
     */
    public BinaryGrayCode(int numBits){
        if(numBits< 1){
            throw new IllegalArgumentException("num Bits must be >=1");
        }

        //follows simple algorithm of creating
        //grey code recursively by
        //making a copy of the list, reversing it, then prepending a 1 to each of the copies
        //the original list each gets a 0 prepended to it.
        //finally appending the reversed copied list to the original list
        //
        // 0 , 1  =>  00, 01, 11, 10 => 000, 001, 011, 010, 110, 111, 101, 100

        list = new LinkedList<>();
        BitSet b = new BitSet(numBits);
        b.set(numBits-1);

        list.add( new BitSet(numBits));
        list.add( b);

        for(int i= numBits-2; i>=0; i--){
            //use arrayList for the reversed copy because
            //LinkedList.addAll() uses toArray() so might as well already
            //have an array...
            List<BitSet> reflected = new ArrayList<>(list.size());
            //using listIterator so we can iterate backwards to append reverse list
            ListIterator<BitSet> iter = list.listIterator(list.size());
            while(iter.hasPrevious()){
                BitSet clone = (BitSet) iter.previous().clone();
                clone.set(i); //set the 1
                reflected.add(clone);
            }

            list.addAll(reflected);
        }

    }

    public void traverse(Consumer<BitSet> consumer){
        Objects.requireNonNull(consumer);

        for(BitSet bs : list){
            //make defensive copy
            consumer.accept((BitSet) bs.clone());
        }
    }

    public static void main(String[] args){
        new BinaryGrayCode(3).traverse( bs ->  System.out.println( createPaddedToString(bs)) );


      /*  List<Object> tautomers = new ArrayList<>();
        int maxCount= 10;

        List<MolPath> paths = new ArrayList<>();

        new BinaryGrayCode(paths.size())
                .traverse( bs->{
                    List<MolPath> select = new ArrayList<>();

                    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
                        // operate on index i here
                        if (i == Integer.MAX_VALUE) {
                            break; // or (i+1) would overflow
                        }
                        MolPath pi = paths.get(i);
                        for (int j = bs.nextSetBit(i+1); j >= 0; j = bs.nextSetBit(j+1)) {
                            // operate on index i here
                            if (j == Integer.MAX_VALUE) {
                                break; // or (i+1) would overflow
                            }
                            MolPath pj = paths.get(j);
                            if(pi.intersects(pj)) {
                                // overlapping paths, do nothing
                                return;
                            }
                        }
                        select.add(pi);
                    }
                });*/
    }

    private static String createPaddedToString(BitSet bs) {
        StringBuilder builder = new StringBuilder();
        for(int i=0; i< bs.length(); i++){
            if(bs.get(i)){
                builder.append('1');
            }else{
                builder.append('0');
            }
        }
        //add padding
        for(int i= bs.length(); i<3; i++){
            builder.append('0');
        }
        return builder.toString();
    }
}
