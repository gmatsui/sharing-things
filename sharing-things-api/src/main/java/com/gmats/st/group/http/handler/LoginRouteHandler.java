package com.gmats.st.group.http.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class LoginRouteHandler extends BaseRouterHandler {
    private static final Logger log = LoggerFactory.getLogger(LoginRouteHandler.class);

    private OAuth2Auth oauth2;

    public LoginRouteHandler(OAuth2Auth oauth2) {
        this.oauth2 = oauth2;
    }

    public void login(RoutingContext routingContext) {
        log.info("Login info {}", routingContext.getBodyAsJson().getString("username"));
        try {
            this.oauth2.authenticate(routingContext.getBodyAsJson(), userAsyncResult -> {
                if (userAsyncResult.failed()) {
                    log.error("Access token error {}", userAsyncResult.cause().getMessage());
                    if (userAsyncResult.cause().getMessage() != null
                            && userAsyncResult.cause().getMessage().contains("invalid_grant")) {
                        this.errorHandler(routingContext, HttpResponseStatus.UNAUTHORIZED, "Invalid access");
                    } else {
                        this.errorHandler(routingContext, userAsyncResult.cause());
                    }
                } else {
                    User user = userAsyncResult.result();
                    routingContext.response().end(user.principal().encodePrettily());
                }
            });
        } catch (Exception ex) {
            log.error("Error getting the authentication", ex);
            this.errorHandler(routingContext, ex);
        }

    }
}
