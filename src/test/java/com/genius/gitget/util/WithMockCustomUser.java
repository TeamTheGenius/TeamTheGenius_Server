package com.genius.gitget.util;

import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.global.security.constants.ProviderInfo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(value = RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    ProviderInfo providerInfo() default ProviderInfo.GITHUB;

    String identifier() default "identifier";

    String nickname() default "nickname";

    String interest() default "BE,FE";

    String information() default "information";

    Role role() default Role.USER;

    String profileName() default "profile";
}
