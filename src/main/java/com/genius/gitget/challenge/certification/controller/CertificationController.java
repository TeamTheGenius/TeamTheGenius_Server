package com.genius.gitget.challenge.certification.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.certification.dto.CertificationInformation;
import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.InstancePreviewResponse;
import com.genius.gitget.challenge.certification.dto.TotalResponse;
import com.genius.gitget.challenge.certification.dto.WeekResponse;
import com.genius.gitget.challenge.certification.facade.CertificationFacade;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.service.InstanceService;
import com.genius.gitget.challenge.myChallenge.dto.ActivatedResponse;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.service.ParticipantService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.util.annotation.GitGetUser;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import com.genius.gitget.global.util.response.dto.SlicingResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
    private final InstanceService instanceService;
    private final CertificationFacade certificationFacade;
    private final ParticipantService participantService;


    @GetMapping("/{instanceId}")
    public ResponseEntity<SingleResponse<InstancePreviewResponse>> getInstanceInformation(
            @PathVariable Long instanceId
    ) {
        InstancePreviewResponse instancePreviewResponse = certificationFacade.getInstancePreview(instanceId);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), instancePreviewResponse)
        );
    }

    @PostMapping("/today")
    public ResponseEntity<SingleResponse<CertificationResponse>> certificateByGithub(
            @GitGetUser User user,
            @RequestBody CertificationRequest certificationRequest
    ) {
        CertificationResponse certificationResponse = certificationFacade.updateCertification(
                user, certificationRequest);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), certificationResponse)
        );
    }

    @PostMapping("/pass")
    public ResponseEntity<SingleResponse<ActivatedResponse>> passCertification(
            @GitGetUser User user,
            @RequestBody CertificationRequest certificationRequest
    ) {
        ActivatedResponse activatedResponse = certificationFacade.passCertification(
                user.getId(), certificationRequest);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), activatedResponse)
        );
    }

    @GetMapping("/week/{instanceId}")
    public ResponseEntity<SingleResponse<WeekResponse>> getWeekCertification(
            @GitGetUser User user,
            @PathVariable Long instanceId
    ) {
        LocalDate kstDate = DateUtil.convertToKST(LocalDateTime.now());
        Participant participant = participantService.findByJoinInfo(user.getId(), instanceId);
        WeekResponse weekResponse = certificationFacade.getMyWeekCertifications(participant.getId(), kstDate);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), weekResponse)
        );
    }

    @GetMapping("/week/all/{instanceId}")
    public ResponseEntity<SlicingResponse<WeekResponse>> getAllUserWeekCertification(
            @GitGetUser User user,
            @PathVariable Long instanceId,
            @PageableDefault Pageable pageable
    ) {
        LocalDate kstDate = DateUtil.convertToKST(LocalDateTime.now());
        Slice<WeekResponse> certifications = certificationFacade.getOthersWeekCertifications(
                user.getId(), instanceId, kstDate, pageable);

        return ResponseEntity.ok().body(
                new SlicingResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), certifications)
        );
    }

    @GetMapping("/total/{instanceId}")
    public ResponseEntity<SingleResponse<TotalResponse>> getTotalCertifications(
            @PathVariable Long instanceId,
            @RequestParam Long userId
    ) {
        LocalDate kstDate = DateUtil.convertToKST(LocalDateTime.now());
        User user = userService.findUserById(userId);
        Participant participant = participantService.findByJoinInfo(user.getId(), instanceId);
        TotalResponse totalResponse = certificationFacade.getTotalCertification(
                participant.getId(), kstDate);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), totalResponse)
        );
    }

    @GetMapping("/information/{instanceId}")
    public ResponseEntity<SingleResponse<CertificationInformation>> getCertificationInformation(
            @GitGetUser User user,
            @PathVariable Long instanceId
    ) {

        LocalDate kstDate = DateUtil.convertToKST(LocalDateTime.now());
        Instance instance = instanceService.findInstanceById(instanceId);
        Participant participant = participantService.findByJoinInfo(user.getId(), instanceId);

        CertificationInformation certificationInformation = certificationFacade.getCertificationInformation(
                instance, participant, kstDate);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), certificationInformation)
        );
    }
}
