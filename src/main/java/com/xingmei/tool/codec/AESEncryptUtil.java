package com.xingmei.tool.codec;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * aes对称加密
 */
public class AESEncryptUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(AESEncryptUtil.class);

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
        byte[] bytes = doAES(Cipher.ENCRYPT_MODE, data.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
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
        byte[] bytes = doAES(Cipher.DECRYPT_MODE, src, key.getBytes(StandardCharsets.UTF_8));
        return new String(bytes, StandardCharsets.UTF_8);

    }

    private static byte[] doAES(int mode, byte[] data, byte[] key) {
        try {
            //1.构造密钥生成器，指定为AES算法,不区分大小写
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            //2.根据ecnodeRules规则初始化密钥生成器
            //生成一个128位的随机源,根据传入的字节数组
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key);
            kgen.init(128, secureRandom);
            //3.产生原始对称密钥
            SecretKey secretKey = kgen.generateKey();
            //4.获得原始对称密钥的字节数组
            byte[] enCodeFormat = secretKey.getEncoded();
            //5.根据字节数组生成AES密钥
            SecretKeySpec keySpec = new SecretKeySpec(enCodeFormat, "AES");
            //6.根据指定算法AES自成密码器
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            //7.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
            cipher.init(mode, keySpec);// 初始化
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("加解密失败，e: {}", e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        JSONObject json = new JSONObject();
        json.put("首联", "一叶扁舟伴水流");
        String data = encrypt(json.toJSONString(), "123456789");
        System.out.println("明文是:" + json);
        System.out.println("加密后:" + data);
        System.out.println("解密后：" + decrypt(data, "123456789"));
    }
}
