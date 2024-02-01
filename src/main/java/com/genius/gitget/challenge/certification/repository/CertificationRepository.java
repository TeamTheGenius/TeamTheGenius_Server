package com.genius.gitget.challenge.certification.repository;

import com.genius.gitget.challenge.certification.domain.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
}
