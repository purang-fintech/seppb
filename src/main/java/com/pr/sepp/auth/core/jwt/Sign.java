package com.pr.sepp.auth.core.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Maps;
import com.pr.sepp.common.constants.CommonParameter;
import com.pr.sepp.common.exception.SeppClientException;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

public final class Sign {

    private Sign() {
        throw new IllegalAccessError();
    }

    public static final String CLAIM_USER_ID = CommonParameter.USER_ID;
    public static final String CLAIM_SUPPORT = "support";
    private static Map<String, JWTVerifier> verifierMap = Maps.newConcurrentMap();
    private static Map<String, Algorithm> algorithmMap = Maps.newConcurrentMap();

    private static Algorithm getAlgorithm(String signingToken) {
        return algorithmMap.computeIfAbsent(signingToken, Algorithm::HMAC512);
    }

    public static DecodedJWT verifySessionToken(String tokenString, String signingToken) {
        return verifyToken(tokenString, signingToken);
    }

    static DecodedJWT verifyToken(String tokenString, String signingToken) {
        return verifierMap.computeIfAbsent(signingToken, s -> JWT.require(Algorithm.HMAC512(s)).build()).verify(tokenString);
    }

    public static String generateSessionToken(Integer userId, String signingToken, boolean support, long duration) {
        if (StringUtils.isEmpty(signingToken)) {
            throw new SeppClientException("signing token 不能为空");
        }
        return JWT.create()
                .withClaim(CLAIM_USER_ID, userId)
                .withClaim(CLAIM_SUPPORT, support)
                .withExpiresAt(new Date(System.currentTimeMillis() + duration))
                .sign(getAlgorithm(signingToken));
    }

}