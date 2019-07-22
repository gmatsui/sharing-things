package com.gmats.st.group.http.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthRouteHandler extends BaseRouterHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthRouteHandler.class);

    private OAuth2Auth oauth2;

    public AuthRouteHandler(OAuth2Auth oauth2) {
        this.oauth2 = oauth2;
    }

    public void authenticated(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        String authorization = request.headers().get(HttpHeaders.AUTHORIZATION);
        log.info("Header {}", authorization);
        if (authorization == null) {
            routingContext.fail(HttpResponseStatus.UNAUTHORIZED.code());
        } else {
            String[] parts = authorization.split(" ");
            this.oauth2.introspectToken(parts[1], accessTokenAsyncResult -> {
                if (accessTokenAsyncResult.succeeded()) {
                    log.info("User {}", accessTokenAsyncResult.result().accessToken());
                    routingContext.setUser(accessTokenAsyncResult.result());
                    routingContext.next();
                } else {
                    log.warn("Error fetching introspecting token", accessTokenAsyncResult.cause());
                    routingContext.fail(accessTokenAsyncResult.cause());
                }
            });
        }
    }
}
