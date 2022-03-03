package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.SpringApplicationContext;
import io.jsonwebtoken.Jwts;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
//    public static final String TOKEN_SECRET = "token-secret-constant";

    public static String getTokenSecret()
    {
        // Since app properties if of type - annotation - Component
        // * Either name the beanName as "appProperties" or add the bean to MobileAppWsApplication *
        // AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
        return appProperties.getTokenSecret();
    }

    public static String getUserIdFromToken(String token){
        if(token == null) return null;
        token = token.replace(SecurityConstants.TOKEN_PREFIX, "");

        String userId = Jwts.parser()
                .setSigningKey(SecurityConstants.getTokenSecret())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return userId;
    }
}