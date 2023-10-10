package dev.garbacik.resttemplateclient.security;

import java.io.IOException;
import java.time.Instant;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import dev.garbacik.resttemplateclient.model.OAuthResponse;
import dev.garbacik.resttemplateclient.settings.ClientSettings;

public class RestTemplateOAuthInterceptor implements ClientHttpRequestInterceptor {

    private static OAuthResponse oAuthResponse;

    private ClientSettings clientSettings;
    private RestTemplate oAuthRestTemplate;

    public RestTemplateOAuthInterceptor(RestTemplate oAuthRestTemplate, ClientSettings clientSettings) {
        this.oAuthRestTemplate = oAuthRestTemplate;
        this.clientSettings = clientSettings;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        checkOAuthRespone();

        request.getHeaders().addIfAbsent("Authorization", "Bearer " + oAuthResponse.getAccessToken());
        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }

    private void checkOAuthRespone() {

        final Instant expirationDate = oAuthResponse != null && oAuthResponse.getExpiresOn() != null
                ? Instant.ofEpochMilli(oAuthResponse.getExpiresOn() * 1000)
                : null;

        if (oAuthResponse != null
                && oAuthResponse.getAccessToken() != null
                && expirationDate != null
                && expirationDate.isAfter(Instant.now())) {
            return;
        }

        final ResponseEntity<OAuthResponse> response = this.oAuthRestTemplate.exchange(
                "/oauth2/v2.0/token",
                HttpMethod.POST,
                createHttpEntity(),
                OAuthResponse.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            oAuthResponse = response.getBody();
        } else {
            throw new InternalError("Unable to get authorization token");
        }
    }

    private HttpEntity<MultiValueMap<String, String>> createHttpEntity() {

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(
                this.clientSettings.toClientCredentialReqMultiValueMap(), headers);

        return request;
    }
}
