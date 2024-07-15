package com.genius.gitget.challenge.likes.service;

import static com.genius.gitget.global.security.constants.ProviderInfo.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
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
import com.genius.gitget.global.file.repository.FilesRepository;
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
@Rollback
class LikesServiceTest {
    static User user1;
    static Topic topic1;
    static Instance instance1, instance2, instance3;
    static Files files1, files2, files3, files4;
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
    @Autowired
    FilesRepository filesRepository;

    @BeforeEach
    void setup() {
        files1 = getSavedFiles("originalFileName", "savedFileName", "fileURL", FileType.INSTANCE);
        files2 = getSavedFiles("originalFileName", "savedFileName", "fileURL", FileType.TOPIC);

        user1 = getSavedUser("neo5188@gmail.com", GITHUB, "kimdozzi");

        topic1 = getSavedTopic("1일 1커밋", "BE");

        instance1 = getSavedInstance("1일 1커밋", "BE", 50, 100);
        instance2 = getSavedInstance("1일 1커밋", "BE", 50, 150);
        instance3 = getSavedInstance("1일 1알고리즘", "CS,BE,FE", 50, 200);

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
        List<Likes> likes = likesRepository.findAll();
        Long likesId = likes.get(0).getId();

        likesService.deleteLikes(user1, likesId);
        org.junit.jupiter.api.Assertions.assertThrows(BusinessException.class,
                () -> likesRepository.findById(likesId)
                        .orElseThrow(() -> new BusinessException(ErrorCode.LIKES_NOT_FOUND)));

        List<Likes> all = likesRepository.findAll();

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
    void 유저는_좋아요목록을_조회할_수_있다2() {

        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<UserLikesResponse> likesResponses = likesService.getLikesList(user1, pageRequest);
        for (UserLikesResponse likesResponse : likesResponses) {
            System.out.println(likesResponse.getInstanceId() + " " + likesResponse.getTitle() + " "
                    + likesResponse.getPointPerPerson());
        }
        assertThat(likesResponses.getContent().size()).isEqualTo(3);
        assertThat(likesResponses.getContent().get(2).getTitle()).isEqualTo("1일 1커밋");
        assertThat(likesResponses.getContent().get(2).getPointPerPerson()).isEqualTo(100);

        assertThat(likesResponses.getContent().get(1).getTitle()).isEqualTo("1일 1커밋");
        assertThat(likesResponses.getContent().get(1).getPointPerPerson()).isEqualTo(150);

        assertThat(likesResponses.getContent().get(0).getTitle()).isEqualTo("1일 1알고리즘");
        assertThat(likesResponses.getContent().get(0).getPointPerPerson()).isEqualTo(200);
    }


    private User getSavedUser(String identifier, ProviderInfo providerInfo, String nickname) {
        return userRepository.save(
                User.builder()
                        .identifier(identifier)
                        .providerInfo(providerInfo)
                        .role(Role.ADMIN)
                        .nickname(nickname)
                        .build()
        );
    }

    private Topic getSavedTopic(String title, String tags) {
        return topicRepository.save(
                Topic.builder()
                        .title(title)
                        .tags(tags)
                        .description("토픽 설명")
                        .pointPerPerson(100)
                        .build()
        );
    }

    private Instance getSavedInstance(String title, String tags, int participantCnt, int pointPerPerson) {
        LocalDateTime now = LocalDateTime.now();
        Instance instance = instanceRepository.save(
                Instance.builder()
                        .tags(tags)
                        .title(title)
                        .description("description")
                        .progress(Progress.PREACTIVITY)
                        .pointPerPerson(pointPerPerson)
                        .certificationMethod("인증 방법")
                        .startedDate(now)
                        .completedDate(now.plusDays(1))
                        .build()
        );
        instance.updateParticipantCount(participantCnt);
        return instance;
    }

    private Files getSavedFiles(String originalFilename, String savedFilename, String fileURL, FileType fileType) {
        return filesRepository.save(
                Files.builder()
                        .originalFilename(originalFilename)
                        .savedFilename(savedFilename)
                        .fileURI(fileURL)
                        .fileType(fileType)
                        .build()
        );
    }
}