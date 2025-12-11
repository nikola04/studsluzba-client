package org.raflab.studsluzbadesktopclient;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ClientAppConfig {
	@Bean
    public RestTemplate getRestTemplate() {
       return new RestTemplate();
    }
    
	@Bean
    public String getBaseUrl() {
       return "http://localhost:8080";
    }
}
