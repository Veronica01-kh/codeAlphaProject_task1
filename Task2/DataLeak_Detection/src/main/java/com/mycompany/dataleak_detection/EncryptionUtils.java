/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


/**
 *
 * @author TD
 */
package com.mycompany.dataleak_detection;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtils {
    // AES-256 requires a 32-byte key (32 characters here for simplicity)
    private static final String SECRET_KEY = "12345678901234567890123456789012"; 
    private static final String ALGORITHM = "AES";

    // Encrypts plain text into a secure string
    public static String encrypt(String data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Decrypts the secure string back to plain text
    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        return new String(cipher.doFinal(decodedBytes));
    }

    public static void main(String[] args) throws Exception {
        // Quick Test
        String original = "MyPassword123";
        String encrypted = encrypt(original);
        System.out.println("Original: " + original);
        System.out.println("Encrypted (AES-256): " + encrypted);
        System.out.println("Decrypted: " + decrypt(encrypted));
    }
}