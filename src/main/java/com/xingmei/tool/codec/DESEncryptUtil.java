package com.xingmei.tool.codec;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * des对称加密
 */
public class DESEncryptUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(DESEncryptUtil.class);

    /**
     * 加密
     *
     * @param data 源数据
     * @param key  秘钥
     * @return
     */
    public static String encrypt(String data, String key) {
        if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
            LOGGER.info("参数和密钥不允许为空");
            return null;
        }
        byte[] bytes = encryptOrDecrypt(Cipher.ENCRYPT_MODE, data.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
        // base64编码字节
        return new String(new Base64().encode(bytes), StandardCharsets.UTF_8);
    }

    /**
     * 解密
     *
     * @param data 源数据
     * @param key  秘钥
     * @return
     */
    public static String decrypt(String data, String key) {
        if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
            LOGGER.info("参数和密钥不允许为空");
            return null;
        }
        byte[] src = new Base64().decode(data);
        byte[] bytes = encryptOrDecrypt(Cipher.DECRYPT_MODE, src, key.getBytes(StandardCharsets.UTF_8));
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static byte[] encryptOrDecrypt(int mode, byte[] data, byte[] key) {
        try {
            // 强随机数生成器 (RNG)
            SecureRandom random = new SecureRandom();
            // DESKeySpec是一个成加密密钥的密钥内容的（透明）规范的接口。
            DESKeySpec desKey = new DESKeySpec(key);
            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // 得到密钥对象SecretKey
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            // 用密匙初始化Cipher对象
            cipher.init(mode, securekey, random);
            // 现在，获取数据并加密，正式执行加密操作
            return cipher.doFinal(data);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.put("首联", "一叶扁舟伴水流");
        String data = encrypt(json.toJSONString(), "12345678");
        System.out.println("明文是:" + json);
        System.out.println("加密后:" + data);
        System.out.println("解密后：" + decrypt(data, "12345678"));
    }
}
