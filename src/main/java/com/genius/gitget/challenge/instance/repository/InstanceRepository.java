package com.genius.gitget.challenge.instance.repository;

import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.likes.dto.LikesDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstanceRepository extends JpaRepository<Instance, Long> {

    @Query("select i from Instance i ORDER BY i.id DESC ")
    Page<Instance> findAllById(Pageable pageable);

    @Query("select i from Instance i where i.topic.id = :topicId")
    Page<Instance> findInstancesByTopicId(Pageable pageable, Long topicId);

    @Query("select i from Instance i where i.progress = :progress and i.tags in :userTags")
    Slice<Instance> findRecommendations(@Param("userTags") List<String> userTags, Progress progress, Pageable pageable);

    @Query("select i from Instance i where i.progress = :progress")
    Slice<Instance> findInstanceByCondition(@Param("progress") Progress progress, Pageable pageable);

    @Query("select i.id as instanceId , i.title as title, i.pointPerPerson as pointPerPerson, i.files as files "
            + "from Instance i where i.id in :instanceId")
    Page<LikesDTO> findLikes(@Param("instanceId") List<Long> instanceId, Pageable pageable);
}
