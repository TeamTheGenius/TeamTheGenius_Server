package com.genius.gitget.challenge.item.repository;

import com.genius.gitget.challenge.item.domain.Item;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where i.itemCategory = :category")
    List<Item> findAllByCategory(@Param("category") ItemCategory itemCategory);
}
