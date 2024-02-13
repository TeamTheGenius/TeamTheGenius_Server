package com.genius.gitget.challenge.instance.repository;

import com.genius.gitget.admin.topic.repository.TopicRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import java.time.LocalDateTime;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
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
@Slf4j
public class InstanceSearchRepositoryTest {
    @Autowired
    SearchRepository searchRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    InstanceRepository instanceRepository;

    @Autowired
    EntityManager em;


    @Test
    public void 인스턴스_검색() throws Exception {

    }

}
