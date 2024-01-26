package com.genius.gitget.challenge.user.repository;

import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.challenge.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdentifier(String identifier);

    Optional<User> findByNickname(String nickname);

    @Query("select u from User u where u.identifier = :identifier and u.providerInfo = :providerInfo")
    Optional<User> findByOAuthInfo(@Param("identifier") String identifier,
                                   @Param("providerInfo") ProviderInfo providerInfo);

}
