package com.treefintech.b2b.oncecenter.common.utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * 
 * <pre>
 * Modify Information:
 * Author       Date        Description
 * ============ =========== ============================
 * zhangxing    2009-05-04  Create this file
 * 
 * </pre>
 * 
 */
public class Base64 {
    private static final Base64Encoder encoder = new Base64Encoder();

    /**
     * encode the input data producing a base 64 encoded byte array.
     * 
     * @return a byte array containing the base 64 encoded data.
     */
    public static byte[] encode(byte[] data) {
        int len = (data.length + 2) / 3 * 4;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(len);

        try {
            encoder.encode(data, 0, data.length, outputStream);
        } catch (IOException e) {
            throw new RuntimeException("exception encoding base64 string: " + e);
        }

        return outputStream.toByteArray();
    }

    public static String toBase64String(byte[] data) throws Exception {
        byte[] result = encode(data);

        return new String(result, "utf-8");
    }

    /**
     * Encode the byte data to base 64 writing it to the given output stream.
     * 
     * @return the number of bytes produced.
     */
    public static int encode(byte[] data, OutputStream out) throws IOException {
        return encoder.encode(data, 0, data.length, out);
    }

    /**
     * Encode the byte data to base 64 writing it to the given output stream.
     * 
     * @return the number of bytes produced.
     */
    public static int encode(byte[] data, int off, int length, OutputStream out) throws IOException {
        return encoder.encode(data, off, length, out);
    }

    /**
     * decode the base 64 encoded input data. It is assumed the input data is
     * valid.
     * 
     * @return a byte array representing the decoded data.
     */
    public static byte[] decode(byte[] data) {
        int len = data.length / 4 * 3;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);

        try {
            encoder.decode(data, 0, data.length, bOut);
        } catch (IOException e) {
            throw new RuntimeException("exception decoding base64 string: " + e);
        }

        return bOut.toByteArray();
    }

    /**
     * decode the base 64 encoded String data - whitespace will be ignored.
     * 
     * @return a byte array representing the decoded data.
     */
    public static byte[] decode(String data) {
        int len = data.length() / 4 * 3;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream(len);

        try {
            encoder.decode(data, bOut);
        } catch (IOException e) {
            throw new RuntimeException("exception decoding base64 string: " + e);
        }

        return bOut.toByteArray();
    }

    /**
     * decode the base 64 encoded String data writing it to the given output
     * stream, whitespace characters will be ignored.
     * 
     * @return the number of bytes produced.
     */
    public static int decode(String data, OutputStream out) throws IOException {
        return encoder.decode(data, out);
    }

    /**
     * encode data to base64String
     * 
     * @param data
     * @param charSet
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encode(String data, String charSet) throws UnsupportedEncodingException {
        if (data == null || "".equals(data.trim())) {
            return "";
        } else {
            return new String(encode(data.getBytes(charSet)), charSet);
        }
    }

    public static String encode(byte[] data, String charSet) throws UnsupportedEncodingException {
        return new String(encode(data), charSet);
    }

    /**
     * decode data to base64String
     * 
     * @param data
     * @param charSet
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decode(String data, String charSet) throws UnsupportedEncodingException {
        if (data == null || "".equals(data.trim())) {
            return "";
        } else {
            return new String(decode(data.getBytes(charSet)), charSet);
        }
    }
}

