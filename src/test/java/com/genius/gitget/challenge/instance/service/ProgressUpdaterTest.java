package com.genius.gitget.challenge.instance.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
class ProgressUpdaterTest {
    @Autowired
    private ProgressUpdater progressUpdater;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private InstanceProvider instanceProvider;

    @Test
    @DisplayName("PRE_ACTIVITY 인스턴스들 중, 특정 조건에 해당하는 인스턴스들을 ACTIVITY로 상태를 바꿀 수 있다.")
    public void should_updateToActivity_when_conditionMatches() {
        //given
        LocalDate startedDate = LocalDate.of(2024, 3, 1);
        LocalDate completedDate = LocalDate.of(2024, 3, 30);
        LocalDate currentDate = LocalDate.of(2024, 3, 6);

        getSavedInstance(startedDate, completedDate);
        getSavedInstance(startedDate, completedDate);
        getSavedInstance(startedDate, completedDate);

        //when
        List<Instance> preActivities = instanceProvider.findAllByProgress(Progress.PREACTIVITY);
        progressUpdater.updateToActivity(currentDate);
        List<Instance> activities = instanceProvider.findAllByProgress(Progress.ACTIVITY);

        //then
        assertThat(preActivities.size()).isEqualTo(3);
        assertThat(activities.size()).isEqualTo(3);
    }

    private Instance getSavedInstance(LocalDate startedDate, LocalDate completedDate) {
        return instanceRepository.save(
                Instance.builder()
                        .title("title")
                        .progress(Progress.PREACTIVITY)
                        .pointPerPerson(100)
                        .startedDate(startedDate.atTime(0, 0))
                        .completedDate(completedDate.atTime(0, 0))
                        .build()
        );
    }
}