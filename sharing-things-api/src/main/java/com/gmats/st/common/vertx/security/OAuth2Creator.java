package com.gmats.st.common.vertx.security;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.oauth2.OAuth2Auth;

public interface OAuth2Creator {

    OAuth2Auth createOAuth(Vertx vertx);
}
