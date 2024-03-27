package com.genius.gitget.payment;

import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.store.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback
public class PaymentRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PaymentRepository paymentRepository;

}
