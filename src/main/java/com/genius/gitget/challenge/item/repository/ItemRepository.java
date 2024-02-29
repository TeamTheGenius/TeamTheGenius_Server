package com.genius.gitget.challenge.item.repository;

import com.genius.gitget.challenge.item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
