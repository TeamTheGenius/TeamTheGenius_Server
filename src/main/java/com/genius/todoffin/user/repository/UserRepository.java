package com.genius.todoffin.user.repository;

import com.genius.todoffin.security.constants.ProviderInfo;
import com.genius.todoffin.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdentifier(String identifier);

    @Query("select u from User u where u.identifier = :identifier and u.provider = :provider")
    Optional<User> findByOAuthInfo(@Param("identifier") String identifier,
                                   @Param("provider") ProviderInfo providerInfo);
}
