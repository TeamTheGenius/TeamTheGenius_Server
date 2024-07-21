package com.genius.gitget.challenge.instance.util;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.topic.domain.Topic;
import java.time.LocalDateTime;
import java.util.List;

public class TestSetup {
    public static List<Topic> createTopicList() {
        Topic topicA = Topic.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE")
                .pointPerPerson(100)
                .build();

        Topic topicB = Topic.builder()
                .title("이펙티브 자바")
                .description("1주일에 2개 item씩 공부합니다.")
                .tags("BE")
                .pointPerPerson(500)
                .build();

        Topic topicC = Topic.builder()
                .title("1일 1면접 준비")
                .description("1일에 면접 주제 1개씩 공부합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(700)
                .build();

        return List.of(topicA, topicB, topicC);
    }

    public static List<Instance> createInstanceList() {
        Instance instanceA = Instance.builder()
                .title("1일 1알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE")
                .pointPerPerson(100)
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(30))
                .build();

        Instance instanceB = Instance.builder()
                .title("1일 3알고리즘")
                .description("하루에 한 문제씩 문제를 해결합니다.")
                .tags("BE, FE")
                .pointPerPerson(300)
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(30))
                .build();

        Instance instanceC = Instance.builder()
                .title("이펙티브 자바")
                .description("1주일에 2개 item씩 공부합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(500)
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(30))
                .build();

        Instance instanceD = Instance.builder()
                .title("이펙티브 자바")
                .description("1주일에 1개 item씩 공부합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(400)
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(30))
                .build();

        Instance instanceE = Instance.builder()
                .title("1일 1면접 준비")
                .description("1일에 면접 주제 1개씩 공부합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(700)
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(30))
                .build();

        Instance instanceF = Instance.builder()
                .title("3일 1면접 준비")
                .description("1일에 면접 주제 3개씩 공부합니다.")
                .tags("BE, FE, CS")
                .pointPerPerson(1000)
                .progress(Progress.PREACTIVITY)
                .startedDate(LocalDateTime.now())
                .completedDate(LocalDateTime.now().plusDays(30))
                .build();

        return List.of(instanceA, instanceB, instanceC, instanceD, instanceE, instanceF);
    }
}
