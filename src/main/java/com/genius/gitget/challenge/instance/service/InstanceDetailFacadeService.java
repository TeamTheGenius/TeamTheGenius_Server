package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_JOIN_INSTANCE;
import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_QUIT_INSTANCE;

import com.genius.gitget.challenge.certification.service.GithubService;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.detail.InstanceResponse;
import com.genius.gitget.challenge.instance.dto.detail.JoinRequest;
import com.genius.gitget.challenge.instance.dto.detail.JoinResponse;
import com.genius.gitget.challenge.instance.dto.detail.LikesInfo;
import com.genius.gitget.challenge.likes.service.LikesService;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.service.ParticipantService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesManager;
import com.genius.gitget.global.util.exception.BusinessException;
import java.time.LocalDate;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Component;

@Component
public class InstanceDetailFacadeService implements InstanceDetailFacade {

    private final InstanceService instanceService;
    private final FilesManager filesManager;
    private final ParticipantService participantService;
    private final LikesService likesService;
    private final UserService userService;
    private final GithubService githubService;

    public InstanceDetailFacadeService(InstanceService instanceService, FilesManager filesManager,
                                       ParticipantService participantService, LikesService likesService,
                                       UserService userService, GithubService githubService) {
        this.instanceService = instanceService;
        this.filesManager = filesManager;
        this.participantService = participantService;
        this.likesService = likesService;
        this.userService = userService;
        this.githubService = githubService;
    }

    @Override
    public InstanceResponse getInstanceDetailInformation(User user, Long instanceId) {

        // 인스턴스 정보
        Instance instance = instanceService.findInstanceById(instanceId);

        // 파일 객체 생성
        FileResponse fileResponse = filesManager.convertToFileResponse(instance.getFiles());

        // 좋아요 정보
        LikesInfo likesInfo = likesService.getLikesInfo(user.getId(), instance);

        if (participantService.hasJoinedParticipant(user.getId(), instance.getId())) {
            return InstanceResponse.createByEntity(instance, likesInfo, JoinStatus.YES, fileResponse);
        }
        return InstanceResponse.createByEntity(instance, likesInfo, JoinStatus.NO, fileResponse);
    }


    @Override
    public JoinResponse joinNewChallenge(User user, JoinRequest joinRequest) {

        User persistUser = userService.findUserById(user.getId());

        Instance instance = instanceService.findInstanceById(joinRequest.instanceId());

        String repository = joinRequest.repository();

        validateJoinDate(instance, joinRequest.todayDate());
        validateInstanceCondition(persistUser, instance);
        validateGithub(persistUser, repository);

        instance.updateParticipantCount(1);
        Participant participant = Participant.createDefaultParticipant(repository);
        participant.setUserAndInstance(persistUser, instance);
        return JoinResponse.createJoinResponse(participantService.save(participant));
    }

    private void validateJoinDate(Instance instance, LocalDate todayDate) {
        LocalDate startedDate = instance.getStartedDate().toLocalDate();

        if (todayDate.isBefore(startedDate)) {
            return;
        }
        throw new BusinessException(CAN_NOT_JOIN_INSTANCE);
    }

    private void validateInstanceCondition(User user, Instance instance) {
        boolean isParticipated = participantService.hasJoinedParticipant(user.getId(), instance.getId());
        if ((instance.getProgress() == Progress.PREACTIVITY) && !isParticipated) {
            return;
        }
        throw new BusinessException(CAN_NOT_JOIN_INSTANCE);
    }

    private void validateGithub(User user, String repository) {
        GitHub gitHub = githubService.getGithubConnection(user).join();
        String repositoryFullName = githubService.getRepoFullName(gitHub, repository);
        githubService.validateGithubRepository(gitHub, repositoryFullName);
    }

    @Override
    public JoinResponse quitChallenge(User user, Long instanceId) {
        Instance instance = instanceService.findInstanceById(instanceId);
        Participant participant = participantService.findByJoinInfo(user.getId(), instanceId);

        if (instance.getProgress() == Progress.DONE) {
            throw new BusinessException(CAN_NOT_QUIT_INSTANCE);
        }

        if (instance.getProgress() == Progress.PREACTIVITY) {
            instance.updateParticipantCount(-1);
            participantService.delete(participant);
            return JoinResponse.createQuitResponse();
        }

        instance.updateParticipantCount(-1);
        participant.quitChallenge();
        return JoinResponse.createJoinResponse(participant);
    }
}
