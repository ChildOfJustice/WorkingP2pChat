package com.example.chattest.cryptography;

public interface CipherModule {

    byte[] encrypt(byte[] data);

    byte[] decrypt(byte[] data);

    void importKeyFromString(String fileText, boolean importBothKeys);

}
