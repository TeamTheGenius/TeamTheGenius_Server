package com.genius.gitget.challenge.item.repository;

import com.genius.gitget.challenge.item.domain.UserItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {
}
