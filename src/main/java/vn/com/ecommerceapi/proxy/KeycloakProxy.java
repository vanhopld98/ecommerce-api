package vn.com.ecommerceapi.proxy;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import vn.com.ecommerceapi.logging.LoggingFactory;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeycloakProxy extends BaseProxy {

    private static final Logger LOGGER = LoggingFactory.getLogger(KeycloakProxy.class);

    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String SECRET = "secret";

    private final KeycloakSpringBootProperties keycloakSpringBootProperties;

    public AccessTokenResponse refreshToken(String refreshToken) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(keycloakSpringBootProperties.getAuthServerUrl())
                    .path("/realms/")
                    .path(keycloakSpringBootProperties.getRealm())
                    .path("/protocol/openid-connect/token")
                    .build().toString();
            LOGGER.info("[PROXY][KEYCLOAK][REFRESH TOKEN] Url: {}", url);

            Map<String, Object> payload = buildPayloadRefreshToken(refreshToken);
            LOGGER.info("[PROXY][KEYCLOAK][REFRESH TOKEN] Payload: {}", payload);

            AccessTokenResponse response = this.post(url, integer -> new HttpHeaders(), payload, AccessTokenResponse.class);
            LOGGER.info("[PROXY][KEYCLOAK][REFRESH TOKEN] Response: {}", response);

            return response;
        } catch (Exception e) {
            LOGGER.error("[PROXY][KEYCLOAK][REFRESH TOKEN] Exception: {}", e.getMessage());
            return null;
        }
    }

    private Map<String, Object> buildPayloadRefreshToken(String refreshToken) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(CLIENT_ID, keycloakSpringBootProperties.getResource());
        payload.put(CLIENT_SECRET, keycloakSpringBootProperties.getCredentials().get(SECRET).toString());
        payload.put(REFRESH_TOKEN, refreshToken);
        payload.put(GRANT_TYPE, REFRESH_TOKEN);
        return payload;
    }
}
