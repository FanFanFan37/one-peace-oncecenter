package com.treefintech.b2b.oncecenter.common.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密算法工具类
 *
 * @author shiyc
 * <p/>
 * 2013-3-8
 */
public class MD5Util {

    private static MD5Util instance = null;

    public static MD5Util getInstance() {
        if (instance == null) {
            instance = new MD5Util();
        }
        return instance;
    }

    /**
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
     */
    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    protected static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    /**
     * <p> 加密字符串 </p>
     *
     * @param str
     * @return
     */
    public String getStringMD5(String str) {
        return getStringMD5(str, null);
    }

    /**
     * <p> 加密字符串 </p>
     *
     * @param str
     * @return
     */
    public String getStringMD5(String str, String charset) {
        byte[] buffer;
        if (charset == null) {
            buffer = str.getBytes();
        } else {
            try {
                buffer = str.getBytes(charset);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        messagedigest.update(buffer);
        return bufferToHex(messagedigest.digest());
    }

    public String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString().toUpperCase();
    }

    private void appendHexPair(byte bt, StringBuffer stringbuffer) {
        // 取字节中高 4 位的数字转换
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        // 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        // 取字节中低 4 位的数字转换
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

}
