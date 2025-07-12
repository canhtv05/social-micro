package com.canhtv05.auth.util;

import com.canhtv05.auth.dto.res.UserResponse;
import com.canhtv05.auth.exception.AppException;
import com.canhtv05.auth.exception.ErrorCode;
import com.canhtv05.auth.service.RedisService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenUtil {

    @NonFinal
    @Value("${jwt.key}")
    String key;

    @NonFinal
    @Value("${jwt.valid-duration}")
    Long validDuration;

    @NonFinal
    @Value("${jwt.refresh-duration}")
    Long refreshDuration;

    String EMAIL_CLAIM = "email";
    String ISSUER = "canhtv05";

    RedisService redisService;


    public SignedJWT verifyToken(String token) throws ParseException, JOSEException {
        try {
            if (token.startsWith("Bearer")) {
                token = token.replace("Bearer ", "");
            }
            JWSVerifier verifier = new MACVerifier(key.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean verified = signedJWT.verify(verifier);
            if (!(verified && expiration.after(Date.from(Instant.now())))) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            if (StringUtils.isNotBlank(redisService.get(signedJWT.getJWTClaimsSet().getJWTID()))) {
                throw new AppException(ErrorCode.TOKEN_BLACKLISTED);
            }
            return signedJWT;
        } catch (ParseException | JOSEException _) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
    }

    public String generateAccessToken(UserResponse user) throws JOSEException {
        return generateToken(user, validDuration);
    }

    public String generateRefreshToken(UserResponse user) throws JOSEException {
        return generateToken(user, refreshDuration);
    }

    public String verifyAndExtractEmail(String token) throws ParseException, JOSEException {
        Object object = this.verifyToken(token).getJWTClaimsSet().getClaim(EMAIL_CLAIM);
        if (Objects.isNull(object)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return object.toString();
    }

    public long verifyAndExtractTokenExpired(String token) throws ParseException, JOSEException {
        Date expiredClaim = this.verifyToken(token).getJWTClaimsSet().getExpirationTime();
        if (Objects.isNull(expiredClaim)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return expiredClaim.getTime();
    }


    private String generateToken(UserResponse user, Long duration) throws JOSEException {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet =
                new JWTClaimsSet.Builder().subject(user.getId()).jwtID(UUID.randomUUID().toString()).issueTime(Date.from(Instant.now())).issuer(ISSUER).claim(EMAIL_CLAIM, user.getEmail()).expirationTime(Date.from(Instant.now().plus(duration, ChronoUnit.SECONDS))).build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        jwsObject.sign(new MACSigner(key.getBytes()));

        return jwsObject.serialize();
    }

}
