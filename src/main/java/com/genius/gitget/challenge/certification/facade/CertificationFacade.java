package com.genius.gitget.challenge.certification.facade;

import com.genius.gitget.challenge.certification.dto.CertificationInformation;
import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.InstancePreviewResponse;
import com.genius.gitget.challenge.certification.dto.TotalResponse;
import com.genius.gitget.challenge.certification.dto.WeekResponse;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.user.domain.User;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CertificationFacade {
    WeekResponse getMyWeekCertifications(Long participantId, LocalDate currentDate);

    Slice<WeekResponse> getOthersWeekCertifications(Long userId, Long instanceId,
                                                    LocalDate currentDate, Pageable pageable);

    TotalResponse getTotalCertification(Long participantId, LocalDate currentDate);

    ActivatedResponse passCertification(Long userId, CertificationRequest certificationRequest);

    CertificationResponse updateCertification(User user, CertificationRequest certificationRequest);

    CertificationInformation getCertificationInformation(Instance instance, Participant participant,
                                                         LocalDate currentDate);

    InstancePreviewResponse getInstancePreview(Long instanceId);
}
