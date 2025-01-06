package com.genius.gitget.store.item.repository;

import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i where i.itemCategory = :category")
    List<Item> findAllByCategory(@Param("category") ItemCategory itemCategory);

    Optional<Item> findByIdentifier(@Param("identifier") int identifier);
}
