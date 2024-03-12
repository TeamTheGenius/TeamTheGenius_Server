package com.genius.gitget.challenge.item.repository;

import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.domain.UserItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserItemRepository extends JpaRepository<UserItem, Long> {

    // 여기 자체에서 오류가 발생할 수 있을 듯.
    // List를 반환하게 해야 할듯
    @Query("select u from UserItem u where u.user.id = :userId and u.item.itemCategory = :itemCategory")
    Optional<UserItem> findByCategory(@Param("userId") Long userId,
                                      @Param("itemCategory") ItemCategory itemCategory);

    //TODO: 이름 적절하게 바꾸기
    @Query("select u from UserItem u where u.user.id = :userId and u.item.id = :itemId")
    Optional<UserItem> findByUserId(@Param("userId") Long userId,
                                    @Param("itemId") Long itemId);

    @Query("select u from UserItem u where u.user.id = :userId and u.item.id = :itemId")
    List<UserItem> findAll(@Param("userId") Long userId,
                           @Param("itemId") Long itemId);
}
