package com.genius.gitget.challenge.certification.service;

import com.genius.gitget.challenge.certification.util.EncryptUtil;
import com.genius.gitget.challenge.participantinfo.service.ParticipantInfoService;
import com.genius.gitget.challenge.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CertificationService {
    private final GithubService githubService;
    private final EncryptUtil encryptUtil;
    private final ParticipantInfoService participantInfoService;


    @Transactional
    public void registerGithubPersonalToken(User user, String githubToken) {
        GitHub gitHub = githubService.getGithubConnection(githubToken);
        githubService.validateGithubConnection(gitHub, user.getIdentifier());

        String encryptedToken = encryptUtil.encryptPersonalToken(githubToken);
        user.updateGithubPersonalToken(encryptedToken);
    }

    @Transactional
    public void registerRepository(User user, Long instanceId, String repository) {
        String githubToken = encryptUtil.decryptPersonalToken(user.getGithubToken());
        GitHub gitHub = githubService.getGithubConnection(githubToken);

        String repositoryFullName = user.getIdentifier() + "/" + repository;
        githubService.validateGithubRepository(gitHub, repositoryFullName);

        participantInfoService.joinNewInstance(user, instanceId, repositoryFullName);
    }
}
