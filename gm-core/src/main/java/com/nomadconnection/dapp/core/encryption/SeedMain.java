package com.nomadconnection.dapp.core.encryption;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.Objects;

public class SeedMain {
    static String charset = StandardCharsets.UTF_8.toString();

    // 사용자가 지정하는 입력 키(16bytes), 암호화 대칭키
    public static byte[] pbUserKey = {(byte) 0x2c, (byte) 0x11, (byte) 0x19, (byte) 0x1d, (byte) 0x1f, (byte) 0x16, (byte) 0x12,
            (byte) 0x12, (byte) 0x11, (byte) 0x19, (byte) 0x1d, (byte) 0x1f, (byte) 0x10, (byte) 0x14, (byte) 0x1b,
            (byte) 0x16};

    public static byte[] pbUserKey2 = {(byte) 0x01, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x78, (byte) 0x89, (byte) 0xab,
            (byte) 0xcd, (byte) 0xef, (byte) 0xfe, (byte) 0xdc, (byte) 0xba, (byte) 0x98, (byte) 0x76, (byte) 0x54,
            (byte) 0x32, (byte) 0x10};

    // 사용자가 지정하는 초기화 벡터(16bytes), CBC 대칭키
    public static byte[] bszIV = {(byte) 0x27, (byte) 0x28, (byte) 0x27, (byte) 0x6d, (byte) 0x2d, (byte) 0xd5, (byte) 0x4e,
            (byte) 0x29, (byte) 0x2c, (byte) 0x56, (byte) 0xf4, (byte) 0x2a, (byte) 0x65, (byte) 0x2a, (byte) 0xae,
            (byte) 0x08};


    /**
     * SEED 128 Encryption
     *
     * @param str 암호화 할 문자열
     * @return 암화화된 문자열
     */
    public static String encryptEcb(String str) {
        try {
            return new String(encryptEcb(str, pbUserKey2), charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] encryptEcbGetBytes(String str) {
        return encryptEcb(str, pbUserKey2);
    }

    /**
     * SEED 128 Decryption
     *
     * @param bytes 복호화 할 바이트 배
     * @return 복호화 된 문자열열
     */
    public static String decryptEcb(byte[] bytes) {
        return decryptEcb(bytes, pbUserKey2);
    }

    public static byte[] encryptEcb(String str, byte[] paramPbUserKey) {
        byte[] enc = null;

        try {
            //암호화 함수 호출
            enc = KISA_SEED_ECB.SEED_ECB_Encrypt(paramPbUserKey, str.getBytes(charset), 0, str.getBytes(charset).length);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Encoder encoder = Base64.getEncoder();
        byte[] encArray = encoder.encode(enc);
        try {
            System.out.println(new String(encArray, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encArray;
    }

    public static String decryptEcb(byte[] str, byte[] paramPbUserKey) {

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
        System.out.println("decrypt Result = " + result);
        return result;
    }


    public static byte[] encryptCbcByDefaultDpUserKey(String str) {
        byte[] enc = null;

        try {
            //암호화 함수 호출
            enc = KISA_SEED_CBC.SEED_CBC_Encrypt(pbUserKey, bszIV, str.getBytes(charset), 0,
                    str.getBytes(charset).length);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Encoder encoder = Base64.getEncoder();
        byte[] encArray = encoder.encode(enc);
        try {
            System.out.println(new String(encArray, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encArray;
    }

    public static String decryptCbcByDefaultDpUserKey(byte[] str) {

        Decoder decoder = Base64.getDecoder();
        byte[] enc = decoder.decode(str);

        String result = "";
        byte[] dec;

        try {
            //복호화 함수 호출
            dec = KISA_SEED_CBC.SEED_CBC_Decrypt(pbUserKey, bszIV, enc, 0, enc.length);
            result = new String(dec, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.println("CBC decrypt Result = " + result);
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

    public static void main2(String[] args) {
        System.out.println("============ KEY =================");
        String keyString = "012345677889abcdeffedcba9876543210";
        byte[] bytes = hexToByteArray(keyString);
        String strHex = byteArrayToHex(bytes);

        System.out.println("keyString ==> " + keyString);
        System.out.println("hexToByteArray ==> " + Arrays.toString(bytes));
        System.out.println("byteArrayToHex ==> " + strHex);

        System.out.println("============ KEY 검증 =================");
        String strHex2 = byteArrayToHex(pbUserKey2);

        System.out.println("key(String) ==> " + strHex2);
        System.out.println(Arrays.toString(pbUserKey2));

        System.out.println("============ ECB 암복호화 =================");
//        byte[] encryptData = encryptEcb("1234", bytes);
        byte[] encryptData = encryptEcb("1234", pbUserKey2);
        decryptEcb(encryptData, pbUserKey2);

        System.out.println("\n============ CBC 암복호화 =================");
        byte[] encrypCbctData = encryptCbcByDefaultDpUserKey("1234");
        decryptCbcByDefaultDpUserKey(encrypCbctData);
    }

    public static void main3(String[] args) throws UnsupportedEncodingException {
        System.out.println("============ KEY =================");
        String text = "1234";
        byte[] encryptedBytes = encryptEcb("1234", pbUserKey2);
        String encryptedText = new String(encryptedBytes, charset);
        String decryptedText = decryptEcb(encryptedBytes);

        System.out.println("============ ECB 암복호화 =================");
        System.out.println("text = " + text);
        System.out.println("encryptedBytes = " + Arrays.toString(encryptedBytes));
        System.out.println("encryptedText = " + encryptedText);
        System.out.println("decryptedText = " + decryptedText);

//        byte[] encryptData = encryptEcb(text, pbUserKey);
//        decryptEcb(encryptData, pbUserKey);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println("============ KEY =================");
        String text = "01012345678";
        String encryptedText2 = encryptEcb(text);
        String decryptedText2 = decryptEcb(Objects.requireNonNull(encryptedText2).getBytes(charset));

        System.out.println("============ ECB 암복호화 =================");
        System.out.println("text = " + text);
        System.out.println("encryptedText2 = " + encryptedText2);
        System.out.println("decryptedText2 = " + decryptedText2);
    }

}