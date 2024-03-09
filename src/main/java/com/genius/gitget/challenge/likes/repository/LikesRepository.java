package com.genius.gitget.challenge.likes.repository;

import com.genius.gitget.challenge.likes.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {
}
