package org.raflab.studsluzbadesktopclient.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.api")
public class ConfigProperties {
    private String baseUrl;
}
