package com.xiaosw.core.http.encrypt;

import com.xiaosw.common.util.LogUtil;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * @ClassName {@link AESEncryptManager}
 * @Description AES加解密管理
 *
 * @Date 2018-02-02.
 * @Author xiaosw<xiaosw0802></xiaosw0802>@163.com>.
 */
public class AESEncryptManager {

    private static final String TAG = "AESEncryptManager";

    private static byte[]keyValue;
    private static byte[]iv;

    private static SecretKey key;
    private static AlgorithmParameterSpec paramSpec;
    private static Cipher ecipher;

    static {
        keyValue = getKeyValue();
        iv = getIv();
        if(null != keyValue && null !=iv) {
            KeyGenerator kgen;
            try {
                kgen = KeyGenerator.getInstance("AES");
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG","Crypto");
                random.setSeed(keyValue);
                kgen.init(128,random);
                key =kgen.generateKey();
                paramSpec =new IvParameterSpec(iv);
                ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

            } catch (Exception e) {
                LogUtil.e(TAG, "static initializer: ", e);
            }
        }
    }


    /**加密**/
    public static String encode(String msg) {
        String str ="";
        try {
            //用密钥和一组算法参数初始化此 cipher
            ecipher.init(Cipher.ENCRYPT_MODE,key,paramSpec);
            //加密并转换成16进制字符串
            str = asHex(ecipher.doFinal(msg.getBytes()));
        } catch (Exception e) {
            LogUtil.e(TAG, "encode: ", e);
        }
        return str;
    }
    /**解密**/
    public static String decode(String value) {
        try {
            ecipher.init(Cipher.DECRYPT_MODE,key,paramSpec);
            return new String(ecipher.doFinal(asBin(value)));
        } catch (Exception e) {
            LogUtil.e(TAG, "decode: ", e);
        }
        return"";
    }
    /**转16进制**/
    private static String asHex(byte buf[]) {
        StringBuffer strbuf =new StringBuffer(buf.length * 2);
        int i;
        for (i = 0;i <buf.length;i++) {
            if (((int)buf[i] & 0xff) < 0x10)//小于十前面补零
                strbuf.append("0");
            strbuf.append(Long.toString((int)buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }
    /**转2进制**/
    private static byte[] asBin(String src) {
        if (src.length() < 1)
            return null;
        byte[]encrypted =new byte[src.length() / 2];
        for (int i = 0;i <src.length() / 2;i++) {
            int high = Integer.parseInt(src.substring(i * 2, i * 2 + 1), 16);//取高位字节
            int low = Integer.parseInt(src.substring(i * 2 + 1, i * 2 + 2), 16);//取低位字节
            encrypted[i] = (byte) (high * 16 +low);
        }
        return encrypted;
    }

    ///////////////////////////////////////////////////////////////////////////
    // native method
    ///////////////////////////////////////////////////////////////////////////
    public static native byte[] getKeyValue();
    public static native byte[] getIv();

}
