package com.localhost.anonymouschat.config;

public class WhiteList {
    public static final String[] AUTH_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/v3/api-docs/",
            "/swagger-resources/**",
            "/v3/api-docs/https",
            "/v3/api-docs/https/",
            "/swagger-ui.html",
            "/webjars/**",
            "/error",

            "/api/v1/auth/register",
            "/api/v1/auth/authenticate",
            "/ws/**",
            "/ws"
    };
}
