/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keycloak.storage.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.jboss.logging.Logger;


/**
 * Класс для работы с алгоритмами SHA-1, MD5 и шифроания
 *
 * @author vasil
 */
public class hashUtil {

    private static final Logger log = Logger.getLogger("hashUtil");
    //private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    /**
     * Получает hash по алгоритму SHA-1
     *
     * @param plain
     * @return
     */
    public static byte[] sha1(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("sha");
            md.update(plain.getBytes());
            byte[] digest = md.digest();

            return (digest);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Получает hash по алгоритму MD5
     *
     * @param raw - строка для которой должен быть получен hash
     * @return - hash MD5 для строки raw
     */
    public static byte[] md5(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(raw.getBytes(), 0, raw.length());
            //return new BigInteger(1, md.digest()).toString(16);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param bytes
     * @return
     */
    public static String encodeToHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    /**
     * Функция генерирует "соль" для получения hash-а пароля
     *
     * @return
     */
    public static String genSalt() {
        int idx = (int) (Math.random() * 10);
        String res = encodeToHex(UUID.randomUUID().toString().getBytes());
        log.info("res = " + res + " idx = " + idx);
        int len = res.length();
        if (idx > 5) {
            res = res.substring(len-11, len-1);
        } else {
            res = res.substring(idx, 10+idx);
        }
        log.info("res = " + res);
        return res.toUpperCase();
    }

    /**
     *
     * @param buf
     * @return
     */
//    public static String encodeToHex(byte[] buf) {
//        String stringRes = null;
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < buf.length; i++) {
//
//            String temp = String.format("%x", buf[i]);
//            if (temp.length() < 2) {
//                temp = "0" + temp;
//            }
//            sb.append(temp);
//        }
//        stringRes = sb.toString().toUpperCase();
//        return stringRes;
//    }
//    public static byte[] sha1ToByte(String plain) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("sha");
//            md.update(plain.getBytes());
//            byte[] digest = md.digest();
//
//            return digest;
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     *
//     * @param plain
//     * @return
//     */
//    public static String sha1ToString(String plain) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("sha");
//            md.update(plain.getBytes());
//            byte[] digest = md.digest();
//
//            return (new BigInteger(1, md.digest()).toString(16)).toUpperCase();
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    /**
     *
     * @param raw
     * @return
     */
//    public static String md5ToString(String raw) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            md.update(raw.getBytes(), 0, raw.length());
//            return (new BigInteger(1, md.digest()).toString(16)).toUpperCase();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     *
//     * @param raw
//     * @return
//     */
//    public static byte[] md5ToByte(String raw) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            md.update(raw.getBytes(), 0, raw.length());
//            return md.digest();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    /**
     *
     * @param buf
     * @return
     */
    /*private static String encode(byte[] buf) {
        int size = buf.length;
        char[] ar = new char[((size + 2) / 3) * 4];
        int a = 0;
        int i = 0;
        while (i < size) {
            byte b0 = buf[i++];
            byte b1 = (i < size) ? buf[i++] : 0;
            byte b2 = (i < size) ? buf[i++] : 0;

            int mask = 0x3F;
            ar[a++] = ALPHABET[(b0 >> 2) & mask];
            ar[a++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
            ar[a++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
            ar[a++] = ALPHABET[b2 & mask];
        }
        switch (size % 3) {
            case 1:
                ar[--a] = '=';
            case 2:
                ar[--a] = '=';
        }
        return new String(ar);
    }*/
}
