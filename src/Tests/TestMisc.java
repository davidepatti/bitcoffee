package Tests;

import bitcoffee.*;

import java.math.BigInteger;

public class TestMisc {

    public static void main(String[] args) {

        // from hex string to bytes:

        // notice: n1 has the first bit 0, while n2 has 1
        String n1_hex = "7fab";
        String n2_hex = "8fab";
        // both numbers are created as positive numbers, so the first bit is not considered as sign (good)
        var n1 = new BigInteger(n1_hex,16);
        var n2 = new BigInteger(n2_hex,16);

        // when reconverting to bytes
        // n1 is 2 bytes: 127, -85
        // n2 results in 3 bytes, with the first 0, to avoid negative sign
        // 0 -113, -85
        var n1_b = n1.toByteArray();
        var n2_b = n2.toByteArray();

        byte[] s1 = Kit.hexStringToByteArray(n1_hex);
        byte[] s2 = Kit.hexStringToByteArray(n2_hex);

    }
}
