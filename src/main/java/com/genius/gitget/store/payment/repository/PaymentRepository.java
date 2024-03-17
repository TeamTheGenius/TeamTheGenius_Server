package com.genius.gitget.store.payment.repository;

import com.genius.gitget.store.payment.domain.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderId(String orderId);

    @Query("select p from Payment p where p.user.id = :id order by p.createdDate desc")
    List<Payment> findPaymentDetailsByUserId(Long id);
}
