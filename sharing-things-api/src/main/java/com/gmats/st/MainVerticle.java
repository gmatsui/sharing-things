package com.gmats.st;

import com.gmats.st.common.vertx.security.OAuth2KeycloakCreator;
import com.gmats.st.group.service.GroupDatabaseVerticle;
import com.gmats.st.group.http.HttpEndpointVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainVerticle extends AbstractVerticle {
  private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Future<Void> startFuture) {
    this.loadConfiguration().getConfig(aConfig -> {
      if (aConfig.succeeded()) {
        log.info("Configuration loaded");
        JsonObject config = aConfig.result();
        this.createDatabaseVerticle(config)
                .compose(databaseDeploymentId -> this.createGroupHttpVerticle(config)
        )
        .setHandler(ar -> {
          if (ar.succeeded()) {
            log.info("Application started");
            startFuture.complete();
          } else {
            log.error("Fail starting the application", ar.cause());
            startFuture.fail(ar.cause());
          }
        });
      } else {
        log.error("Error reading the config file");
        startFuture.fail(aConfig.cause());
      }
    });
  }


  private Future<String> createDatabaseVerticle(JsonObject config) {
    Future<String> dbGroupsVerticleDeployment = Future.future();
    vertx.deployVerticle(new GroupDatabaseVerticle(), new DeploymentOptions().setConfig(config), dbGroupsVerticleDeployment);
    return dbGroupsVerticleDeployment;
  }

  private Future<String> createGroupHttpVerticle(JsonObject config) {
    Future<String> groupVerticleDeployment = Future.future();
    this.vertx.deployVerticle(new HttpEndpointVerticle(new OAuth2KeycloakCreator(config)), new DeploymentOptions().setConfig(config), groupVerticleDeployment);
    return groupVerticleDeployment;
  }


  private ConfigRetriever loadConfiguration() {
    ConfigStoreOptions propertyFile = new ConfigStoreOptions()
            .setType("file")
            .setFormat("properties")
            .setConfig(new JsonObject().put("path", "config.properties"));

    ConfigStoreOptions environment = new ConfigStoreOptions()
            .setType("env");

    ConfigRetrieverOptions options = new ConfigRetrieverOptions()
            .addStore(propertyFile)
            .addStore(environment);


    return ConfigRetriever.create(this.vertx, options);
  }

}
