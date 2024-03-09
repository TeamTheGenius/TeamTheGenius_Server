package com.genius.gitget;

import com.genius.gitget.challenge.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;


@SpringBootTest
@Sql({"/data.sql"})
class GitgetApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void test() {
    }

}
