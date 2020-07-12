package SecurityPack;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FileEncrypterDecrypter {

	private SecretKey secretKey;
	private Cipher cipher;
	
	public FileEncrypterDecrypter(SecretKey secretKey) throws NoSuchAlgorithmException, NoSuchPaddingException {
	    this.secretKey = secretKey;
	    this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	}
	
	public void encrypt(byte[] content, String fileName) throws InvalidKeyException, IOException {
	    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        FileOutputStream fs = new FileOutputStream(fileName);
        CipherOutputStream out = new CipherOutputStream(fs, cipher);
        out.write(content);
        out.flush();
        out.close();
	}
	
	public byte[] decrypt(String fileName) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException, InvalidAlgorithmParameterException {			
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			FileInputStream fis = new FileInputStream(fileName);
            CipherInputStream in = new CipherInputStream(fis, cipher);           
            byte[] b = new byte[1024];
            int numberOfBytedRead;
            while ((numberOfBytedRead = in.read(b)) >= 0) {
                baos.write(b, 0, numberOfBytedRead);
            }            
		} 
		catch (IOException exp){
			exp.printStackTrace();
		}
		return baos.toByteArray();
	}
}