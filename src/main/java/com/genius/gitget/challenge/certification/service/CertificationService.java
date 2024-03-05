package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;
import static com.genius.gitget.global.util.exception.ErrorCode.CERTIFICATION_UNABLE;

import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.dto.CertificationInformation;
import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.InstancePreviewResponse;
import com.genius.gitget.challenge.certification.dto.WeekResponse;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.service.InstanceProvider;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
import com.genius.gitget.challenge.item.service.UserItemProvider;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.service.ParticipantProvider;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationService {
    private final FilesService filesService;
    private final GithubProvider githubProvider;
    private final CertificationProvider certificationProvider;
    private final ParticipantProvider participantProvider;
    private final InstanceProvider instanceProvider;
    private final UserItemProvider userItemProvider;


    public List<CertificationResponse> getWeekCertification(Long participantId, LocalDate currentDate) {
        LocalDate startDate = participantProvider.getInstanceStartDate(participantId);
        int curAttempt = DateUtil.getWeekAttempt(startDate, currentDate);

        List<Certification> certifications = certificationProvider.findByDuration(
                DateUtil.getWeekStartDate(currentDate),
                currentDate,
                participantId);

        return convertToCertificationResponse(certifications, curAttempt, currentDate);
    }

    public Slice<WeekResponse> getAllWeekCertification(Long userId, Long instanceId,
                                                       LocalDate currentDate, Pageable pageable) {
        Slice<Participant> participants = participantProvider.findAllByInstanceId(userId, instanceId, pageable);
        return participants.map(
                participant -> convertToWeekResponse(participant, currentDate)
        );
    }

    private WeekResponse convertToWeekResponse(Participant participant, LocalDate currentDate) {
        User user = participant.getUser();
        LocalDate startDate = participantProvider.getInstanceStartDate(participant.getId());

        List<Certification> certifications = certificationProvider.findByDuration(
                DateUtil.getWeekStartDate(currentDate),
                currentDate,
                participant.getId());
        List<CertificationResponse> certificationResponses = convertToCertificationResponse(
                certifications,
                DateUtil.getWeekAttempt(startDate, currentDate),
                currentDate);
        FileResponse fileResponse = FileResponse.create(user.getFiles());

        return WeekResponse.create(user, fileResponse, certificationResponses);
    }

    public List<CertificationResponse> getTotalCertification(Long participantId, LocalDate currentDate) {
        LocalDate startDate = participantProvider.getInstanceStartDate(participantId);
        int curAttempt = DateUtil.getAttemptCount(startDate, currentDate);

        List<Certification> certifications = certificationProvider.findByDuration(
                startDate, currentDate, participantId);

        return convertToCertificationResponse(certifications, curAttempt, currentDate);
    }

    private List<CertificationResponse> convertToCertificationResponse(List<Certification> certifications,
                                                                       int curAttempt, LocalDate currentDate) {
        List<CertificationResponse> result = new ArrayList<>();
        Map<Integer, Certification> certificationMap = convertToMap(certifications);
        currentDate = DateUtil.getWeekStartDate(currentDate).minusDays(1);

        for (int cur = 1; cur <= curAttempt; cur++) {
            currentDate = currentDate.plusDays(1);
            if (certificationMap.containsKey(cur)) {
                result.add(CertificationResponse.createSuccess(certificationMap.get(cur)));
                continue;
            }
            result.add(CertificationResponse.createFail(cur, currentDate));
        }

        return result;
    }

    private Map<Integer, Certification> convertToMap(List<Certification> certifications) {
        Map<Integer, Certification> certificationMap = new HashMap<>();

        for (Certification certification : certifications) {
            certificationMap.put(certification.getCurrentAttempt(), certification);
        }
        return certificationMap;
    }

    @Transactional
    public CertificationResponse passCertification(Long userId, CertificationRequest certificationRequest) {
        Instance instance = instanceProvider.findById(certificationRequest.instanceId());
        Participant participant = participantProvider.findByJoinInfo(userId, instance.getId());
        LocalDate targetDate = certificationRequest.targetDate();

        UserItem userItem = userItemProvider.findUserItemByUser(userId, ItemCategory.CERTIFICATION_PASSER);
        Optional<Certification> optional = certificationProvider.findByDate(targetDate, participant.getId());

        validatePassCondition(userItem, optional);

        //TODO: 리팩토링 시급...
        if (optional.isPresent()) {
            optional.get().updateToPass(targetDate);
            return CertificationResponse.createSuccess(optional.get());
        }

        Certification certification = Certification.createPassed(targetDate);
        certification.setParticipant(participant);
        certificationProvider.save(certification);

        return CertificationResponse.createSuccess(certification);
    }

    private void validatePassCondition(UserItem userItem, Optional<Certification> optional) {
        if (!userItem.hasItem()) {
            throw new BusinessException(ErrorCode.USER_ITEM_NOT_FOUND);
        }
        if (optional.isEmpty() || optional.get().getCertificationStatus() == NOT_YET) {
            return;
        }
        throw new BusinessException(ErrorCode.CAN_NOT_USE_PASS_ITEM);
    }

    @Transactional
    public CertificationResponse updateCertification(User user, CertificationRequest certificationRequest) {
        GitHub gitHub = githubProvider.getGithubConnection(user);
        Instance instance = instanceProvider.findById(certificationRequest.instanceId());
        Participant participant = participantProvider.findByJoinInfo(user.getId(), instance.getId());

        if (!canCertificate(instance, certificationRequest.targetDate())) {
            throw new BusinessException(CERTIFICATION_UNABLE);
        }

        List<String> pullRequests = getPullRequestLink(
                gitHub,
                participant.getRepositoryName(),
                certificationRequest.targetDate());

        Certification certification = createOrUpdate(participant, certificationRequest.targetDate(), pullRequests);

        return CertificationResponse.createSuccess(certification);
    }

    private Certification createOrUpdate(Participant participant, LocalDate targetDate, List<String> pullRequests) {
        Optional<Certification> optional = certificationProvider.findByDate(targetDate, participant.getId());
        if (optional.isPresent()) {
            return certificationProvider.update(optional.get(), targetDate, pullRequests);
        }

        return certificationProvider.createCertification(participant, targetDate, pullRequests);
    }

    private boolean canCertificate(Instance instance, LocalDate targetDate) {
        boolean isValidPeriod = targetDate.isAfter(instance.getStartedDate().toLocalDate()) &&
                targetDate.isBefore(instance.getCompletedDate().toLocalDate());

        return ((instance.getProgress() == Progress.ACTIVITY) && isValidPeriod);
    }

    private List<String> getPullRequestLink(GitHub gitHub, String repositoryName, LocalDate targetDate) {
        List<GHPullRequest> ghPullRequests = githubProvider.getPullRequestByDate(gitHub, repositoryName, targetDate);

        return ghPullRequests.stream()
                .map(pr -> pr.getHtmlUrl().toString())
                .toList();
    }

    public InstancePreviewResponse getInstancePreview(Long instanceId) {
        Instance instance = instanceProvider.findById(instanceId);
        FileResponse fileResponse = filesService.getEncodedFile(instance.getFiles());
        return InstancePreviewResponse.createByEntity(instance, fileResponse);
    }

    @Transactional
    public CertificationInformation getCertificationInformation(Instance instance, Participant participant,
                                                                LocalDate targetDate) {
        //성공 인증 개수
        int successCount = certificationProvider.countCertificatedByStatus(participant.getId(), CERTIFICATED,
                targetDate);

        //targetDate 기준 현재 진행 일차
        int currentAttempt = DateUtil.getAttemptCount(instance.getStartedDate().toLocalDate(), targetDate);

        //남은 인증 개수 = 전체 일차 - 오늘 회차
        int totalAttempt = instance.getTotalAttempt();
        int remainAttempt = totalAttempt - currentAttempt;

        return CertificationInformation.builder()
                .repository(participant.getRepositoryName())
                .successPercent(getSuccessPercent(successCount, currentAttempt))
                .totalAttempt(totalAttempt)
                .currentAttempt(currentAttempt)
                .pointPerPerson(instance.getPointPerPerson())
                .successCount(successCount)
                .failureCount(currentAttempt - successCount)
                .remainCount(remainAttempt)
                .build();
    }

    private double getSuccessPercent(int successCount, int currentAttempt) {
        double successPercent = (double) successCount / (double) currentAttempt * 100;
        return Math.round(successPercent * 100 / 100.0);
    }
}