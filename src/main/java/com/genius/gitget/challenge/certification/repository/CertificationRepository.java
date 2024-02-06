package com.genius.gitget.challenge.certification.repository;

import com.genius.gitget.challenge.certification.domain.Certification;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    @Query("select c from Certification c where c.certificatedAt = :targetDate and c.participantInfo.id = :participantId")
    Optional<Certification> findCertificationByDate(@Param("targetDate") LocalDate targetDate,
                                                    @Param("participantId") Long participantId);
}
