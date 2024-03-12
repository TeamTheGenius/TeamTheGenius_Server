package com.genius.gitget.challenge.likes.repository;

import com.genius.gitget.challenge.likes.domain.Likes;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    @Query("select l from Likes l where l.user.id = :userId and l.instance.id = :instanceId")
    Optional<Likes> findSpecificLike(@Param("userId") Long userId,
                                     @Param("instanceId") Long instanceId);
}
