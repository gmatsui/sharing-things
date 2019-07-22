package com.gmats.st.group.http.handler;

import com.gmats.st.group.service.GroupDatabaseService;
import io.vertx.core.json.Json;
import io.vertx.ext.auth.oauth2.AccessToken;
import io.vertx.ext.auth.oauth2.impl.OAuth2UserImpl;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupRouteHandler extends BaseRouterHandler {

    private static final Logger log = LoggerFactory.getLogger(GroupRouteHandler.class);

    private GroupDatabaseService groupDatabaseService;


    public GroupRouteHandler(final GroupDatabaseService groupDatabaseService) {
        this.groupDatabaseService = groupDatabaseService;
    }

    public void getAll(RoutingContext routingContext) {
        log.info("Principal {}", ((AccessToken) routingContext.user()).accessToken().getString("email"));

        this.groupDatabaseService.fetchAll(jsonArrayAsyncResult -> {
            if (jsonArrayAsyncResult.succeeded()) {
                routingContext.response()
                        .putHeader("content-type", "application/json; charset=utf-8")
                        .end(Json.encodePrettily(jsonArrayAsyncResult.result().getList()));
            } else {
                log.error("Error sending the message to database Verticle", jsonArrayAsyncResult.cause());
                this.errorHandler(routingContext, jsonArrayAsyncResult.cause());
            }
        });
    }
}
