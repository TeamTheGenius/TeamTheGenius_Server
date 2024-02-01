package com.genius.gitget.challenge.home.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.admin.topic.domain.Topic;
import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.home.dto.HomeInstanceResponse;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.user.domain.User;
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
class HomeServiceTest {
    @Autowired
    HomeService homeService;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    InstanceRepository instanceRepository;

    @Test
    @DisplayName("사용자가 설정한 태그에 맞는 추천 인스턴스들을 페이징 형태로 받아올 수 있다.")
    public void should_getSuggestions_when_passUserTags() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Direction.DESC, "participantCnt"));
        getSavedInstance("title1", "BE", 20);
        getSavedInstance("title2", "BE", 10);
        getSavedInstance("title3", "FE", 10);
        getSavedInstance("title4", "FE", 12);

        User user = User.builder().tags("BE").build();

        //when
        Slice<HomeInstanceResponse> recommendations = homeService.getRecommendations(user, pageRequest);

        //then
        assertThat(recommendations.getContent().size()).isEqualTo(2);
        assertThat(recommendations.getContent().get(0).title()).isEqualTo("title1");
        assertThat(recommendations.getContent().get(0).participantCnt()).isEqualTo(20);
        assertThat(recommendations.getContent().get(0).pointPerPerson()).isEqualTo(100);

        assertThat(recommendations.getContent().get(1).title()).isEqualTo("title2");
        assertThat(recommendations.getContent().get(1).participantCnt()).isEqualTo(10);
        assertThat(recommendations.getContent().get(1).pointPerPerson()).isEqualTo(100);
    }

    private Instance getSavedInstance(String title, String tags, int participantCnt) {
        LocalDateTime now = LocalDateTime.now();
        Instance instance = instanceRepository.save(
                Instance.builder()
                        .tags(tags)
                        .title(title)
                        .description("description")
                        .progress(Progress.PRE_ACTIVITY)
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