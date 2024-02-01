package com.genius.gitget.challenge.certification.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncryptUtil {
    private final AesBytesEncryptor encryptor;

    public String encryptPersonalToken(String personalToken) {
        byte[] encrypt = encryptor.encrypt(personalToken.getBytes(StandardCharsets.UTF_8));
        return byteArrayToString(encrypt);
    }

    public String decryptPersonalToken(String encryptString) {
        byte[] decryptBytes = stringToByteArray(encryptString);
        byte[] decrypt = encryptor.decrypt(decryptBytes);
        return new String(decrypt, StandardCharsets.UTF_8);
    }

    private String byteArrayToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes) {
            builder.append(aByte);
            builder.append(" ");
        }
        return builder.toString();
    }

    private byte[] stringToByteArray(String byteString) {
        String[] split = byteString.split("\\s");
        ByteBuffer buffer = ByteBuffer.allocate(split.length);
        for (String single : split) {
            buffer.put((byte) Integer.parseInt(single));
        }
        return buffer.array();
    }
}
