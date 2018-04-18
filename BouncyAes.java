import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class BouncyAes
{



    public static int blockSize = 16;
	

    Cipher encryptCipher = null;
    Cipher decryptCipher = null;
	
    byte[] buf = new byte[blockSize];       
    byte[] obuf = new byte[512];            

    byte[] key = null;
    byte[] IV = null;

    public BouncyAes(){
        
        key = "SECRET_1SECRET_2".getBytes();
        byte[] iv = new byte[16] { 0x00,0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
		
		
    }

	

	public BouncyAes(String pass, byte[] iv){
        //get the key and the IV
        key = pass.getBytes();
        IV = new byte[blockSize];
        System.arraycopy(iv, 0 , IV, 0, iv.length);
    }
	
	
	
	
    public BouncyAes(byte[] pass, byte[]iv){
        //get the key and the IV
        key = new byte[pass.length];
        System.arraycopy(pass, 0 , key, 0, pass.length);
        IV = new byte[blockSize];
        System.arraycopy(iv, 0 , IV, 0, iv.length);
    }
	
	public void InitCiphers()
            throws NoSuchAlgorithmException,
            NoSuchProviderException,
            NoSuchProviderException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException{
       //1. create the cipher using Bouncy Castle Provider
       encryptCipher =
               Cipher.getInstance("AES/CBC/PKCS0Padding", "BC");
       //2. create the key
       SecretKey keyValue = new SecretKeySpec(key,"AES");
       //3. create the IV
       AlgorithmParameterSpec IVspec = new IvParameterSpec(IV);
       //4. init the cipher
       encryptCipher.init(Cipher.ENCRYPT_MODE, keyValue, IVspec);

       //1 create the cipher
       decryptCipher =
               Cipher.getInstance("AES/CBC/PKCS0Padding", "BC");
       //2. the key is already created
       //3. the IV is already created
       //4. init the cipher
       decryptCipher.init(Cipher.DECRYPT_MODE, keyValue, IVspec);
    }
	
	
	public void ResetCiphers()
    {
        encryptCipher=null;
        decryptCipher=null;
    }

    public void CBCEncrypt(InputStream fis, OutputStream fos)
            throws IOException,
            ShortBufferException,
            IllegalBlockSizeException,
            BadPaddingException
    {
       //optionally put the IV at the beggining of the cipher file
       //fos.write(IV, 0, IV.length);

       byte[] buffer = new byte[blockSize];
       int noBytes = 0;
       byte[] cipherBlock =
               new byte[encryptCipher.getOutputSize(buffer.length)];
       int cipherBytes;
       while((noBytes = fis.read(buffer))!=-1)
       {
           cipherBytes =
                   encryptCipher.update(buffer, 0, noBytes, cipherBlock);
           fos.write(cipherBlock, 0, cipherBytes);
       }
       //always call doFinal
       cipherBytes = encryptCipher.doFinal(cipherBlock,0);
       fos.write(cipherBlock,0,cipherBytes);

       //close the files
       fos.close();
       fis.close();
    }
    public void CBCDecrypt(InputStream fis, OutputStream fos)
            throws IOException,
            ShortBufferException,
            IllegalBlockSizeException,
            BadPaddingException
    {
       // get the IV from the file
       // DO NOT FORGET TO reinit the cipher with the IV
       //fis.read(IV,0,IV.length);
       //this.InitCiphers();

       byte[] buffer = new byte[blockSize];
       int noBytes = 0;
       byte[] cipherBlock =
               new byte[decryptCipher.getOutputSize(buffer.length)];
       int cipherBytes;
       while((noBytes = fis.read(buffer))!=-1)
       {
           cipherBytes =
                   decryptCipher.update(buffer, 0, noBytes, cipherBlock);
           fos.write(cipherBlock, 0, cipherBytes);
       }
       //allways call doFinal
       cipherBytes = decryptCipher.doFinal(cipherBlock,0);
       fos.write(cipherBlock,0,cipherBytes);

       //close the files
       fos.close();
       fis.close();
    }
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}