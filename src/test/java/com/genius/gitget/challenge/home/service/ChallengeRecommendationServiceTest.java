package com.genius.gitget.challenge.home.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.dto.home.HomeInstanceResponse;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.instance.service.ChallengeRecommendationService;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.topic.repository.TopicRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ChallengeRecommendationServiceTest {
    @Autowired
    ChallengeRecommendationService challengeRecommendationService;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    InstanceRepository instanceRepository;

    @Test
    @DisplayName("사용자가 설정한 태그와 하나라도 맞는 시작 전인 인스턴스의 수 만큼 반환한다.")
    public void should_getSuggestions_when_passUserTags() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Direction.DESC, "participantCount"));
        getSavedInstance("title1", "BE,AI", 20);
        getSavedInstance("title2", "BE,Spring", 10);
        getSavedInstance("title3", "FE,BE", 10);
        getSavedInstance("title4", "FE,React", 12);

        User user = User.builder().tags("BE,React").build();

        //when
        Slice<HomeInstanceResponse> recommendations = challengeRecommendationService.getRecommendations(user,
                pageRequest);

        //then
        assertThat(recommendations.getContent().size()).isEqualTo(4);
        assertThat(recommendations.getContent().get(0).title()).isEqualTo("title1");
        assertThat(recommendations.getContent().get(0).participantCnt()).isEqualTo(20);
        assertThat(recommendations.getContent().get(0).pointPerPerson()).isEqualTo(100);

        assertThat(recommendations.getContent().get(1).title()).isEqualTo("title2");
        assertThat(recommendations.getContent().get(1).participantCnt()).isEqualTo(10);
        assertThat(recommendations.getContent().get(1).pointPerPerson()).isEqualTo(100);
    }

    @Test
    @DisplayName("조건에 맞는 인스턴스의 개수가 pageSize보다 많다면, hasNext()가 true여야 한다.")
    public void should_hasNextIsTrue_when_instanceSizeOverThanPageSize() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Direction.DESC, "participantCount"));
        getSavedInstance("title1", "BE,AI", 20);
        getSavedInstance("title2", "BE,Spring", 10);
        getSavedInstance("title3", "FE,BE", 10);
        getSavedInstance("title4", "FE,React", 12);

        User user = User.builder().tags("BE").build();

        //when
        Slice<HomeInstanceResponse> recommendations = challengeRecommendationService.getRecommendations(user,
                pageRequest);

        //then
        assertThat(recommendations.getContent().size()).isEqualTo(2);
        assertThat(recommendations.getContent().get(0).title()).isEqualTo("title1");
        assertThat(recommendations.getContent().get(1).title()).isEqualTo("title2");
        assertThat(recommendations.hasNext()).isTrue();
    }

    @Test
    @DisplayName("조건에 맞는 인스턴스의 개수가 pageSize보다 적다면, hasNext()가 false여야 한다.")
    public void should_hasNextIsTrue_when_instanceSizeLessThanPageSize() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Direction.DESC, "participantCount"));
        getSavedInstance("title1", "BE,AI", 20);
        getSavedInstance("title2", "BE,Spring", 10);
        getSavedInstance("title3", "FE,BE", 10);
        getSavedInstance("title4", "FE,React", 12);

        User user = User.builder().tags("BE").build();

        //when
        Slice<HomeInstanceResponse> recommendations = challengeRecommendationService.getRecommendations(user,
                pageRequest);

        //then
        assertThat(recommendations.getContent().size()).isEqualTo(3);
        assertThat(recommendations.hasNext()).isFalse();
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
                        .startedDate(now)
                        .completedDate(now.plusDays(1))
                        .build()
        );
        instance.updateParticipantCount(participantCnt);
        instance.setTopic(getSavedTopic());
        return instance;
    }

    private Topic getSavedTopic() {
        return topicRepository.save(
                Topic.builder()
                        .title("title")
                        .description("description")
                        .tags("BE")
                        .pointPerPerson(100)
                        .build()
        );
    }
}