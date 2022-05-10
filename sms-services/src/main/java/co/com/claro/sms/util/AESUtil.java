package co.com.claro.sms.util;

import java.security.Security;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
public class AESUtil {
        
    public final static String TYPE = "AES";
    public final static String INSTANCE = "AES/ECB/PKCS7Padding";
    public final static String PROVIDER = "BC";

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

            ctLength += cipher.doFinal(cipherText, ctLength);
            String encriptado = Base64.getEncoder().encodeToString(cipherText);

            return encriptado;
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
            String datos = new String(datosDesencriptados);

            return datos;
        } catch (Exception ex) {
            return null;
        }
        
    }

}

