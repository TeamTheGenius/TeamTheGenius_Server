package com.genius.gitget.challenge.participantinfo.repository;

import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantInfoRepository extends JpaRepository<ParticipantInfo, Long> {
}
