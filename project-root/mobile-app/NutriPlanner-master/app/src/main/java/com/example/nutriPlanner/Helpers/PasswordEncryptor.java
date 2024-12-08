package com.example.nutriPlanner.Helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptor {

    public String encryptPassword(String password) {
        try {
            // экземпляр MessageDigest с использованием SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // хеширование пароля
            byte[] hashedBytes = digest.digest(password.getBytes());
            // преоброзвание хэша в строку в шестнадцатеричном формате
            StringBuilder stringBuilder = new StringBuilder();
            for (byte b : hashedBytes) {
                stringBuilder.append(String.format("%02x", b));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        // шифрование введенного пароля и проверка на соответствие хэшу
        String encryptedPassword = encryptPassword(rawPassword);
        return encryptedPassword != null && encryptedPassword.equals(hashedPassword);
    }
}
