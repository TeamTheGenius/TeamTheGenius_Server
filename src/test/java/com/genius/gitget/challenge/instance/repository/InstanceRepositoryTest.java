package com.genius.gitget.challenge.instance.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class InstanceRepositoryTest {
    @Autowired
    InstanceRepository instanceRepository;

    @Test
    @DisplayName("인스턴스들 중, 사용자의 tag가 포함되어 있는 인스턴스들을 반환받을 수 있다.")
    public void should_return() {
        //given
        List<String> userTags = List.of("BE", "FE", "AI");
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Direction.DESC, "participantCnt"));

        //when
        getSavedInstance("title1", "BE", 10);
        getSavedInstance("title2", "FE", 3);
        getSavedInstance("title3", "FE", 20);
        Slice<Instance> suggestions = instanceRepository.findRecommendations(userTags, Progress.ACTIVITY, pageRequest);

        //then
        assertThat(suggestions.getContent().size()).isEqualTo(3);
        assertThat(suggestions.getContent().get(0).getTitle()).isEqualTo("title3");
        assertThat(suggestions.getContent().get(0).getTags()).isEqualTo("FE");
        assertThat(suggestions.getContent().get(0).getParticipantCnt()).isEqualTo(20);

        assertThat(suggestions.getContent().get(1).getTitle()).isEqualTo("title1");
        assertThat(suggestions.getContent().get(1).getTags()).isEqualTo("BE");
        assertThat(suggestions.getContent().get(1).getParticipantCnt()).isEqualTo(10);

        assertThat(suggestions.getContent().get(2).getTitle()).isEqualTo("title2");
        assertThat(suggestions.getContent().get(2).getTags()).isEqualTo("FE");
        assertThat(suggestions.getContent().get(2).getParticipantCnt()).isEqualTo(3);
    }

    private Instance getSavedInstance(String title, String tags, int participantCnt) {
        LocalDateTime now = LocalDateTime.now();
        Instance instance = instanceRepository.save(
                Instance.builder()
                        .tags(tags)
                        .title(title)
                        .description("description")
                        .progress(Progress.ACTIVITY)
                        .pointPerPerson(100)
                        .startedDate(now)
                        .completedDate(now.plusDays(1))
                        .build()
        );
        instance.updateParticipantCnt(participantCnt);
        return instance;
    }
}