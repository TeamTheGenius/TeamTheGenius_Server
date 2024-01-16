package com.genius.todoffin.participantinfo.repository;

import com.genius.todoffin.participantinfo.domain.ParticipantInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantInfoRepository extends JpaRepository<ParticipantInfo, Long> {
}
