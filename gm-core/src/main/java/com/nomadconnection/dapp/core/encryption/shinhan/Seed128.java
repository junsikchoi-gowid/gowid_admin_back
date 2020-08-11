package com.nomadconnection.dapp.core.encryption.shinhan;

import com.nomadconnection.dapp.core.encryption.shinhan.core.KISA_SEED_ECB;
import com.nomadconnection.dapp.core.utils.EnvUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

@Slf4j
@Component
@RequiredArgsConstructor
public class Seed128 {

    private static byte[] key;
    private final EnvUtil envUtil;

    static String charset = "EUC-KR";

    @PostConstruct
    private void initKey() {
        key = new byte[]{(byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF,
                              (byte) 0xFE, (byte) 0xDC, (byte) 0xBA, (byte) 0x98, (byte) 0x76, (byte) 0x54, (byte) 0x32, (byte) 0x10};   // stgKey
       if(envUtil.isProd()) {
           key = new byte[]{(byte) 0xA4, (byte) 0xE8, (byte) 0x34, (byte) 0xCA, (byte) 0x87, (byte) 0xF5, (byte) 0x44, (byte) 0x33,
                                 (byte) 0xAA, (byte) 0x86, (byte) 0xA5, (byte) 0x1C, (byte) 0xB7, (byte) 0x24, (byte) 0x88, (byte) 0xA8};
       }
    }

    /**
     * SEED-128 Encryption
     *
     * @param str 암호화 할 문자열
     * @return 암화화 된 문자열
     */
    public static String encryptEcb(String str) {
        try {
            if (StringUtils.isEmpty(str)) {
                log.warn("## input string is empty! skip encryption!");
                return str;
            }
            return new String(encryptEcb(str, key), charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * SEED-128 Decryption
     *
     * @param encryptedText 암호화 된 문자열
     * @return 복호화 된 문자열
     */
    public static String decryptEcb(String encryptedText) {
        try {
            if (StringUtils.isEmpty(encryptedText)) {
                log.warn("## encryptedText is empty! skip decryption!");
                return encryptedText;
            }
            return decryptEcb(encryptedText.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * SEED-128 Decryption
     *
     * @param bytes 암호화 된 바이트 배열
     * @return 복호화 된 문자열
     */
    public static String decryptEcb(byte[] bytes) {
        return decryptEcb(bytes, key);
    }


    private static byte[] encryptEcb(String str, byte[] paramPbUserKey) {
        byte[] enc = null;

        try {
            //암호화 함수 호출
            enc = KISA_SEED_ECB.SEED_ECB_Encrypt(paramPbUserKey, str.getBytes(charset), 0, str.getBytes(charset).length);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.debug("hex : [{}]", byteArrayToHex(enc));

        Encoder encoder = Base64.getEncoder();
        byte[] encArray = encoder.encode(enc);
        return encArray;
    }

    private static String decryptEcb(byte[] str, byte[] paramPbUserKey) {

        Decoder decoder = Base64.getDecoder();
        byte[] enc = decoder.decode(str);

        String result = "";
        byte[] dec;

        try {
            //복호화 함수 호출
            dec = KISA_SEED_ECB.SEED_ECB_Decrypt(paramPbUserKey, enc, 0, enc.length);
            result = new String(dec, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * hex string to byte[]
     *
     * @param hex HEX String
     * @return converted byte array from hex string
     */
    private static byte[] hexToByteArray(String hex) {
        hex = hex.replaceAll("\"", "\\\""); /*" */
        if (hex.length() == 0) {
            return null;
        }
        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return ba;
    }

    /**
     * byte[] to hex sting
     *
     * @param byteArray byte array
     * @return converted hex string from byte array
     */
    public static String byteArrayToHex(byte[] byteArray) {
        if (byteArray == null || byteArray.length == 0) {
            return null;
        }
        StringBuilder stringBuffer = new StringBuilder(byteArray.length * 2);
        String hexNumber;
        for (byte aBa : byteArray) {
            hexNumber = "0" + Integer.toHexString(0xff & aBa);

            stringBuffer.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
        System.out.println("============ ECB 암복호화 =================");
        String text = "신한카드";
        String encryptedText = encryptEcb(text);
        String decryptedText = decryptEcb(encryptedText);
        System.out.println("plain text = [" + text + "]");
        System.out.println("encryptedText = [" + encryptedText + "]");
        System.out.println("decryptedText = [" + decryptedText + "]");

        System.out.println("============ ECB 암복호화2 =================");

        String text2 = "GOWID 1234 신한카드";
        String encryptedText2 = encryptEcb(text2);
        String decryptedText2 = decryptEcb(encryptedText2);
        System.out.println("plain text = [" + text2 + "]");
        System.out.println("encryptedText = [" + encryptedText2 + "]");
        System.out.println("decryptedText = [" + decryptedText2 + "]");
    }

}