package com.gmats.st.group.http.handler;


import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.oauth2.AccessToken;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.impl.OAuth2UserImpl;
import io.vertx.ext.web.RoutingContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static io.vertx.core.http.HttpHeaders.AUTHORIZATION;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class AuthRouteHandlerTest {

    @Mock
    private OAuth2Auth oauth2;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void test_groupRouteHandler_authenticated_withRightToken_response_call_next() {
        AuthRouteHandler instance = new AuthRouteHandler(this.oauth2);
        RoutingContext routingContextMock = mock(RoutingContext.class);

        //Mock oauth service
        AccessToken accessTokenMock = mock(AccessToken.class);
        this.mockOAuth2Introspect(accessTokenMock, null);

        //Execution
        HttpServerRequest httpServerRequestMock = mock(HttpServerRequest.class);
        when(routingContextMock.request()).thenReturn(httpServerRequestMock);
        MultiMap headersMock = mock(MultiMap.class);
        when(httpServerRequestMock.headers()).thenReturn(headersMock);
        when(headersMock.get(AUTHORIZATION)).thenReturn("Beare SOME_TOKEN");

        instance.authenticated(routingContextMock);

        //Asserts
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(routingContextMock).setUser(userArgumentCaptor.capture());
        verify(routingContextMock).next();
        assertEquals(accessTokenMock, userArgumentCaptor.getValue());
    }

    @Test
    public void test_groupRouteHandler_authenticated_withError_withTokenIntrospection_call_fail() {
        AuthRouteHandler instance = new AuthRouteHandler(this.oauth2);
        RoutingContext routingContextMock = mock(RoutingContext.class);

        //Mock oauth service
        this.mockOAuth2Introspect(null, new Throwable("General error"));

        //Execution
        HttpServerRequest httpServerRequestMock = mock(HttpServerRequest.class);
        when(routingContextMock.request()).thenReturn(httpServerRequestMock);
        MultiMap headersMock = mock(MultiMap.class);
        when(httpServerRequestMock.headers()).thenReturn(headersMock);
        when(headersMock.get(AUTHORIZATION)).thenReturn("Beare SOME_TOKEN");

        instance.authenticated(routingContextMock);

        //Asserts
        verify(routingContextMock, never()).next();
        verify(routingContextMock).fail(any());
    }

    @Test
    public void test_groupRouteHandler_authenticated_withoutToken_fail() {
        AuthRouteHandler instance = new AuthRouteHandler(this.oauth2);
        RoutingContext routingContextMock = mock(RoutingContext.class);

       //Execution
        HttpServerRequest httpServerRequestMock = mock(HttpServerRequest.class);
        when(routingContextMock.request()).thenReturn(httpServerRequestMock);
        MultiMap headersMock = mock(MultiMap.class);
        when(httpServerRequestMock.headers()).thenReturn(headersMock);
        when(headersMock.get(AUTHORIZATION)).thenReturn(null);

        instance.authenticated(routingContextMock);

        //Asserts
        verify(routingContextMock, never()).next();

        ArgumentCaptor<Integer> statusCapture = ArgumentCaptor.forClass(Integer.class);
        verify(routingContextMock).fail(statusCapture.capture());
        assertEquals(401, statusCapture.getValue().intValue());
    }



    private void mockOAuthContext(RoutingContext routingContextMock) {
        OAuth2UserImpl oAuth2UserImplMock = mock(OAuth2UserImpl.class);
        when(routingContextMock.user()).thenReturn(oAuth2UserImplMock);
        when(oAuth2UserImplMock.accessToken()).thenReturn(new JsonObject().put("email", "email@test.com"));
    }

    private void mockOAuth2Introspect(final AccessToken jsonArrayResult, Throwable throwable) {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                Handler<AsyncResult<AccessToken>> callback = (Handler<AsyncResult<AccessToken>>) invocationOnMock.getArguments()[1];
                AsyncResult<AccessToken> result = new AsyncResult<AccessToken>() {

                    @Override
                    public AccessToken result() { return jsonArrayResult;}

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
        }).when(this.oauth2).introspectToken(anyString(), any());
    }
}
