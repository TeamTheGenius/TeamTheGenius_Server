package com.genius.gitget.challenge.certification.repository;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    @Query("select c from Certification c where c.certificatedAt = :targetDate and c.participantInfo.id = :participantId")
    Optional<Certification> findByDate(@Param("targetDate") LocalDate targetDate,
                                       @Param("participantId") Long participantId);

    @Query("select c from Certification c where c.participantInfo.id = :participantId and c.certificatedAt >= :startDate and c.certificatedAt <= :endDate order by c.currentAttempt desc")
    List<Certification> findByDuration(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       @Param("participantId") Long participantId);

    @Query("select c from Certification c where c.participantInfo.id = :participantId and c.certificationStatus = :status and c.certificatedAt <= :currentDate")
    List<Certification> findByStatus(@Param("participantId") Long participantId,
                                     @Param("status") CertificateStatus status,
                                     @Param("currentDate") LocalDate currentDate);
}
