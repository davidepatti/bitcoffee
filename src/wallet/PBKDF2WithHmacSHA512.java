package wallet;
/*
 * The MIT License (MIT)
 * Copyright (c) <2015> <Suraj Kumar>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * <p>
 * This class is used for encrypting passwords using the PBKDF2WithHmacSHA1
 * algorithm. Passwords are salted using SHA1PRNG.
 * </p>
 *
 * <a href=
 * "http://csrc.nist.gov/publications/nistpubs/800-132/nist-sp800-132.pdf"
 * >Specification referenced</a>.<br>
 * <a href="http://tools.ietf.org/search/rfc2898">RFC2898 - Password-Based
 * Cryptography Specification</a>
 *
 * @author Suraj Kumar <k975@live.co.uk>
 * @version 2.0
 */
public final class PBKDF2WithHmacSHA512 {
    /**
     * This is the algorithm this service uses.
     */
    private static final String ALGORITHM = PBKDF2WithHmacSHA512.class.getSimpleName();

    /**
     * The amount of computation needed to derive a key from the password. Note:
     * The bigger the number the longer it'll take to a generate key. Note: When
     * user based performance is not an issue, a value of 10,000,000 is
     * recommended otherwise a minimum of 1000 recommended.
     */
    private static final int ITERATION_COUNT = 2048;

    /**
     * The length of the derived key.
     */
    private static final int KEY_LENGTH = 512;

    /**
     * Private constructor to stop the class from being instantiated.
     *
     * @throws AssertionError If the class tried to be instantiated.
     */
    private PBKDF2WithHmacSHA512() {
        throw new AssertionError();
    }

    /**
     * This method returns an encrypted byte[] of the password.
     *
     * @param password
     *            The password to encrypt.
     * @param SALT
     *            The random data used for the hashing function.
     * @return The encrypted password as a byte[].
     * @throws NoSuchAlgorithmException
     *             If the cryptographic algorithm is unavailable.
     * @throws InvalidKeySpecException
     *             If the derived key cannot be produced.
     */
    public static byte[] hash(final String password, final byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        final SecretKeyFactory secretKeyfactory = SecretKeyFactory.getInstance(ALGORITHM);
        return secretKeyfactory.generateSecret(keySpec).getEncoded();
    }

    /**
     * Generates a random salt used for password matching.
     *
     * @return A randomly produced byte[].
     *
     * @throws NoSuchAlgorithmException
     *             If SHA1PRNG does not exist on the system.
     */
    public static byte[] salt() throws NoSuchAlgorithmException {
        final byte[] salt = new byte[16];
        SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
        return salt;
    }

    /**
     * This method takes a byte[] and converts it to a String.
     * E.g. byte[] test = {1, 2, 3, 4} this method will convert that
     * into a String that looks like this: "1 2 3 4";
     *
     * This allows you to save an array to a database easily.
     *
     * @param payload The byte[] to convert to a String
     * @return The converted byte[] as a String
     */
    public static String convertToString(final byte[] payload) {
        String result = "";
        for (byte b : payload) {
            result += b + " ";
        }
        return result.trim();
    }

    /**
     * This method is specific to this password hashing service. It converts a
     * String (assuming it contains suitable values that can be converted to a
     * byte) to a byte[].
     *
     * @param s
     *            The String to convert
     * @return The converted String to a byte[]
     */
    public static byte[] toByteArray(final String s) {
        final String[] arr = s.split(" ");
        final byte[] b = new byte[arr.length];
        for (int i = 0; i < arr.length; i++) {
            b[i] = Byte.parseByte(arr[i]);
        }
        return b;
    }

    /**
     * Checks the attemptedPassword against the encryptedPassword using the
     * random salt.
     *
     * @param attemptedPassword
     *            The password entered by the user.
     * @param encryptedPassword
     *            The hashed password stored on the database.
     * @param SALT
     *            The salt to use
     * @return If the attempted password matched the hashed password.
     * @throws Exception
     *             If the algorithm cannot be performed.
     */
    public static boolean authenticate(final String attemptedPassword, final byte[] salt, final byte[] hashedPassword) throws Exception {
        return Arrays.equals(hash(attemptedPassword, salt), hashedPassword);
    }
}