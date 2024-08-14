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
import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.likes.repository.LikesRepository;
import com.genius.gitget.challenge.participant.domain.JoinStatus;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.service.ParticipantService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InstanceDetailService {
    private final UserService userService;
    private final FilesService filesService;
    private final InstanceProvider instanceProvider;
    private final ParticipantService participantService;
    private final GithubService githubService;
    private final LikesRepository likesRepository;


    public InstanceResponse getInstanceDetailInformation(User user, Long instanceId) {
        Instance instance = instanceProvider.findById(instanceId);
        FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());
        LikesInfo likesInfo = getLikesInfo(user.getId(), instance);

        if (participantService.hasJoinedParticipant(user.getId(), instanceId)) {
            return InstanceResponse.createByEntity(instance, likesInfo, JoinStatus.YES, fileResponse);
        }

        return InstanceResponse.createByEntity(instance, likesInfo, JoinStatus.NO, fileResponse);
    }

    private LikesInfo getLikesInfo(Long userId, Instance instance) {
        Optional<Likes> optionalLikes = likesRepository.findSpecificLike(userId, instance.getId());
        if (optionalLikes.isPresent()) {
            Likes likes = optionalLikes.get();
            return LikesInfo.createExist(likes.getId(), instance.getLikesCount());
        }

        return LikesInfo.createNotExist(instance.getLikesCount());
    }

    @Transactional
    public JoinResponse joinNewChallenge(User user, JoinRequest joinRequest) {
        User persistUser = userService.findUserById(user.getId());
        Instance instance = instanceProvider.findById(joinRequest.instanceId());

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
        GitHub gitHub = githubService.getGithubConnection(user);
        String repositoryFullName = githubService.getRepoFullName(gitHub, repository);
        githubService.validateGithubRepository(gitHub, repositoryFullName);
    }

    @Transactional
    public JoinResponse quitChallenge(User user, Long instanceId) {
        Instance instance = instanceProvider.findById(instanceId);
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
