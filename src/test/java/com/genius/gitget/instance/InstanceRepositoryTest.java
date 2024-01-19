package com.genius.gitget.instance;

import com.genius.gitget.instance.domain.Instance;
import com.genius.gitget.instance.domain.Progress;
import com.genius.gitget.instance.repository.InstanceRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class InstanceRepositoryTest {

    @Autowired
    private InstanceRepository instanceRepository;

    @Test
    public void 인스턴스_저장() {
        Instance instance = new Instance("1일 1커밋", "챌린지 세부사항입니다." ,10, "BE, CS",
                100, Progress.ACTIVITY, LocalDateTime.now(), LocalDateTime.now().plusDays(3));

        Instance savedInstance = instanceRepository.save(instance);

        Assertions.assertThat(instance.getId()).isEqualTo(savedInstance.getId());
    }
}
