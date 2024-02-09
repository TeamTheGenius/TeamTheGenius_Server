package com.genius.gitget.challenge.instance.repository;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SearchRepository extends JpaRepository<Instance, Long>, SearchRepositoryCustom {

    // 검색 키워드인 제목을 포함하는 경우
    Page<Instance> findByTitleContainingOrderByStartedDateDesc(String title, Pageable pageable);

    // 모집 진행 상황의 조건과 일치하는 검색 키워드인 제목을 포함하는 경우
    /*
    * select I.topic_id , I.instance_id , I.title , I.point_per_person , I.participant_count, I.files_id , I.progress,
    F.files_id , F.fileuri , F.original_filename , F.saved_filename , F.file_type
    from instance as I inner join files F
    on I.files_id = F.files_id
    WHERE I.progress = 'PREACTIVITY' and I.title LIKE '%2%'
    order by I.started_at;
    * */
    Page<Instance> findByProgressAndTitleContainingOrderByStartedDateDesc(Progress progress, String title, Pageable pageable);
}
