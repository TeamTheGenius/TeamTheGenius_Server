package com.genius.gitget.challenge.hits.repository;

import com.genius.gitget.challenge.hits.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {
}
