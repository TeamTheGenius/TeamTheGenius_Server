package com.genius.gitget.challenge.certification.util;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class EncryptUtilTest {
    @Autowired
    EncryptUtil encryptUtil;

    @Test
    @DisplayName("특정 문자열에 대해서 암호화하고 복호화했을 때, 원래의 값과 일치해야 한다.")
    public void should_returnOrigin_when_decrypt() {
        //given
        String target = "target token";

        //when
        String encrypted = encryptUtil.encrypt(target);
        String decrypted = encryptUtil.decrypt(encrypted);

        //then
        Assertions.assertThat(decrypted).isEqualTo(target);
    }
}