package com.genius.todoffin.security.domain;

import com.genius.todoffin.user.domain.User;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

@Getter
public class UserPrincipal extends DefaultOAuth2User {
    private User user;

    public UserPrincipal(User user, Map<String, Object> attributes, String nameAttributeKey) {
        super(Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getKey())),
                attributes,
                nameAttributeKey);
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getIdentifier();
    }
}
