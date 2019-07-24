package com.gmats.st.group.http;

import com.gmats.st.common.vertx.security.OAuth2Creator;
import com.gmats.st.group.entity.Group;
import com.gmats.st.group.entity.GroupQueueEnum;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.AccessToken;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.impl.OAuth2UserImpl;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(VertxUnitRunner.class)
public class HttpEndpointVerticleTest {

    private Vertx vertx;

    private int serverPort;

    @Mock
    private OAuth2Creator oAuth2CreatorMock;

    @Mock
    private OAuth2Auth oAuth2AuthMock;

    @Mock
    //TODO check the interface
    private OAuth2UserImpl accessTokenMock;

    @Before
    public void setUp(TestContext testContext) throws IOException {

        this.vertx = Vertx.vertx();
        this.serverPort = this.getRandomPort();
        MockitoAnnotations.initMocks(this);
        when(oAuth2CreatorMock.createOAuth(any(Vertx.class))).thenReturn(this.oAuth2AuthMock);
        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", Integer.toString(this.serverPort)));
        this.vertx.deployVerticle(new HttpEndpointVerticle(this.oAuth2CreatorMock), options, testContext.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext testContext) {
        this.vertx.close(testContext.asyncAssertSuccess());
    }

    @Test
    public void test_getAllGroups_returnListOfGroups(TestContext testContext) {
        Async async = testContext.async();

        //Authorization
        when(accessTokenMock.accessToken()).thenReturn(new JsonObject().put("email", "test@test.com"));
        this.mockTokenIntropector(this.accessTokenMock, null);

        Group expectedGroup = new Group();
        expectedGroup.setName("Group test");
        List<JsonObject> groupList = Arrays.asList(JsonObject.mapFrom(expectedGroup));

        //Event mock
        this.vertx.eventBus().consumer(GroupQueueEnum.GROUP_DATABASE.getDefaultValue(), message -> {
            message.reply(new JsonArray(groupList));
        });

        String expectedToken = "SOME_TOKEN";

        //Executions
        WebClient client = WebClient.create(vertx);
        client.get(this.serverPort, "localhost", "/api/groups")
              .putHeader("Authorization", "Bearer " + expectedToken)
              .send(httpResponseAsyncResult -> {
                  try {
                      if (httpResponseAsyncResult.succeeded()) {
                          HttpResponse<Buffer> response = httpResponseAsyncResult.result();
                          //Assertions
                          ArgumentCaptor<String> tokenArgumentCapture = ArgumentCaptor.forClass(String.class);
                          testContext.verify((Void aVoid) -> verify(oAuth2AuthMock).introspectToken(tokenArgumentCapture.capture(), any()));
                          testContext.assertEquals(expectedToken, tokenArgumentCapture.getValue());
                          testContext.assertNotNull(response.bodyAsString());
                          JsonArray result = new JsonArray(response.bodyAsString());
                          testContext.assertNotNull(result);
                          testContext.assertEquals(groupList.size(), result.size());
                          JsonObject resultGroup = result.getJsonObject(0);
                          testContext.assertEquals(expectedGroup.getName(), resultGroup.getString("name"));
                          async.complete();
                      } else {
                          testContext.fail("Error in  client call");
                      }
                  } catch (Exception ex) {
                      testContext.fail(ex);
                  } finally {
                      client.close();
                  }
              });
    }

    @Test
    public void test_getAllGroup_withoutToken_return_status_UNAUTHORIZED(TestContext testContext) {
        Async async = testContext.async();

        //Executions
        WebClient client = WebClient.create(vertx);
        client.get(this.serverPort, "localhost", "/api/groups")
                .send(httpResponseAsyncResult -> {
                    try {
                        if (httpResponseAsyncResult.succeeded()) {
                            testContext.assertEquals(HttpResponseStatus.UNAUTHORIZED.code(), httpResponseAsyncResult.result().statusCode());
                            async.complete();
                        } else {
                            testContext.fail("Error in  client call");
                        }
                    } finally {
                        client.close();
                    }
                });
    }

    @Test
    public void test_getAllGroup_withProblemIntrospectingToken_status_INTERNAL_SERVER_ERROR(TestContext testContext) {
        Async async = testContext.async();

        //Authorization
        this.mockTokenIntropector(null, new RuntimeException("Generic error"));

        Group expectedGroup = new Group();
        expectedGroup.setName("Group test");
        List<JsonObject> groupList = Arrays.asList(JsonObject.mapFrom(expectedGroup));

        //Event mock
        this.vertx.eventBus().consumer(GroupQueueEnum.GROUP_DATABASE.getDefaultValue(), message -> {
            message.reply(new JsonArray(groupList));
        });

        String expectedToken = "SOME_TOKEN";

        //Executions
        WebClient client = WebClient.create(vertx);
        client.get(this.serverPort, "localhost", "/api/groups")
                .putHeader("Authorization", "Bearer " + expectedToken)
                .send(httpResponseAsyncResult -> {
                    try {
                        if (httpResponseAsyncResult.succeeded()) {
                            testContext.assertEquals(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), httpResponseAsyncResult.result().statusCode());
                            async.complete();
                        } else {
                            testContext.fail("Error in  client call");
                        }
                    } finally {
                        client.close();
                    }
                });
    }

    private void mockTokenIntropector(AccessToken accessToken, Throwable throwable) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Handler<AsyncResult<AccessToken>> callback = (Handler<AsyncResult<AccessToken>>) invocationOnMock.getArguments()[1];
                AsyncResult<AccessToken> result = new AsyncResult<AccessToken>() {

                    @Override
                    public AccessToken result() {
                        return accessToken;
                    }

                    @Override
                    public Throwable cause() {
                        return throwable;
                    }

                    @Override
                    public boolean succeeded() {
                        return (throwable == null);
                    }

                    @Override
                    public boolean failed() {
                        return false;
                    }
                };
                callback.handle(result);
                return null;
            }
        }).when(this.oAuth2AuthMock).introspectToken(anyString(), any());
    }



    private int getRandomPort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();
        socket.close();
        return port;
    }



}
