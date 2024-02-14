package com.genius.gitget.challenge.certification.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.CertificationStatus;
import com.genius.gitget.challenge.certification.service.CertificationService;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.challenge.participantinfo.domain.ParticipantInfo;
import com.genius.gitget.challenge.participantinfo.service.ParticipantInfoService;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.response.dto.ListResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certification")
public class CertificationController {
    private final CertificationService certificationService;
    private final InstanceService instanceService;
    private final ParticipantInfoService participantInfoService;

    @PostMapping("/today")
    public ResponseEntity<SingleResponse<CertificationResponse>> certificateByGithub(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody CertificationRequest certificationRequest
    ) {

        CertificationResponse certificationResponse = certificationService.updateCertification(
                userPrincipal.getUser(),
                certificationRequest);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), certificationResponse)
        );
    }

    @GetMapping("/week/{instanceId}")
    public ResponseEntity<ListResponse<CertificationResponse>> getCertification(
            @PathVariable Long instanceId,
            @RequestParam Long userId
    ) {
        ParticipantInfo participantInfo = participantInfoService.getParticipantInfoByJoinInfo(userId, instanceId);
        List<CertificationResponse> weekCertification = certificationService.getWeekCertification(
                participantInfo.getId(), LocalDate.now());

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), weekCertification)
        );
    }

    @GetMapping("/total/{instanceId}")
    public ResponseEntity<ListResponse<CertificationResponse>> getTotalCertifications(
            @PathVariable Long instanceId,
            @RequestParam Long userId
    ) {
        ParticipantInfo participantInfo = participantInfoService.getParticipantInfoByJoinInfo(userId, instanceId);
        List<CertificationResponse> totalCertification = certificationService.getTotalCertification(
                participantInfo.getId(), LocalDate.now());

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), totalCertification)
        );
    }

    @GetMapping("/status/{instanceId}")
    public ResponseEntity<SingleResponse<CertificationStatus>> getCertificationStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long instanceId
    ) {

        Instance instance = instanceService.findInstanceById(instanceId);
        ParticipantInfo participantInfo = participantInfoService.getParticipantInfoByJoinInfo(
                userPrincipal.getUser().getId(),
                instanceId);

        CertificationStatus certificationStatus = certificationService.getCertificationStatus(
                instance, participantInfo, LocalDate.now());

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), certificationStatus)
        );
    }
}
