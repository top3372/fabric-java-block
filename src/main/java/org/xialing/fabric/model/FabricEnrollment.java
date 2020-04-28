package org.xialing.fabric.model;


import org.xialing.fabric.utils.CryptoUtils;
import org.hyperledger.fabric.sdk.Enrollment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

public class FabricEnrollment implements Enrollment {

    private String keyFilePath;
    private String certFilePath;

    private PrivateKey privateKey;
    private String cert;

    public FabricEnrollment(String keyFilePath, String certFilePath) {
        this.keyFilePath = keyFilePath;
        this.certFilePath = certFilePath;
    }
    @Override
    public PrivateKey getKey() {
        try {
            if (privateKey == null) {
//                privateKey = CryptoUtils.getPrivateKeyFromBytes(FileUtils.getFileBytes(FileUtils.getResourceFilePath(keyFilePath)));
                privateKey = CryptoUtils.getPrivateKeyFromBytes(keyFilePath.getBytes(StandardCharsets.UTF_8));
            }
            return privateKey;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public String getCert() {
        if (cert == null) {
            cert = certFilePath;
        }
        return cert;
    }


}
