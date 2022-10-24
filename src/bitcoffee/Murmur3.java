package bitcoffee;

import java.math.BigInteger;

public class Murmur3 {

    static final BigInteger C1 = new BigInteger(1, Kit.hexStringToByteArray("cc9e2d51"));
    static final BigInteger C2 = new BigInteger(1, Kit.hexStringToByteArray("1b873593"));
    static final BigInteger f8 = new BigInteger(1, Kit.hexStringToByteArray("ffffffff"));

    public static final int UNSIGNED_MASK = 0xff;


    /**
     * Compute the bitcoffee.Murmur3 hash as described in the original source code.
     *
     * @param data
     *            the data that needs to be hashed
     *
     * @param length
     *            the length of the data that needs to be hashed
     *
     * @param seed
     *            the seed to use to compute the hash
     *
     */
    public static BigInteger hash_x86_32(final byte[] data, int length, long seed) {
        final int nblocks = length >> 2;
        BigInteger h1 = BigInteger.valueOf(seed);

        //----------
        // body
        for(int i = 0; i < nblocks; i++) {
            final int i4 = i << 2;

            long k1 = (data[i4] & UNSIGNED_MASK);
            k1 |= (data[i4 + 1] & UNSIGNED_MASK) << 8;
            k1 |= (data[i4 + 2] & UNSIGNED_MASK) << 16;
            k1 |= (long) (data[i4 + 3] & UNSIGNED_MASK) << 24;

            BigInteger K1 = BigInteger.valueOf(k1);

            K1 = K1.multiply(C1);
            K1 = rotl32(K1, 15);
            K1 = K1.multiply(C2);

            h1 = h1.xor(K1);
            h1 = rotl32(h1,13);
            h1 = h1.multiply(BigInteger.valueOf(5)).add(new BigInteger(1, Kit.hexStringToByteArray("e6546b64")));
        }
        //----------
        // tail

        // Advance offset to the unprocessed tail of the data.
        int offset = (nblocks << 2); // nblocks * 2;
        var K1 = BigInteger.ZERO;

        long k1 = 0;

        switch (length & 3) {
            case 3:
                k1 = (data[offset + 2] & UNSIGNED_MASK) << 16;

            case 2:
                k1 |= (data[offset + 1] & UNSIGNED_MASK) << 8;

            case 1:
                K1 = BigInteger.valueOf(k1);
                K1 = K1.or(BigInteger.valueOf(data[offset] & UNSIGNED_MASK));
                K1 = K1.multiply(C1);
                K1 = rotl32(K1, 15);
                K1 = K1.multiply(C2);
                h1 = h1.xor(K1);
        }

        // ----------
        // finalization

        h1 = h1.xor(BigInteger.valueOf(length));
        var hash = fmix32(h1);

        hash = hash.and(f8);

        return hash;

    }

    /**
     * Rotate left for 32 bits.
     *
     * @param original
     * @param shift
     * @return
     */
    private static BigInteger rotl32(BigInteger original, int shift) {
        return original.shiftLeft(shift).or(original.and(f8).shiftRight(32 - shift));
    }

    private static BigInteger fmix32(BigInteger h) {
        h = h.xor(h.and(f8).shiftRight(16));
        h = h.multiply(new BigInteger(1, Kit.hexStringToByteArray("85ebca6b")));
        h = h.xor(h.and(f8).shiftRight(13));
        h = h.multiply(new BigInteger(1, Kit.hexStringToByteArray("c2b2ae35")));
        h = h.xor(h.and(f8).shiftRight(16));

        return h;
    }


}