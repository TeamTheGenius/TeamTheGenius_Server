package com.genius.todoffin.security.domain;

import com.genius.todoffin.user.entity.Role;
import com.genius.todoffin.user.entity.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
@AllArgsConstructor
public class UserPrincipal implements OAuth2User {
    private User user;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserPrincipal(User user, Map<String, Object> attributes) {
        this.user = user;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority(Role.USER.getKey()));
        this.attributes = attributes;
    }


    @Override
    public String getName() {
        return user.getEmail();
    }
}
