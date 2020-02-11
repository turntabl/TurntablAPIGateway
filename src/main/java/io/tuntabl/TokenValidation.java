package io.tuntabl;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

public class TokenValidation {

    public static boolean isTokenValidated(String authorizationHeader, RSAPublicKey  pubKey) {
        String ISSUER = System.getenv("ISSUER");
        String CLIENT_ID = System.getenv("CLIENT_ID");
        String HOST_DOMAIN = System.getenv("HOST_DOMAIN");
        try {
            boolean iss = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(authorizationHeader).getBody().get("iss").equals(ISSUER);
            boolean aud = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(authorizationHeader).getBody().get("aud").equals(CLIENT_ID);
            boolean hd = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(authorizationHeader).getBody().get("hd").equals(HOST_DOMAIN);

            return iss && aud && hd;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isTokenExpired(RSAPublicKey pubKey, String authorizationHeader ){
        Claims claim = Jwts.parser().setSigningKey(pubKey).parseClaimsJws(authorizationHeader).getBody();
        return (new Date()).after(claim.getExpiration());
    }

    public static Optional<RSAPublicKey> getParsedPublicKey(){
        String PUB_KEY = System.getenv("PUBLIC_KEY") ;
        String PUBLIC_KEY = "";
        if ( PUB_KEY != null && !PUB_KEY.isEmpty()) {
            PUBLIC_KEY = PUB_KEY.replace(" ", "");
        }else {
            return Optional.empty();
        }
        try {
            X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(PUBLIC_KEY));
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpecX509);
            return Optional.of(pubKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}