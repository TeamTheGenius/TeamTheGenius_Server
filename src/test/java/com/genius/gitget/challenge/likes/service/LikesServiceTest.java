package com.genius.gitget.challenge.likes.service;

import static com.genius.gitget.global.security.constants.ProviderInfo.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.likes.dto.UserLikesResponse;
import com.genius.gitget.challenge.likes.repository.LikesRepository;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LikesServiceTest {
    static User user1;
    static Topic topic1;
    static Instance instance1, instance2, instance3;
    @Autowired
    UserRepository userRepository;
    @Autowired
    InstanceRepository instanceRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    LikesRepository likesRepository;
    @Autowired
    LikesService likesService;

    @BeforeEach
    void setup() {
        user1 = getSavedUser("neo5188@gmail.com", GITHUB, "kimdozzi");

        topic1 = getSavedTopic("1일 1커밋", "BE");

        instance1 = getSavedInstance("1일 1커밋", "BE", 50);
        instance2 = getSavedInstance("1일 1커밋", "BE", 100);
        instance3 = getSavedInstance("1일 1알고리즘", "CS,BE,FE", 500);

        //== 연관관계 ==//
        instance1.setTopic(topic1);
        instance2.setTopic(topic1);
        instance3.setTopic(topic1);

        Likes likes1 = new Likes(user1, instance1);
        Likes likes2 = new Likes(user1, instance2);
        Likes likes3 = new Likes(user1, instance3);
        likesRepository.save(likes1);
        likesRepository.save(likes2);
        likesRepository.save(likes3);
    }

    @Test
    void 유저_좋아요_목록_추가() {
        List<Likes> all = likesRepository.findAll();
        int cnt = 0;
        for (Likes likes : all) {
            if (likes.getUser().getIdentifier().equals("neo5188@gmail.com") && likes.getInstance().getTitle()
                    .equals("1일 1커밋")) {
                cnt++;
            }
        }
        Assertions.assertThat(all.size() - 1).isEqualTo(cnt);
    }

    @Test
    void 유저_좋아요_목록_삭제() {
        likesService.deleteLikes(user1, 1L);
        List<Likes> all = likesRepository.findAll();

        org.junit.jupiter.api.Assertions.assertThrows(BusinessException.class,
                () -> likesRepository.findById(1L).orElseThrow(() -> new BusinessException(ErrorCode.LIKES_NOT_FOUND)));

        Assertions.assertThat(all.size()).isEqualTo(2);
    }

    @Test
    void 유저는_좋아요목록을_조회할_수_있다1() {
        List<Likes> all = likesRepository.findAll();

        for (int i = 0; i < all.size(); i++) {
            if (i <= 1) {
                assertThat(all.get(i).getInstance().getTitle()).isEqualTo("1일 1커밋");
            } else {
                assertThat(all.get(i).getInstance().getTitle()).isEqualTo("1일 1알고리즘");
            }
        }
        assertThat(all.size()).isEqualTo(3);
    }

    @Test
    @Rollback(value = false)
    void 유저는_좋아요목록을_조회할_수_있다2() {
        Files files1 = Files.builder()
                .fileType(FileType.INSTANCE)
                .originalFilename("originalFilenameA")
                .savedFilename("savedFilenameA")
                .fileURI("fileURI")
                .build();

        Files files2 = Files.builder()
                .fileType(FileType.INSTANCE)
                .originalFilename("originalFilenameB")
                .savedFilename("savedFilenameB")
                .fileURI("fileURI")
                .build();

        Files files3 = Files.builder()
                .fileType(FileType.PROFILE)
                .originalFilename("originalFilename")
                .savedFilename("savedFilename")
                .fileURI("fileURI")
                .build();

        instance1.setFiles(files1);
        instance2.setFiles(files2);
        user1.setFiles(files3);
        userRepository.save(user1);

        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<UserLikesResponse> likesResponses = likesService.getLikesList(user1, pageRequest);
        for (UserLikesResponse likesRespons : likesResponses) {
            System.out.println(likesRespons.getInstanceId() + " " + likesRespons.getTitle() + " "
                    + likesRespons.getPointPerPerson());
            System.out.println(likesRespons.getFileResponse());
        }
        assertThat(likesResponses.getContent().size()).isEqualTo(3);
        assertThat(likesResponses.getContent().get(0).getTitle()).isEqualTo("1일 1커밋");
        assertThat(likesResponses.getContent().get(0).getPointPerPerson()).isEqualTo(50);

        assertThat(likesResponses.getContent().get(1).getTitle()).isEqualTo("1일 1커밋");
        assertThat(likesResponses.getContent().get(1).getPointPerPerson()).isEqualTo(100);

        assertThat(likesResponses.getContent().get(2).getTitle()).isEqualTo("1일 1알고리즘");
        assertThat(likesResponses.getContent().get(2).getPointPerPerson()).isEqualTo(500);
    }


    private User getSavedUser(String identifier, ProviderInfo providerInfo, String nickname) {
        User user = userRepository.save(
                User.builder()
                        .identifier(identifier)
                        .providerInfo(providerInfo)
                        .role(Role.ADMIN)
                        .nickname(nickname)
                        .build()
        );
        return user;
    }

    private Topic getSavedTopic(String title, String tags) {
        Topic topic = topicRepository.save(
                Topic.builder()
                        .title(title)
                        .tags(tags)
                        .description("토픽 설명")
                        .pointPerPerson(100)
                        .build()
        );
        return topic;
    }

    private Instance getSavedInstance(String title, String tags, int participantCnt) {
        LocalDateTime now = LocalDateTime.now();
        Instance instance = instanceRepository.save(
                Instance.builder()
                        .tags(tags)
                        .title(title)
                        .description("description")
                        .progress(Progress.PREACTIVITY)
                        .pointPerPerson(100)
                        .certificationMethod("인증 방법")
                        .startedDate(now)
                        .completedDate(now.plusDays(1))
                        .build()
        );
        instance.updateParticipantCount(participantCnt);
        return instance;
    }
}