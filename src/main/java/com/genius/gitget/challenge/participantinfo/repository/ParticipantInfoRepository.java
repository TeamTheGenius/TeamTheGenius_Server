package com.genius.gitget.challenge.participantinfo.repository;

import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipantInfoRepository extends JpaRepository<ParticipantInfo, Long> {

    @Query("select p from ParticipantInfo p where p.instance.id = :instanceId and p.user.id = :userId")
    Optional<ParticipantInfo> findBy(@Param("userId") Long userId,
                                     @Param("instanceId") Long instanceId);
}
