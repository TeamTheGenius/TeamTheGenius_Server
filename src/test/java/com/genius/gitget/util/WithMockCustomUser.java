package com.genius.gitget.util;

import com.genius.gitget.security.constants.ProviderInfo;
import com.genius.gitget.user.domain.Role;
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
}
