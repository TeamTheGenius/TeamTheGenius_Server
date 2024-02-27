package com.genius.gitget.challenge.participantinfo.repository;

import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.participantinfo.domain.Participant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    @Query("select p from Participant p where p.instance.id = :instanceId and p.user.id = :userId")
    Optional<Participant> findByJoinInfo(@Param("userId") Long userId,
                                         @Param("instanceId") Long instanceId);

    @Query("select p from Participant p where p.user.id = :userId and p.instance.progress = :progress and p.joinStatus = 'YES'")
    List<Participant> findAllJoinedByProgress(@Param("userId") Long userId,
                                              @Param("progress") Progress progress);
}
