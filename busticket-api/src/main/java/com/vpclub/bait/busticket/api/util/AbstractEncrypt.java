package com.vpclub.bait.busticket.api.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 从不伟大，亦不渺小
 * <p/>
 * Created by Byrdkm17@gmail.com on 2014-07-31
 */
public abstract class AbstractEncrypt {

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;

    protected void loadPEMKey(InputStream public_data, InputStream private_data) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        if (public_data != null) {
            try {
                LineIterator public_li = IOUtils.lineIterator(public_data, "utf-8");

                StringBuilder public_sb = new StringBuilder();
                while (public_li.hasNext()) {
                    String line = public_li.next();
                    if (!line.startsWith("-")) {
                        public_sb.append(line).append("\n");
                    }
                }
                X509EncodedKeySpec public_keySpec = new X509EncodedKeySpec(Base64.decodeBase64(public_sb.toString()));
                this.publicKey = (RSAPublicKey) keyFactory.generatePublic(public_keySpec);
                System.out.println("加载公钥成功");
            } catch (InvalidKeySpecException e) {
                throw new Exception("公钥非法");
            } catch (IOException e) {
                throw new Exception("公钥数据内容读取错误");
            } catch (NullPointerException e) {
                throw new Exception("公钥数据为空");
            }
        }

        if (private_data != null) {
            try {
                LineIterator private_li = IOUtils.lineIterator(private_data, "utf-8");

                StringBuilder private_sb = new StringBuilder();
                while (private_li.hasNext()) {
                    String line = private_li.next();
                    if (!line.startsWith("-")) {
                        private_sb.append(line).append("\n");
                    }
                }

                PKCS8EncodedKeySpec private_keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(private_sb.toString()));
                this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(private_keySpec);
                System.out.println("加载私钥成功");
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                throw new Exception("私钥非法");
            } catch (IOException e) {
                throw new Exception("私钥数据内容读取错误");
            } catch (NullPointerException e) {
                throw new Exception("私钥数据为空");
            }
        }
    }

    /**
     * 加载公钥和私钥
     *
     * @throws Exception
     */
    protected void loadKey(InputStream public_data, InputStream private_data) throws Exception {
        if (public_data != null) {
            ObjectInputStream public_input = new ObjectInputStream(public_data);
            this.publicKey = (RSAPublicKey) public_input.readObject();
        }

        if (private_data != null) {
            ObjectInputStream private_input = new ObjectInputStream(private_data);
            this.privateKey = (RSAPrivateKey) private_input.readObject();
        }
    }

    /**
     * 数据加密
     *
     * @param data 明文数据
     * @return 密文
     * @throws Exception
     */
//    public String encrypt(String data) throws Exception {
//        Cipher cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
//        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
//        return Base64.encodeBase64String(cipher.doFinal(data.getBytes("utf-8")));
//    }

    /**
     * 数据解密
     *
     * @param data 密文
     * @return 明文
     * @throws Exception
     */
//    public String decrypt(String data) throws Exception {
//        Cipher cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
//        cipher.init(Cipher.DECRYPT_MODE, publicKey);
//        return new String(cipher.doFinal(Base64.decodeBase64(data)));
//    }

    /**
     * 数据签名
     *
     * @param data 明文数据
     * @return 签名
     * @throws Exception
     */
    public String sign(String data) throws Exception {
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes("utf-8"));
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * 验证签名
     *
     * @param data 原始数据
     * @param sign 签名
     * @return 验证结果
     * @throws Exception
     */
    public boolean verify(String data, String sign) throws Exception {
        Signature signature = Signature.getInstance("MD5withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes("utf-8"));

        // 验证签名是否正常
        return signature.verify(Base64.decodeBase64(sign));

    }
}
