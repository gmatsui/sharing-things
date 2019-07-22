package com.gmats.st.group.service;

import com.gmats.st.group.service.GroupDatabaseServiceVertxEBProxy;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLClient;

@ProxyGen
@VertxGen
public interface GroupDatabaseService {
    @Fluent
    GroupDatabaseService fetchAll(Handler<AsyncResult<JsonArray>> resultHandler);


    @GenIgnore
    static GroupDatabaseService create(SQLClient dbClient, Handler<AsyncResult<GroupDatabaseService>> readyHandler) {
        return new GroupDatabaseServiceImpl(dbClient, readyHandler);
    }

    @GenIgnore
    static GroupDatabaseService createProxy(Vertx vertx, String address) {
        return new GroupDatabaseServiceVertxEBProxy(vertx, address);
    }
}
