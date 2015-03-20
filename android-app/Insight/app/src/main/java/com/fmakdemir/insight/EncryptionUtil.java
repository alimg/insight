package com.fmakdemir.insight;

import com.fmakdemir.insight.webservice.model.EventListResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

    private static final String MODE_DEVICE_AES = "device/aes";
    private static Cipher aesCipher;

    static {
        try {
            aesCipher = Cipher.getInstance("AES/CBC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static File decryptFile(EventListResponse.Event event, File file) throws IOException, InvalidKeyException {
        if (event.encryption == null || event.encryption.equals("")) {
            return file;
        }
        if (event.encryption.equals(MODE_DEVICE_AES)) {
            byte[] aesKey = getKey(event.deviceid);
            SecretKeySpec aeskeySpec = new SecretKeySpec(aesKey, "AES");
            aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

            File outfile = new File(file.getPath()+".out");
            CipherInputStream is = new CipherInputStream(new FileInputStream(file), aesCipher);
            FileOutputStream os = new FileOutputStream(outfile);

            int numBytes;
            byte[] buffer = new byte[1024];
            while((numBytes=is.read(buffer))!=-1) {
                os.write(buffer, 0, numBytes);
            }

            is.close();
            os.close();
            return outfile;
        }
        return file;
    }

    private static byte[] getKey(String deviceid) {
        return new byte[16];
    }
}
