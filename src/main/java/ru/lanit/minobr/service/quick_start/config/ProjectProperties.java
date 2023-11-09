package ru.lanit.minobr.service.quick_start.config;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ProjectProperties implements EnvironmentAware {

    private String serverUrl;
    private String realmName;
    private String clientId;

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.serverUrl = environment.getProperty("keycloak.server.url");
        this.realmName = environment.getProperty("keycloak.realm");
        this.clientId = environment.getProperty("keycloak.client");
    }


}
