package vn.com.ecommerceapi.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("expiresIn")
    private long expiresIn;

    @JsonProperty("refreshExpiresIn")
    private long refreshExpiresIn;

    @JsonProperty("refreshToken")
    private String refreshToken;

    @JsonProperty("tokenType")
    private String tokenType;

    @JsonProperty("idToken")
    private String idToken;

    @JsonProperty("notBeforePolicy")
    private int notBeforePolicy;

    @JsonProperty("sessionState")
    private String sessionState;

    private Map<String, Object> otherClaims = new HashMap<>();

    @JsonProperty("scope")
    private String scope;

    @JsonProperty("error")
    private String error;

    @JsonProperty("errorDescription")
    private String errorDescription;

    @JsonProperty("errorUri")
    private String errorUri;

}
