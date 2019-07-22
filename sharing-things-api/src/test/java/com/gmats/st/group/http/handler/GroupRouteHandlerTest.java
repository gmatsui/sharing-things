package com.gmats.st.group.http.handler;

import com.gmats.st.group.entity.Group;
import com.gmats.st.group.service.GroupDatabaseService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.oauth2.impl.OAuth2UserImpl;
import io.vertx.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class GroupRouteHandlerTest {

    @Mock
    private GroupDatabaseService groupDatabaseService;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void test_groupRouteHandler_getAll_response_list() {
        GroupRouteHandler instance = new GroupRouteHandler(this.groupDatabaseService);
        RoutingContext routingContextMock = mock(RoutingContext.class);
        //Mock auth
        this.mockOAuthContext(routingContextMock);

        //Mock database service
        JsonArray databaseServiceResult = new JsonArray();
        Group group = new Group();
        group.setName("Test name");
        databaseServiceResult.add(JsonObject.mapFrom(group));
        this.mockGroupDatabaseServiceFetchAll(databaseServiceResult, null);

        //Mock response chain
        HttpServerResponse httpServerResponseMock = mock(HttpServerResponse.class);
        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.putHeader(anyString(), anyString())).thenReturn(httpServerResponseMock);

        //Execution
        instance.getAll(routingContextMock);

        //Asserts
        verify(routingContextMock, atLeast(0)).response();

        ArgumentCaptor<String> headerKeyCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValueCapture = ArgumentCaptor.forClass(String.class);
        verify(httpServerResponseMock).putHeader(headerKeyCapture.capture(), headerValueCapture.capture());
        assertEquals("content-type", headerKeyCapture.getValue());
        assertEquals("application/json; charset=utf-8", headerValueCapture.getValue());

        ArgumentCaptor<String> jsonCapture = ArgumentCaptor.forClass(String.class);
        verify(httpServerResponseMock).end(jsonCapture.capture());
        assertNotNull(jsonCapture.getValue());
        JsonArray result = new JsonArray(jsonCapture.getValue());
        JsonObject row = result.getJsonObject(0);

        assertEquals(group.getName(), row.getString("name"));
    }

    @Test
    public void test_groupRouteHandler_getAll_with_error_response_status_INTERNAL_SERVER_ERROR() {
        GroupRouteHandler instance = new GroupRouteHandler(this.groupDatabaseService);
        RoutingContext routingContextMock = mock(RoutingContext.class);
        //Mock auth
        this.mockOAuthContext(routingContextMock);

        //Mock database service
        this.mockGroupDatabaseServiceFetchAll(null, new RuntimeException("General error"));

        //Mock response chain
        HttpServerResponse httpServerResponseMock = mock(HttpServerResponse.class);
        when(routingContextMock.response()).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.putHeader(anyString(), anyString())).thenReturn(httpServerResponseMock);
        when(httpServerResponseMock.setStatusCode(anyInt())).thenReturn(httpServerResponseMock);

        //Execution
        instance.getAll(routingContextMock);

        //Asserts
        verify(routingContextMock, atLeast(0)).response();

        ArgumentCaptor<String> headerKeyCapture = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValueCapture = ArgumentCaptor.forClass(String.class);
        verify(httpServerResponseMock).putHeader(headerKeyCapture.capture(), headerValueCapture.capture());
        assertEquals("content-type", headerKeyCapture.getValue());
        assertEquals("application/json; charset=utf-8", headerValueCapture.getValue());

        ArgumentCaptor<Integer> statusCodeCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(httpServerResponseMock).setStatusCode(statusCodeCaptor.capture());
        assertEquals(500, statusCodeCaptor.getValue().intValue());
    }

    private void mockOAuthContext(RoutingContext routingContextMock) {
        OAuth2UserImpl oAuth2UserImplMock = mock(OAuth2UserImpl.class);
        when(routingContextMock.user()).thenReturn(oAuth2UserImplMock);
        when(oAuth2UserImplMock.accessToken()).thenReturn(new JsonObject().put("email", "email@test.com"));
    }

    private void mockGroupDatabaseServiceFetchAll(final JsonArray jsonArrayResult, Throwable throwable) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Handler<AsyncResult<JsonArray>> callback = (Handler<AsyncResult<JsonArray>>) invocationOnMock.getArguments()[0];
                AsyncResult<JsonArray> result = new AsyncResult<JsonArray>() {

                    @Override
                    public JsonArray result() { return jsonArrayResult;}

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
                        return !this.succeeded();
                    }
                };
                callback.handle(result);
                return null;
            }
        }).when(this.groupDatabaseService).fetchAll(any());
    }
}
