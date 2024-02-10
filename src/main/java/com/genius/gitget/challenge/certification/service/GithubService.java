package com.genius.gitget.challenge.certification.service;

import com.genius.gitget.challenge.certification.util.EncryptUtil;
import com.genius.gitget.challenge.participantinfo.service.ParticipantInfoService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GithubService {
    private final UserService userService;
    private final ParticipantInfoService participantInfoService;
    private final GithubProvider githubProvider;
    private final EncryptUtil encryptUtil;

    @Transactional
    public void registerGithubPersonalToken(User user, String githubToken) {
        GitHub gitHub = githubProvider.getGithubConnection(githubToken);
        githubProvider.validateGithubConnection(gitHub, user.getIdentifier());

        String encryptedToken = encryptUtil.encryptPersonalToken(githubToken);
        user.updateGithubPersonalToken(encryptedToken);
        userService.save(user);
    }

    @Transactional
    public void registerRepository(User user, Long instanceId, String repository) {
        GitHub gitHub = githubProvider.getGithubConnection(user);

        String repositoryFullName = user.getIdentifier() + "/" + repository;
        githubProvider.validateGithubRepository(gitHub, repositoryFullName);

        participantInfoService.joinNewInstance(user.getId(), instanceId, repositoryFullName);
    }

    public List<String> getPublicRepositories(User user) {
        GitHub gitHub = githubProvider.getGithubConnection(user);
        List<GHRepository> repositoryList = githubProvider.getRepositoryList(gitHub);
        return repositoryList.stream()
                .map(String::valueOf)
                .toList();
    }
}
