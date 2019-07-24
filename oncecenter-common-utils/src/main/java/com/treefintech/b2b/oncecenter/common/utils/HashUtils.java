package com.treefintech.b2b.oncecenter.common.utils;

import org.bouncycastle.jcajce.provider.digest.Keccak;

import java.security.MessageDigest;


/**
 * 获取hash值
 */
public class HashUtils {

    /**
     * 获取这个字节数据的hash值，可以用于文件
     */
    public static String sha3(byte[] input) throws IllegalArgumentException {
        if (input.length <= 0) {
            throw new IllegalArgumentException();
        } else {


            byte[] output = sha3(input, 0, input.length);
            return toHexString(output, 0, output.length, false);
        }
    }

    public static byte[] sha3(byte[] input, int offset, int length) {
        Keccak.DigestKeccak keccak = new Keccak.Digest256();
        keccak.update(input, offset, length);
        return keccak.digest();
    }



    public static String toHexString(byte[] input, int offset, int length, boolean withPrefix) {
        StringBuilder stringBuilder = new StringBuilder();
        if (withPrefix) {
            stringBuilder.append("0x");
        }

        for(int i = offset; i < offset + length; ++i) {
            stringBuilder.append(String.format("%02x", input[i] & 255));
        }

        return stringBuilder.toString();
    }

    public static String getHashValue(byte[] input) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(input);
            return Base64.encode(hash, "UTF-8");
        } catch (Exception e) {

        }
        return "";
    }


}
