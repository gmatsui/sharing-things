package com.gmats.st.group.http.handler;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class BaseRouterHandler {

    protected void errorHandler(RoutingContext routingContext, Throwable throwable) {
        this.errorHandler(routingContext, HttpResponseStatus.INTERNAL_SERVER_ERROR, throwable.getMessage());
    }


    protected void errorHandler(RoutingContext routingContext, HttpResponseStatus httpResponseStatus, String message) {
        JsonObject responseBody = new JsonObject();
        responseBody.put("message", message);
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .setStatusCode(httpResponseStatus.code())
                .end(responseBody.encodePrettily());
    }
}
