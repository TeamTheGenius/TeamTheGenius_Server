package com.genius.gitget.participantinfo.repository;

import com.genius.gitget.participantinfo.domain.ParticipantInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantInfoRepository extends JpaRepository<ParticipantInfo, Long> {
}
