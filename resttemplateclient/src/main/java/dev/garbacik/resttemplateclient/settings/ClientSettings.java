package dev.garbacik.resttemplateclient.settings;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "api.client")
public class ClientSettings {
    private String clientId;
    private String clientSecret;
    private String scope;
    private String tokenRequestUrl;

    public MultiValueMap<String, String> toClientCredentialReqMultiValueMap() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("grant_type", "client_credentials");
        map.add("client_id", this.clientId);
        map.add("client_secret", this.clientSecret);
        map.add("scope", this.scope);

        return map;
    }
}
