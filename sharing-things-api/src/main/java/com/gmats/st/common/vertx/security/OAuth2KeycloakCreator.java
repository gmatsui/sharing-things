package com.gmats.st.common.vertx.security;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OAuth2KeycloakCreator implements OAuth2Creator {

    private static final Logger log = LoggerFactory.getLogger(OAuth2KeycloakCreator.class);

    private JsonObject config;

    public OAuth2KeycloakCreator(JsonObject config) {
        this.config = config;
    }

    @Override
    public OAuth2Auth createOAuth(Vertx vertx) {
        log.info("Creating OAuth, server-url {}", config.getString("oauth2.keycloak.auth-server-url"));
        Double confidentialPort = new Double(config.getString("oauth2.keycloak.confidential-port", "0"));
        JsonObject keycloakJson = new JsonObject()
                .put("realm", config.getString("oauth2.keycloak.realm"))
                .put("realm-public-key", config.getString("oauth2.keycloak.realm-public-key"))
                .put("auth-server-url", config.getString("oauth2.keycloak.auth-server-url"))
                .put("ssl-required", config.getString("oauth2.keycloak.ssl-required"))
                .put("resource", config.getString("oauth2.keycloak.resource"))
                .put("credentials", new JsonObject().put("secret",  config.getString("oauth2.keycloak.credentials.secret")))
                .put("confidential-port", confidentialPort);
        return KeycloakAuth.create(vertx, OAuth2FlowType.PASSWORD, keycloakJson);
    }

}
