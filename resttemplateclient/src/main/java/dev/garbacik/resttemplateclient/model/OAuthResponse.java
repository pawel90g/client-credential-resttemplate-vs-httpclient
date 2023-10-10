package dev.garbacik.resttemplateclient.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("not_before")
    private Long notBefore;
    @JsonProperty("expires_in")
    private Integer expiresIn;
    @JsonProperty("expires_on")
    private Long expiresOn;
    @JsonProperty("resource")
    private UUID resource;
}
