package com.gmats.st.group.service;

import com.gmats.st.group.entity.GroupQueueEnum;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLClient;

import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupDatabaseVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(GroupDatabaseVerticle.class);

    private SQLClient jdbcClient;


    @Override
    public void start(Future<Void> startFuture) {
        this.jdbcClient = this.getJDBCClient();
        String groupDatabaseQueue = config().getString(GroupQueueEnum.GROUP_DATABASE.getConfigKey(), GroupQueueEnum.GROUP_DATABASE.getDefaultValue());
        GroupDatabaseService.create(this.jdbcClient, ready -> {
            if (ready.succeeded()) {
                ServiceBinder binder = new ServiceBinder(this.vertx);
                binder.setAddress(groupDatabaseQueue).register(GroupDatabaseService.class, ready.result());
                startFuture.complete();
            } else {
                startFuture.fail(ready.cause());
            }
        });
    }



    private SQLClient getJDBCClient() {
        Double port = new Double(this.config().getString("datasource.port"));
        return PostgreSQLClient.createShared(this.vertx, new JsonObject()
                .put("host", this.config().getString("datasource.host"))
                .put("port", port)
                .put("database", this.config().getString("datasource.database"))
                .put("username", this.config().getString("datasource.user"))
                .put("password", this.config().getString("datasource.password"))
        );
    }

}
