package com.pr.sepp.auth.core.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.pr.sepp.auth.model.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public final class Sessions {

    public static final long SHORT_SESSION = TimeUnit.HOURS.toMillis(1);
    public static final String COOKIE_NAME = "sepp-auth";

    private Sessions() {
        throw new IllegalAccessError();
    }


    public static void loginUser(Integer userId,
                                 boolean support,
                                 String signingSecret,
                                 HttpServletResponse response) {
        int maxAge = (int) (SHORT_SESSION / 1000);
        String token = Sign.generateSessionToken(userId, signingSecret, support, SHORT_SESSION);
        AtomicReference<Cookie> cookie = new AtomicReference<>(new Cookie(COOKIE_NAME, token));
        cookie.get().setPath("/");
        cookie.get().setMaxAge(maxAge);
        cookie.get().setHttpOnly(true);
        response.addCookie(cookie.get());
    }

    public static String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isEmpty(cookies)) return null;
        Cookie tokenCookie = Arrays.stream(cookies)
                .filter(cookie -> COOKIE_NAME.equals(cookie.getName()))
                .findAny().orElse(null);
        if (tokenCookie == null) return null;
        return tokenCookie.getValue();
    }

    public static void logout(HttpServletResponse response) {
        AtomicReference<Cookie> cookie = new AtomicReference<>(new Cookie(COOKIE_NAME, ""));
        cookie.get().setPath("/");
        cookie.get().setMaxAge(0);
        response.addCookie(cookie.get());
    }

    public static Session getSession(HttpServletRequest request, String signingSecret) {
        String token = getToken(request);
        if (token == null) return null;
        try {
            DecodedJWT decodedJwt = Sign.verifySessionToken(token, signingSecret);
            Integer userId = decodedJwt.getClaim(Sign.CLAIM_USER_ID).asInt();
            boolean support = decodedJwt.getClaim(Sign.CLAIM_SUPPORT).asBoolean();
            return Session.builder().userId(userId).support(support).build();
        } catch (Exception e) {
            log.error("token校验失败", "token", token, e);
            return null;
        }
    }

}
