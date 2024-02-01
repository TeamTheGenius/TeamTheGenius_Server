package com.genius.gitget.challenge.certification.repository;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;
import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.certification.domain.Certification;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class CertificationRepositoryTest {
    @Autowired
    CertificationRepository certificationRepository;

    @Test
    @DisplayName("Certification 객체를 만들어서 저장할 수 있다.")
    public void should() {
        //given
        LocalDate localDate = LocalDate.of(2024, 2, 1);
        String certificationLinks = "https://test.com";
        Certification certification = Certification.builder()
                .certificationStatus(NOT_YET)
                .certificatedAt(localDate)
                .certificationLinks(certificationLinks)
                .build();

        //when
        Certification savedCertification = certificationRepository.save(certification);

        //then
        assertThat(savedCertification.getCertificationStatus()).isEqualTo(NOT_YET);
        assertThat(savedCertification.getCertificatedAt()).isEqualTo(localDate);
        assertThat(savedCertification.getCertificationLinks()).isEqualTo(certificationLinks);
    }
}