package dev.garbacik.webclientclient.configuration;

import java.time.Instant;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.UriSpec;

import dev.garbacik.common.model.OAuthResponse;
import dev.garbacik.common.settings.ClientSettings;
import reactor.core.publisher.Mono;

@Configuration
@EnableConfigurationProperties(ClientSettings.class)
public class WebConfig {

    private static OAuthResponse oAuthResponse;

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    private ClientSettings clientSettings;

    @Value("${host.url}")
    private String hostUrl;

    @Bean(name = "apiWebClient")
    WebClient apiWebClient() {
        WebClient client = WebClient.builder()
                .baseUrl(hostUrl)
                .filter(oAuthTokenFilter)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", hostUrl))
                .build();

        return client;
    }

    @Bean(name = "oAuthWebClient")
    WebClient oAuthWebClient() {

        WebClient client = WebClient.builder()
                .baseUrl(this.clientSettings.getTokenRequestUrl())
                .defaultUriVariables(Collections.singletonMap("url", this.clientSettings.getTokenRequestUrl()))
                .build();

        return client;
    }

    private ExchangeFilterFunction oAuthTokenFilter = (request, nextFilter) -> {

        checkOAuthResponse();

        ClientRequest newRequest = ClientRequest.from(request)
                .header("Authorization",
                        oAuthResponse.getTokenType() + " " + oAuthResponse.getAccessToken())
                .build();

        return nextFilter.exchange(newRequest);
    };

    private void checkOAuthResponse() {
        final Instant expirationDate = oAuthResponse != null && oAuthResponse.getExpiresOn() != null
                ? Instant.ofEpochMilli(oAuthResponse.getExpiresOn() * 1000)
                : null;

        if (oAuthResponse != null
                && oAuthResponse.getAccessToken() != null
                && expirationDate != null
                && expirationDate.isAfter(Instant.now())) {
            return;
        }

        UriSpec<RequestBodySpec> uriSpec = oAuthWebClient().method(HttpMethod.POST);

        RequestBodySpec bodySpec = uriSpec.uri(
                uriBuilder -> uriBuilder
                        .pathSegment("/oauth2/v2.0/token")
                        .build());

        RequestHeadersSpec<?> headersSpec = bodySpec.body(
                BodyInserters.fromMultipartData(
                        this.clientSettings.toClientCredentialReqMultiValueMap()));

        Mono<OAuthResponse> response = headersSpec.retrieve()
                .bodyToMono(OAuthResponse.class);

        response
                .doOnError(err -> {
                    throw new InternalError("Unable to get authorization token");
                })
                .doOnNext(result -> oAuthResponse = result)
                .block();
    }
}
