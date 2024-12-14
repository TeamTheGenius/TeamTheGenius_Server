package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_REPOSITORY_INCORRECT;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_TOKEN_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.dto.github.PullRequestResponse;
import com.genius.gitget.challenge.certification.facade.GithubFacade;
import com.genius.gitget.challenge.certification.util.EncryptUtil;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.util.user.UserFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletionException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
public class GithubFacadeTest {
    @Autowired
    private EncryptUtil encryptUtil;
    @Autowired
    private GithubFacade githubFacade;
    @Autowired
    private UserRepository userRepository;

    @Value("${github.yeon-personalKey}")
    private String githubToken;
    @Value("${github.yeon-githubId}")
    private String githubId;
    @Value("${github.yeon-repository}")
    private String targetRepo;

    private User user;

    @BeforeEach
    void setup() {
        user = userRepository.save(UserFactory.createByInfo(githubId, Role.USER));
    }

    @Nested
    @DisplayName("Github Token 등록 시")
    class context_register_github_token {
        @Nested
        @DisplayName("Github 연결에 이상이 없다면")
        class describe_connection_valid {
            @Test
            @DisplayName("Github token을 암호화하여 User 엔티티에 저장한다.")
            public void it_save_token_to_user_entity() {
                String encrypted = encryptUtil.encrypt(githubToken);

                githubFacade.registerGithubPersonalToken(user, githubToken).join();

                assertThat(user.getGithubToken()).isEqualTo(encrypted);
            }
        }

        @Nested
        @DisplayName("깃허브 계정 정보와 identifier 비교 시")
        class describe_mismatch_id {
            @Test
            @DisplayName("사용자의 identifier와 일치하지 않으면 GITHUB_ID_INCORRECT 예외가 발생한다.")
            public void it_throws_GITHUB_ID_INCORRECT_exception() {
                user = userRepository.save(UserFactory.createByInfo("incorrectID", Role.USER));

                assertThatThrownBy(() -> githubFacade.registerGithubPersonalToken(user, githubToken).join())
                        .isInstanceOf(CompletionException.class)
                        .hasCause(new BusinessException(ErrorCode.GITHUB_ID_INCORRECT));
            }
        }
    }

    @Nested
    @DisplayName("사용자의 Github token 유효성 확인 시")
    class context_validate_github_token {
        @Nested
        @DisplayName("사용자의 깃허브 토큰이 유효하다면")
        class describe_token_valid {
            @BeforeEach
            void init() {
                githubFacade.registerGithubPersonalToken(user, githubToken).join();
            }

            @Test
            @DisplayName("예외가 발생하지 않는다.")
            public void it_does_not_throw_exception() {
                Assertions.assertThatNoException()
                        .isThrownBy(() -> githubFacade.verifyGithubToken(user));
            }
        }

        @Nested
        @DisplayName("사용자의 깃허브 토큰이 유효하지 않은 경우")
        class describe_token_invalid {
            @Test
            @DisplayName("토큰이 null이거나 빈 문자열인 경우 GITHUB_TOKEN_NOT_FOUND 예외가 발생한다.")
            public void it_throws_GITHUB_TOKEN_NOT_FOUND_exception() {
                assertThatThrownBy(() -> githubFacade.verifyGithubToken(user).join())
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(GITHUB_TOKEN_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Repository 유효성 확인 시")
    class context_register_repository {
        @Nested
        @DisplayName("사용자로부터 Github token을 불러올 때")
        class describe_get_github_token {
            @Test
            @DisplayName("사용자에게 Github token이 저장되어 있지 않다면 GITHUB_TOKEN_NOT_FOUND 예외가 발생한다.")
            public void it_throws_GITHUB_TOKEN_NOT_FOUND_exception() {
                assertThatThrownBy(() -> githubFacade.verifyRepository(user, targetRepo))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(GITHUB_TOKEN_NOT_FOUND.getMessage());
            }
        }

        @Nested
        @DisplayName("repository의 이름을 전달했을 때")
        class describe_pass_repository_name {
            @BeforeEach
            void setup() {
                user.updateGithubPersonalToken(encryptUtil.encrypt(githubToken));
            }

            @Test
            @DisplayName("해당 깃허브 계정에 repository가 존재한다면 예외가 발생하지 않는다.")
            public void it_not_throw_exception() {
                assertThatNoException().isThrownBy(() -> {
                    githubFacade.verifyRepository(user, targetRepo);
                });
            }

            @Test
            @DisplayName("해당 깃허브 계정에 Repository가 존재하지 않는다면 GITHUB_REPOSITORY_INCORRECT 예외가 발생한다.")
            public void it_throw_GITHUB_REPOSITORY_INCORRECT_exception() {
                String fakeRepoName = "Fake";

                assertThatThrownBy(() -> githubFacade.verifyRepository(user, fakeRepoName))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(GITHUB_REPOSITORY_INCORRECT.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("특정 일자의 PR 내역 확인 시")
    class context_check_PR {
        LocalDate targetDate;

        @Nested
        @DisplayName("조건 확인 시")
        class describe_validate_condition {
            @Test
            @DisplayName("사용자의 Github token이 저장되어 있지 않을 때 GITHUB_TOKEN_NOT_FOUND 예외가 발생한다.")
            public void it_throws_GITHUB_TOKEN_NOT_FOUND_exception() {
                targetDate = LocalDate.of(2024, 1, 4);
                assertThatThrownBy(() -> githubFacade.getPullRequestListByDate(user, targetRepo, targetDate))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(GITHUB_TOKEN_NOT_FOUND.getMessage());
            }

            @Test
            @DisplayName("repository 이름이 유효하지 않을 때 예외가 발생한다.")
            public void it_throws_GITHUB_REPOSITORY_INCORRECT_exception() {
                String fakeRepo = "fake Repo";
                targetDate = LocalDate.of(2024, 2, 5);
                user.updateGithubPersonalToken(encryptUtil.encrypt(githubToken));

                assertThatThrownBy(() -> githubFacade.getPullRequestListByDate(user, fakeRepo, targetDate))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(GITHUB_REPOSITORY_INCORRECT.getMessage());
            }
        }

        @Nested
        @DisplayName("PR을 확인할 수 있는 유효한 조건일 때")
        class describe_valid_condition {
            @BeforeEach
            void setup() {
                user.updateGithubPersonalToken(encryptUtil.encrypt(githubToken));
            }

            @Test
            @DisplayName("특정 일자에 PR이 존재하지 않는다면 빈 리스트를 반환한다.")
            public void it_returns_emptyList_when_pr_not_exist() {
                LocalDate targetDate = LocalDate.of(2024, 1, 4);

                List<PullRequestResponse> pullRequestResponses = githubFacade.getPullRequestListByDate(
                        user, targetRepo, targetDate);

                assertThat(pullRequestResponses.size()).isEqualTo(0);
            }

            @Test
            @DisplayName("특정 일자에 PR이 존재한다면 목록을 불러 올 수  있다.")
            public void it_returns_pr_list() {
                LocalDate targetDate = LocalDate.of(2024, 2, 5);

                List<PullRequestResponse> pullRequestResponses = githubFacade.getPullRequestListByDate(
                        user, targetRepo, targetDate);

                assertThat(pullRequestResponses.size()).isEqualTo(1);
            }
        }
    }

    @Nested
    @DisplayName("특정 레포지토리에 PR이 존재하는지 확인 시")
    class context_verify_pr {
        LocalDate targetDate;

        @BeforeEach
        void setup() {
            targetDate = LocalDate.of(2024, 3, 5);
            user.updateGithubPersonalToken(encryptUtil.encrypt(githubToken));
        }

        @Nested
        @DisplayName("PR 확인 시")
        class describe_check_pr {
            @Test
            @DisplayName("존재하지 않는다면 GITHUB_PR_NOT_FOUND 예외가 발생한다.")
            public void it_throws_GITHUB_PR_NOT_FOUND_exception() {
                assertThatThrownBy(() -> githubFacade.verifyPullRequest(user, targetRepo, targetDate))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ErrorCode.GITHUB_PR_NOT_FOUND.getMessage());
            }

            @Test
            @DisplayName("특정 일자에 PR이 존재한다면 결과를 List로 반환한다.")
            public void it_returns_list_if_pr_exist() {
                targetDate = LocalDate.of(2024, 2, 5);

                //when
                List<PullRequestResponse> pullRequestResponses = githubFacade.verifyPullRequest(user, targetRepo,
                        targetDate);

                //then
                assertThat(pullRequestResponses.size()).isNotZero();

            }
        }
    }
}
