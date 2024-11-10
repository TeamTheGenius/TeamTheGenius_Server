package com.genius.gitget.challenge.certification.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class EncryptUtil {
    private final AesBytesEncryptor encryptor;

    public String encrypt(String target) {
        byte[] encrypt = encryptor.encrypt(target.getBytes(StandardCharsets.UTF_8));
        return byteArrayToString(encrypt);
    }

    public String decrypt(String encrypted) {
        byte[] decryptBytes = stringToByteArray(encrypted);
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
