package com.gmats.st.group.http;

import com.gmats.st.common.vertx.security.OAuth2Creator;
import com.gmats.st.group.entity.GroupQueueEnum;
import com.gmats.st.group.http.handler.AuthRouteHandler;
import com.gmats.st.group.http.handler.GroupRouteHandler;
import com.gmats.st.group.http.handler.LoginRouteHandler;
import com.gmats.st.group.service.GroupDatabaseService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.web.Router;

import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;


public class HttpEndpointVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(HttpEndpointVerticle.class);

    private GroupDatabaseService groupDatabaseService;

    private OAuth2Creator oAuth2Creator;

    public HttpEndpointVerticle(OAuth2Creator oAuth2Creator) {
        this.oAuth2Creator = oAuth2Creator;
    }

    @Override
    public void start(Future future) {
        HttpServer server = this.vertx.createHttpServer();
        String groupDatabaseQueue = config().getString(GroupQueueEnum.GROUP_DATABASE.getConfigKey(), GroupQueueEnum.GROUP_DATABASE.getDefaultValue());
        this.groupDatabaseService = GroupDatabaseService.createProxy(vertx, groupDatabaseQueue);
        OAuth2Auth oauth2 = this.oAuth2Creator.createOAuth(this.vertx);

        Router router = Router.router(this.vertx);
        router.route().handler(this.createCommonCorsHeaders());
        router.route().handler(BodyHandler.create());

        GroupRouteHandler groupRouteHandler = new GroupRouteHandler(groupDatabaseService);
        LoginRouteHandler loginRouteHandler = new LoginRouteHandler(oauth2);
        AuthRouteHandler authRouteHandler = new AuthRouteHandler(oauth2);

        router.post("/login").produces("application/json").handler(loginRouteHandler::login);
        router.route("/api/*").handler(authRouteHandler::authenticated);
        router.get("/api/groups").handler(groupRouteHandler::getAll);


        int port = config().getInteger("http.port", 8090);
        server.requestHandler(router).listen(port, httpServerAsyncResult -> {
            if (httpServerAsyncResult.succeeded()) {
                log.debug("Server starting in port {}", port);
                future.complete();
            } else {
                future.fail(httpServerAsyncResult.cause());
            }
        });
    }

    private  CorsHandler createCommonCorsHeaders() {
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("x-requested-with");
        allowedHeaders.add("Access-Control-Allow-Origin");
        allowedHeaders.add("origin");
        allowedHeaders.add("Content-Type");
        allowedHeaders.add("accept");
        allowedHeaders.add("Authorization");

        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        return CorsHandler.create("*").allowedHeaders(allowedHeaders).allowedMethods(allowedMethods);
    }



}

