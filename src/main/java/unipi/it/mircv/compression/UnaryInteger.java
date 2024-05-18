package unipi.it.mircv.compression;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class UnaryInteger {

    public static byte[] encodeToUnary(ArrayList<Integer> uncompressed) {

        // count the number of bits needed to store the compressed array
        int bits = 0;
        for (Integer integer : uncompressed) {
            // check if 0 or negative
            if (integer <= 0) {
                continue;
            }
            bits += integer;
        }

        // allocate the byte array for the compressed array (round up to the next byte)
        byte[] compressed = new byte[(int) Math.ceil(bits / 8.0)];

        // compress each number n with n-1 1s and a 0 at the end
        int index = 0;
        int count = 0;
        for (Integer integer : uncompressed) {
            // check if 1
            if (integer == 1) {
                // set to 0 the bit at position count
                compressed[index] |= (byte) (0 << (7 - count));
                count++;
                // check if the byte is full
                if (count == 8) {
                    // reset count and move to the next byte
                    index++;
                    count = 0;
                }
                continue;
            }
            // add n-1 1s
            for (int j = 0; j < integer - 1; j++) {
                // set to 1 the bit at position count
                compressed[index] |= (byte) (1 << (7 - count));
                count++;

                if (count == 8) {
                    index++;
                    count = 0;
                }
            }
            // add a 0 at the end of the sequence
            compressed[index] |= (byte) (0 << (7 - count));
            count++;

            if (count == 8) {
                index++;
                count = 0;
            }
        }

        // check if the last byte is full
        if (count != 0) {
            // fill the last bits with 1s
            for (int i = count; i < 8; i++) {
                compressed[index] |= (byte) (1 << (7 - i));
            }
        }
        return compressed;
    }

    // Encode integer to unary
//    public static byte[] encodeToUnary(int number) {
//        if (number < 0) {
//            throw new IllegalArgumentException("Unary encoding only supports non-negative integers.");
//        }
//
//        ArrayList<Byte> encodedList = new ArrayList<>();
//
//        // Add '1's to the list equal to the number
//        for (int i = 0; i < number; i++) {
//            encodedList.add((byte) 1);
//        }
//
//        // Add terminating '0'
//        encodedList.add((byte) 0);
//
//        // Convert ArrayList to byte array
//        byte[] encodedArray = new byte[encodedList.size()];
//        for (int i = 0; i < encodedList.size(); i++) {
//            encodedArray[i] = encodedList.get(i);
//        }
//
//        return encodedArray;
//    }

    // Decode unary to integer
//    public static int decodeFromUnary(byte[] unaryBytes) {
//        int count = 0;
//        int index = 0;
//
//        // Count consecutive '1's until '0' is encountered
//        while (index < unaryBytes.length && unaryBytes[index] == 1) {
//            count++;
//            index++;
//        }
//
//        // Check if the sequence ends with '0'
//        if (index == unaryBytes.length || unaryBytes[index] == 0) {
//            return count;
//        } else {
//            throw new IllegalArgumentException("Invalid unary encoding.");
//        }
//    }

    // decompress an array of bytes
    public static ArrayList<Integer> decodeFromUnary(byte[] compressed) {

        // allocate the array for the decompressed array
        ArrayList<Integer> decompressed = new ArrayList<>();

        // decompress each byte
        int count = 0;
        for (int i = 0; i < compressed.length; i++) {
            for (int j = 0; j < 8; j++) {
                // check if the bit at position j is 1
                if ((compressed[i] & (1 << (7 - j))) != 0) {
                    // increment the counter
                    count++;
                } else {
                    // add the number to the array (number of 1s + 1)
                    decompressed.add(count + 1);
                    // reset the counter
                    count = 0;
                }
            }
        }
        return decompressed;
    }
//
//    public static int writeTermFreqCompressed(ArrayList<Integer> freqList, FileChannel channelTermFreq) throws IOException {
//        byte[] compressed = UnaryInteger.encodeToUnary(freqList);
//        int i = 0;
//
//        // allocate ByteBuffer for writing termFreqs
//        channelTermFreq.position(channelTermFreq.size());
//        ByteBuffer freqsByteBuffer = ByteBuffer.wrap(compressed);
//
//        // write termFreqs to the buffers
//        while (freqsByteBuffer.hasRemaining())
//            channelTermFreq.write(freqsByteBuffer);
//
//        // return the number of bytes written
//        return compressed.length;
//    }
//
//    public static ArrayList<Integer> readTermFreqCompressed(FileChannel channelTermFreq , long offsetTermFreq, int termFreqLen) throws IOException {
//        try {
//            // array for decompressed termFreqs
//            ArrayList<Integer> TFlistDecompressed = new ArrayList<>();
//            // set position
//            channelTermFreq.position(offsetTermFreq);
//
//            // creating ByteBuffer for reading termFreqs
//            ByteBuffer bufferTermFreq = ByteBuffer.allocate(termFreqLen );
//
//            // reading termFreqs from channel
//            while (bufferTermFreq.hasRemaining())
//                channelTermFreq.read(bufferTermFreq);
//
//            bufferTermFreq.rewind(); // reset the buffer position to 0
//
//            // reading termFreqs from buffer
//            TFlistDecompressed = decodeFromUnary(bufferTermFreq.array());
//
//            // return the decompressed termFreqs
//            return TFlistDecompressed;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
    public static void main(String[] args) {
//        int number = 7;
//        byte[] encoded = encodeToUnary(number);
//        System.out.println("Encoded: " + new String(encoded));
//        int decoded = decodeFromUnary(encoded);
//        System.out.println("Decoded: " + decoded);
    }
//        return decompressed;

//    }

}
