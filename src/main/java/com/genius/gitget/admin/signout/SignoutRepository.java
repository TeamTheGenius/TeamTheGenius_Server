package com.genius.gitget.admin.signout;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SignoutRepository extends JpaRepository<Signout, Long> {

    @Query("select s from Signout s where s.identifier = :identifier")
    Signout findByIdentifier(String identifier);
}
