package com.genius.gitget.challenge.item.repository;

import com.genius.gitget.challenge.item.domain.EquipStatus;
import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

    @Query("select u from UserItem u where u.user.id = :userId and u.item.itemCategory = :itemCategory")
    List<UserItem> findByCategory(@Param("userId") Long userId,
                                  @Param("itemCategory") ItemCategory itemCategory);

    @Query("select u from UserItem u where u.user.id = :userId and u.item.id = :itemId")
    Optional<UserItem> findByOrderInfo(@Param("userId") Long userId,
                                       @Param("itemId") Long itemId);

    @Query("select u from UserItem u where u.user.id = :userId and u.item.itemCategory = :category and u.equipStatus = :equipStatus")
    Optional<UserItem> findByEquipStatus(@Param("userId") Long userId,
                                         @Param("category") ItemCategory category,
                                         @Param("equipStatus") EquipStatus equipStatus);
}
