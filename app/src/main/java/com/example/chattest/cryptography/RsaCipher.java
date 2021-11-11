package com.example.chattest.cryptography;

import android.os.Build;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import android.util.Base64;
import android.util.Log;

import com.example.chattest.utils.Constants;

public class RsaCipher implements CipherModule {
    PrivateKey privateKey;
    PublicKey publicKey;

    public RsaCipher()  {

    }

    public void generateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
    }



    public void importPublicKey(String fileName) {
        try {
//            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            byte[] publicKeyBytes = new byte[(int) file.length()];
            fis.read(publicKeyBytes);
            fis.close();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

    }

    public void importPrivateKey(String fileName) {
        try {
//            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            byte[] privateKeyBytes = new byte[(int) file.length()];
            fis.read(privateKeyBytes);
            fis.close();
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

    }

    public void exportPublicKey(String fileName){
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(publicKey.getEncoded());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] getPubKeyBytes(){
        return publicKey.getEncoded();
    }
    public void exportPrivateKey(String fileName){
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(privateKey.getEncoded());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] getPrivateKeyBytes(){
        return privateKey.getEncoded();
    }


    @Override
    public byte[] encrypt(byte[] data) {
        Cipher encryptCipher = null;
        try {
            encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return encryptCipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        //byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8);)
        //String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
        return new byte[0];
    }

    @Override
    public byte[] decrypt(byte[] data) {
        Cipher decryptCipher = null;
        try {
            decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            return decryptCipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    public void importKeyFromString(String fileText, boolean importBothKeys) {
        KeyFactory keyFactory = null;
        if(importBothKeys) {
            try {
                Log.e(Constants.TAG, "ENC FILE 2: " + fileText);
                String[] expected2 = fileText.split("\n", 2);

                keyFactory = KeyFactory.getInstance("RSA");
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(expected2[0].getBytes());
                publicKey = keyFactory.generatePublic(publicKeySpec);

                EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(expected2[1].getBytes());
                privateKey = keyFactory.generatePrivate(privateKeySpec);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        } else {
            //only pub key
            try {
                keyFactory = KeyFactory.getInstance("RSA");
                EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(fileText.getBytes());
                publicKey = keyFactory.generatePublic(publicKeySpec);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
    }


    public byte[] getPubKey() {
        return getPubKeyBytes();
    }










    public void importPublicKey(byte[] data) {
        try {
//            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(data);
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

    }

    public void importPrivateKey(byte[] data) {
        try {
//            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(data);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

    }
}
