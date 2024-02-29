package com.genius.gitget.challenge.item.repository;

import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

    @Query("select u from UserItem u where u.user = :userId and u.item.itemCategory = :itemCategory")
    Optional<UserItem> findUserItemByUser(@Param("userId") Long userId,
                                          @Param("itemCategory") ItemCategory itemCategory);
}
