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
            res = res.substring(len - 11, len - 1);
        } else {
            res = res.substring(idx, 10 + idx);
        }
        log.info("res = " + res);
        return res.toUpperCase();
    }
}
