package com.bellamyphan.finora_2026_spring.postgres.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class DefaultAccountProperties {

    private AdminAccount admin = new AdminAccount();
    private UserAccount user = new UserAccount();

    @Getter
    @Setter
    public static class AdminAccount {
        private String email;
        private String password;
        private String name;
    }

    @Getter
    @Setter
    public static class UserAccount {
        private String email;
        private String password;
        private String name;
    }
}
