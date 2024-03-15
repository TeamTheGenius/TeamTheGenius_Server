package com.genius.gitget.store.item.repository;

import com.genius.gitget.store.item.domain.EquipStatus;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query("select u from Orders u where u.user.id = :userId and u.item.itemCategory = :itemCategory")
    List<Orders> findByCategory(@Param("userId") Long userId,
                                @Param("itemCategory") ItemCategory itemCategory);

    @Query("select u from Orders u where u.user.id = :userId and u.item.id = :itemId")
    Optional<Orders> findByOrderInfo(@Param("userId") Long userId,
                                     @Param("itemId") Long itemId);

    @Query("select u from Orders u where u.user.id = :userId and u.item.itemCategory = :category and u.equipStatus = :equipStatus")
    Optional<Orders> findByEquipStatus(@Param("userId") Long userId,
                                       @Param("category") ItemCategory category,
                                       @Param("equipStatus") EquipStatus equipStatus);
}
