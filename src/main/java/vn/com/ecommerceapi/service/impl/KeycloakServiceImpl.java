package vn.com.ecommerceapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import vn.com.ecommerceapi.constant.Constant;
import vn.com.ecommerceapi.logging.LoggingFactory;
import vn.com.ecommerceapi.service.KeycloakService;
import vn.com.ecommerceapi.utils.StringUtils;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    private static final Logger LOGGER = LoggingFactory.getLogger(KeycloakServiceImpl.class);

    private final KeycloakSpringBootProperties keycloakSpringBootProperties;

    public Keycloak getKeycloakByClient() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakSpringBootProperties.getAuthServerUrl())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(keycloakSpringBootProperties.getRealm())
                .clientId(keycloakSpringBootProperties.getResource())
                .clientSecret(keycloakSpringBootProperties.getCredentials().get("secret").toString())
                .build();
    }

    public Keycloak getKeycloak(String username, String password) {
        return Keycloak.getInstance(
                keycloakSpringBootProperties.getAuthServerUrl(),
                keycloakSpringBootProperties.getRealm(),
                username,
                password,
                keycloakSpringBootProperties.getResource(),
                keycloakSpringBootProperties.getCredentials().get("secret").toString());
    }

    /* Cài lại password của user vào keycloak */
    public CredentialRepresentation credentialRepresentation(String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();

        /* Cài đặt password tạm thời = false để không phải đổi mật khẩu ở keycloak khi đăng nhập */
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);

        /* Nếu không có password sẽ để mặc định password */
        if (StringUtils.isNullOrEmpty(password)) {
            passwordCred.setValue(Constant.PASSWORD_DEFAULT);
        } else {
            passwordCred.setValue(password);
        }
        return passwordCred;
    }
}
