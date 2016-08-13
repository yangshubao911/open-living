package com.shihui.openpf.living.util;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by zhoutc on 2016/5/31.
 */
public class MauthUtil {
    private static String metaKey = "NGQxNmUwMjM4M2Y0MTI2MTM3NDI0Y2MxMjA1N2IyNDM=";
    // for 16 hex char
    private static char[] HEXCHAR = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static void main(String[] args) throws Exception {
        String msg = System.currentTimeMillis() + ":40854";//":1000"
        String token = encode(msg);
        //System.out.println("Authorization=" + token);
        decode(token);
    }

    public static String encode(String msg) throws Exception{
        //生成对称算法
        Cipher cipher = Cipher.getInstance("AES");
        //将自定义密匙字节数据,转换为“AES” 要求的128位(16字节)的密匙字节数据
        // byte[] key = MessageDigest.getInstance("MD5").digest(metaKey.getBytes());
        String buffer = new String(Base64.decodeBase64(metaKey));
        byte[] keyStr = toBytes(buffer);

        //定义密匙
        SecretKeySpec secretKey = new SecretKeySpec(keyStr, "AES");

        //设置算法为加密模式，并且设置密匙
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] data = msg.getBytes();
        //原数据加密，并且返回加密数据
        byte[] encryptData = cipher.doFinal(data);

        System.out.println("encrypt data:" + new String(toHexString(encryptData)));
        return toHexString(encryptData);
    }

    public static void decode(String msg) throws Exception{
        Cipher ecipher = Cipher.getInstance("AES");
        String buffer = new String(Base64.decodeBase64(metaKey));
        byte[] keyStr = toBytes(buffer);
        SecretKeySpec aesKey = new SecretKeySpec(keyStr, "AES");
        ecipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] bytes = ecipher.doFinal(toBytes(msg));
        System.out.println(new String(bytes));
    }

    /**
     * 据高智说这样特殊处理的原因是为了兼容ios
     *
     * @param s
     * @return
     */
    static final byte[] toBytes(String s) {
        byte[] bytes;
        bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2),
                    16);
        }
        return bytes;
    }

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEXCHAR[(b[i] & 0xf0) >>> 4]);
            sb.append(HEXCHAR[b[i] & 0x0f]);
        }
        return sb.toString();
    }
}

