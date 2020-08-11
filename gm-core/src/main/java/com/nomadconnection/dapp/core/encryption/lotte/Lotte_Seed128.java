package com.nomadconnection.dapp.core.encryption.lotte;

import com.nomadconnection.dapp.core.encryption.lotte.core.SEED_KISA;
import com.nomadconnection.dapp.core.utils.EnvUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class Lotte_Seed128 {

	private static byte[] pbUserKey;
	private final EnvUtil envUtil;

	private static int SeedBlockSize = 16;    // block length in bytes
	private static int RoundKey[] = new int[32];
	private static final byte PADDING_VALUE_00 = 0x00; // Null

	@PostConstruct
	private void initKey() {
		pbUserKey = new byte[]{(byte) 0x43, (byte) 0x50, (byte) 0x47, (byte) 0x57, (byte) 0x30, (byte) 0x37, (byte) 0x30, (byte) 0x32,
				(byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x00};   // stgKey
		if (envUtil.isProd()) {
			pbUserKey = new byte[]{(byte) 0x43, (byte) 0x50, (byte) 0x47, (byte) 0x57, (byte) 0x30, (byte) 0x37, (byte) 0x30, (byte) 0x32,
					(byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x00};
		}
		SEED_KISA.SeedRoundKey(RoundKey, pbUserKey);
	}

	/**
	 * SEED-128 Encryption
	 *
	 * @param str 암호화 할 문자열
	 * @return 암화화 된 문자열
	 */
	public static String encryptEcb(String str) {
		if (StringUtils.isEmpty(str)) {
			log.warn("## input string is empty! skip encryption!");
			return str;
		}
		return byte2hex(encrypt(str.getBytes()));
	}

	/**
	 * SEED-128 Decryption
	 *
	 * @param encryptedText 암호화 된 문자열
	 * @return 복호화 된 문자열
	 */
	public static String decryptEcb(String encryptedText) {
		if (StringUtils.isEmpty(encryptedText)) {
			log.warn("## encryptedText is empty! skip decryption!");
			return encryptedText;
		}
		return decryptAsString(hex2byte(encryptedText));
	}

	private static byte[] encrypt(byte[] sbuffer) {

		byte[] inDataBuffer = addPadding(sbuffer, SeedBlockSize, "NoPadding");
		byte[] encryptBytes = new byte[inDataBuffer.length];

		int rt = inDataBuffer.length / SeedBlockSize;
		for (int j = 0; j < rt; j++) {
			byte sSource[] = new byte[SeedBlockSize];
			byte sTarget[] = new byte[SeedBlockSize];

			System.arraycopy(inDataBuffer, (j * SeedBlockSize), sSource, 0, SeedBlockSize);
			SEED_KISA.SeedEncrypt(sSource, RoundKey, sTarget);

			System.arraycopy(sTarget, 0, encryptBytes, (j * SeedBlockSize), sTarget.length);
		}

		return encryptBytes;
	}

	private static String decryptAsString(byte[] encryptBytes) {
		return new String(decrypt(encryptBytes));
	}

	private static byte[] decrypt(byte[] encryptBytes) {

		byte[] decryptBytes = new byte[encryptBytes.length];
		int rt = encryptBytes.length / SeedBlockSize;

		byte sSource[] = new byte[SeedBlockSize];
		byte sTarget[] = new byte[SeedBlockSize];
		for (int j = 0; j < rt; j++) {
			System.arraycopy(encryptBytes, (j * SeedBlockSize), sSource, 0,
					SeedBlockSize);

			SEED_KISA.SeedDecrypt(sSource, RoundKey, sTarget);
			System.arraycopy(sTarget, 0, decryptBytes, (j * SeedBlockSize), SeedBlockSize);
		}

		return removePadding(decryptBytes, SeedBlockSize, "NoPadding");
	}

	private static byte[] removePadding(byte[] sourceBytes, int nBlockSize,
										String padMod) {
		byte paddingResult[] = null;
		if (sourceBytes.length == 0 || nBlockSize < 1
				|| sourceBytes.length < nBlockSize
				|| sourceBytes.length % nBlockSize != 0) {
			return null;
		}

		int lastindex = sourceBytes.length;
		// byte lastByte = sourceBytes[lastindex-1];
		byte lastByte = (("PKCS5".equals(padMod) ? sourceBytes[lastindex - 1] : (byte) PADDING_VALUE_00));

		while (lastindex > 0) {
			if (sourceBytes[lastindex - 1] != lastByte) {
				break;
			}
			lastindex--;
		}

		if ("PKCS5".equals(padMod)) {
			if ((sourceBytes.length - lastindex) == lastByte) {
				paddingResult = new byte[lastindex];
			} else {
				paddingResult = new byte[sourceBytes.length];
			}
		} else {
			paddingResult = new byte[lastindex];
		}

		System.arraycopy(sourceBytes, 0, paddingResult, 0, paddingResult.length);
		return paddingResult;
	}

	private static byte[] addPadding(byte[] sourceBytes, int nBlockSize,
									 String padMod) {
		byte paddingResult[] = null;
		if (sourceBytes.length == 0 || nBlockSize < 1) {
			return null;
		}

		int needBlankLength = nBlockSize - (sourceBytes.length % nBlockSize);

		if (needBlankLength == nBlockSize) {
			needBlankLength = 0;
		}

		if (needBlankLength > 0) {
			paddingResult = new byte[sourceBytes.length + needBlankLength];
			System.arraycopy(sourceBytes, 0, paddingResult, 0, sourceBytes.length);
			byte padByte = (("PKCS5".equals(padMod) ? (byte) needBlankLength : (byte) PADDING_VALUE_00));
			for (int i = 0; i < needBlankLength; i++) {
				paddingResult[paddingResult.length - 1 - i] = padByte;
			}
		} else {
			paddingResult = sourceBytes;
		}
		return paddingResult;
	}

	public static String byte2hex(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null; //throw new IllegalArgumentException("Input byte Empty");
		}

		StringBuffer sb = new StringBuffer(bytes.length * 2);
		String hexNumber;
		for (int x = 0; x < bytes.length; x++) {
			hexNumber = Integer.toHexString(bytes[x] & 0xff).toUpperCase();
			if (hexNumber.length() == 1) {
				sb.append("0" + hexNumber);
			} else {
				sb.append(hexNumber);
			}
		}
		return sb.toString();
	}

	public static byte[] hex2byte(String hex) throws IllegalArgumentException {
		if (hex == null || hex.length() == 0) {
			return null; //throw new IllegalArgumentException("Hexa String Empty");
		}

		if (hex.length() % 2 != 0) {
			throw new IllegalArgumentException("Hexa String Bad padding");
		}

		byte[] ba = new byte[hex.length() / 2];
		for (int i = 0; i < ba.length; i++) {
			ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, (2 * i) + 2), 16);
		}
		return ba;
	}

	public static void main(String[] args) {

		// Round keys for encryption or decryption
		// User secret key
		byte pbUserKey[] = new byte[]{(byte) 0x43, (byte) 0x50, (byte) 0x47, (byte) 0x57, (byte) 0x30, (byte) 0x37, (byte) 0x30, (byte) 0x32,
				(byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x00};

		// Derive roundkeys from user secret key
		SEED_KISA.SeedRoundKey(RoundKey, pbUserKey);

		String str = "930412";
		System.out.println("raw :" + str);

		String encDataStr = encryptEcb(str);
		System.out.println("encDataStr : " + encDataStr);

		String seedDecryptRs = decryptEcb(encDataStr);
		System.out.println("decData :" + seedDecryptRs);
	}
}
