package com.qdt.blockchain.support.utils;

import java.security.Key;
import java.util.Base64;

/**
 * Created on 2018/5/16.
 *
 * @author piaozaiguang
 * @since 1.0
 */
public class StringUtil {

    /**
     * Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"
     * @param difficulty
     * @return
     */
    public static String getDifficultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    public static String base64Encoding(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
