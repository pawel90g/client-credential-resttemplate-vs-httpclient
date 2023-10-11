package dev.garbacik.resttemplateclient.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import dev.garbacik.common.settings.ClientSettings;
import dev.garbacik.resttemplateclient.security.RestTemplateOAuthInterceptor;

@Configuration
@EnableConfigurationProperties(ClientSettings.class)
public class HttpConfig {

    @Autowired
    private ClientSettings clientSettings;

    @Value("${host.url}")
    private String hostUrl;

    @Bean(name = "apiRestTemplate")
    RestTemplate apiRestTemplate() {
        RestTemplate restTemplate = new RestTemplateBuilder()
                .rootUri(this.hostUrl)
                .interceptors(new RestTemplateOAuthInterceptor(
                        this.oAuthAuthenticator(),
                        this.clientSettings))
                .build();

        return restTemplate;
    }

    private RestTemplate oAuthAuthenticator() {
        RestTemplate oAuthRestTemplate = new RestTemplateBuilder()
                .rootUri(this.clientSettings.getTokenRequestUrl())
                .build();

        return oAuthRestTemplate;
    }
}
