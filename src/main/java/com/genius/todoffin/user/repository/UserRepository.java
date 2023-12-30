package com.genius.todoffin.user.repository;

import com.genius.todoffin.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("select u from User u where u.email = :email and u.provider = :provider")
    Optional<User> findByOAuthInfo(@Param("email") String email, @Param("provider") String provider);
}
