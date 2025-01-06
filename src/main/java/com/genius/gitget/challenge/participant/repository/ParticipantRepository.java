package com.genius.gitget.challenge.participant.repository;

import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    @Query("select p from Participant p where p.instance.id = :instanceId and p.user.id = :userId")
    Optional<Participant> findByJoinInfo(@Param("userId") Long userId,
                                         @Param("instanceId") Long instanceId);

    @Query("select p from Participant p where p.user.id = :userId and p.instance.progress = :progress and p.joinStatus = :joinStatus")
    List<Participant> findAllByStatus(@Param("userId") Long userId,
                                      @Param("progress") Progress progress,
                                      @Param("joinStatus") JoinStatus joinStatus);

    @Query("select p from Participant p where p.instance.id = :instanceId and p.joinStatus = :joinStatus")
    Slice<Participant> findAllByInstanceId(@Param("instanceId") Long instanceId,
                                           @Param("joinStatus") JoinStatus joinStatus,
                                           Pageable pageable);
}
