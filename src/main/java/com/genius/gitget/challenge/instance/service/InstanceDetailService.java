package com.genius.gitget.challenge.instance.service;

import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_JOIN_INSTANCE;
import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_QUIT_INSTANCE;

import com.genius.gitget.challenge.certification.service.GithubProvider;
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
import com.genius.gitget.challenge.participant.service.ParticipantProvider;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesService;
import com.genius.gitget.global.util.exception.BusinessException;
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
    private final ParticipantProvider participantProvider;
    private final GithubProvider githubProvider;
    private final LikesRepository likesRepository;


    public InstanceResponse getInstanceDetailInformation(User user, Long instanceId) {
        Instance instance = instanceProvider.findById(instanceId);
        FileResponse fileResponse = filesService.convertToFileResponse(instance.getFiles());
        LikesInfo likesInfo = getLikesInfo(user.getId(), instance);

        if (participantProvider.hasParticipant(user.getId(), instanceId)) {
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

        return LikesInfo.createNotExist();
    }

    @Transactional
    public JoinResponse joinNewChallenge(User user, JoinRequest joinRequest) {
        User persistUser = userService.findUserById(user.getId());
        Instance instance = instanceProvider.findById(joinRequest.instanceId());

        String repository = joinRequest.repository();

        if (!verifyGithub(persistUser, repository) || !canJoinChallenge(persistUser, instance)) {
            throw new BusinessException(CAN_NOT_JOIN_INSTANCE);
        }

        instance.updateParticipantCount(1);
        Participant participant = Participant.createDefaultParticipant(repository);
        participant.setUserAndInstance(persistUser, instance);
        return JoinResponse.createJoinResponse(participantProvider.save(participant));
    }

    private boolean canJoinChallenge(User user, Instance instance) {
        boolean b = !participantProvider.hasParticipant(user.getId(), instance.getId());
        return (instance.getProgress() == Progress.PREACTIVITY) &&
                !participantProvider.hasParticipant(user.getId(), instance.getId());
    }

    private boolean verifyGithub(User user, String repository) {
        GitHub gitHub = githubProvider.getGithubConnection(user);
        String repositoryFullName = githubProvider.getRepoFullName(gitHub, repository);
        githubProvider.validateGithubRepository(gitHub, repositoryFullName);
        return true;
    }

    @Transactional
    public JoinResponse quitChallenge(User user, Long instanceId) {
        Instance instance = instanceProvider.findById(instanceId);
        Participant participant = participantProvider.findByJoinInfo(user.getId(), instanceId);

        if (instance.getProgress() == Progress.DONE) {
            throw new BusinessException(CAN_NOT_QUIT_INSTANCE);
        }

        if (instance.getProgress() == Progress.PREACTIVITY) {
            instance.updateParticipantCount(-1);
            participantProvider.delete(participant);
            return JoinResponse.createQuitResponse();
        }

        instance.updateParticipantCount(-1);
        participant.quitChallenge();
        return JoinResponse.createJoinResponse(participant);
    }
}
