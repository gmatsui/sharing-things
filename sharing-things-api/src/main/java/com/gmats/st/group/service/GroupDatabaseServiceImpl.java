package com.gmats.st.group.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GroupDatabaseServiceImpl implements GroupDatabaseService {

    private static final Logger log = LoggerFactory.getLogger(GroupDatabaseServiceImpl.class);

    private final SQLClient jdbcClient;

    public GroupDatabaseServiceImpl(SQLClient jdbcClient, Handler<AsyncResult<GroupDatabaseService>> readyHandler) {
        log.info("Creating GroupDatabaseServiceImpl");
        this.jdbcClient = jdbcClient;
        readyHandler.handle(Future.succeededFuture(this));
    }

    @Override
    public GroupDatabaseService fetchAll(Handler<AsyncResult<JsonArray>> resultHandler) {
        this.jdbcClient.getConnection(sqlConnectionAsyncResult -> {
            if(sqlConnectionAsyncResult.succeeded()) {
                SQLConnection connection = sqlConnectionAsyncResult.result();
                connection.query("SELECT * FROM collaboration_group", fetch -> {
                    connection.close();
                    if (fetch.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(new JsonArray(fetch.result().getRows())));
                    } else {
                        resultHandler.handle(Future.failedFuture(fetch.cause()));
                    }
                });
            } else {
                resultHandler.handle(Future.failedFuture(sqlConnectionAsyncResult.cause()));
            }
        });
        return this;
    }

}
