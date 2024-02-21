package com.genius.gitget.challenge.certification.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.certification.dto.CertificationInformation;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.RenewRequest;
import com.genius.gitget.challenge.certification.dto.RenewResponse;
import com.genius.gitget.challenge.certification.service.CertificationService;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.challenge.participantinfo.domain.Participant;
import com.genius.gitget.challenge.participantinfo.service.ParticipantProvider;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
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
    private final UserService userService;
    private final CertificationService certificationService;
    private final InstanceService instanceService;
    private final ParticipantProvider participantProvider;


    @GetMapping("/{instanceId}")
    public ResponseEntity<SingleResponse<CertificationResponse>> getInstanceInformation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long instanceId
    ) {
        CertificationResponse certificationResponse = certificationService.getCertificationInformation(
                userPrincipal.getUser(), instanceId);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), certificationResponse)
        );
    }

    @PostMapping("/today")
    public ResponseEntity<SingleResponse<RenewResponse>> certificateByGithub(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody RenewRequest renewRequest
    ) {

        RenewResponse renewResponse = certificationService.updateCertification(
                userPrincipal.getUser(),
                renewRequest);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), renewResponse)
        );
    }

    @GetMapping("/week/{instanceId}")
    public ResponseEntity<ListResponse<RenewResponse>> getCertification(
            @PathVariable Long instanceId,
            @RequestParam String identifier
    ) {
        User user = userService.findUserByIdentifier(identifier);
        Participant participant = participantProvider.findByJoinInfo(user.getId(), instanceId);
        List<RenewResponse> weekCertification = certificationService.getWeekCertification(
                participant.getId(), LocalDate.now());

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), weekCertification)
        );
    }

    @GetMapping("/total/{instanceId}")
    public ResponseEntity<ListResponse<RenewResponse>> getTotalCertifications(
            @PathVariable Long instanceId,
            @RequestParam String identifier
    ) {
        User user = userService.findUserByIdentifier(identifier);
        Participant participant = participantProvider.findByJoinInfo(user.getId(), instanceId);
        List<RenewResponse> totalCertification = certificationService.getTotalCertification(
                participant.getId(), LocalDate.now());

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), totalCertification)
        );
    }

    @GetMapping("/information/{instanceId}")
    public ResponseEntity<SingleResponse<CertificationInformation>> getCertificationInformation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long instanceId
    ) {

        Instance instance = instanceService.findInstanceById(instanceId);
        Participant participant = participantProvider.findByJoinInfo(
                userPrincipal.getUser().getId(),
                instanceId);

        CertificationInformation certificationInformation = certificationService.getCertificationInformation(
                instance, participant, LocalDate.now());

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), certificationInformation)
        );
    }
}
