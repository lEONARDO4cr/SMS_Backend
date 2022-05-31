package co.com.claro.sms.util;

import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

	private AESUtil() {
		throw new RuntimeException("Utility class");
	}

	public static final String TYPE = "AES";
	public static final String INSTANCE = "AES/ECB/PKCS7Padding";
	public static final String PROVIDER = "BC";

	public static String encrypt(String datos, String claveSecreta) throws Exception {

		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			byte[] input = datos.getBytes();
			byte[] keyBytes = claveSecreta.getBytes();
			SecretKeySpec key = new SecretKeySpec(keyBytes, TYPE);
			Cipher cipher = Cipher.getInstance(INSTANCE, PROVIDER);

			cipher.init(Cipher.ENCRYPT_MODE, key);

			byte[] cipherText = new byte[cipher.getOutputSize(input.length)];

			int ctLength = cipher.update(input, 0, input.length, cipherText, 0);

			cipher.doFinal(cipherText, ctLength);
			return Base64.getEncoder().encodeToString(cipherText);

		} catch (Exception ex) {
			return null;
		}
	}

	public static String decrypt(String datosEncriptados, String claveSecreta) throws Exception {

		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			byte[] keyBytes = claveSecreta.getBytes();
			SecretKeySpec key = new SecretKeySpec(keyBytes, TYPE);
			Cipher cipher = Cipher.getInstance(INSTANCE, PROVIDER);
			cipher.init(Cipher.DECRYPT_MODE, key);

			byte[] bytesEncriptados = Base64.getDecoder().decode(datosEncriptados);
			byte[] datosDesencriptados = cipher.doFinal(bytesEncriptados);
			return new String(datosDesencriptados);
		} catch (Exception ex) {
			return null;
		}

	}

}
