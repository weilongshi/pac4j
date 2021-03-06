package org.pac4j.jwt;

/**
 * JWT claims.
 *
 * @author Jerome Leleu
 * @since 1.8.3
 */
public interface JwtClaims {

    String ISSUER          = "iss";

    String SUBJECT         = "sub";

    String AUDIENCE        = "aud";

    String EXPIRATION_TIME = "exp";

    String NOT_BEFORE      = "nbf";

    String ISSUED_AT       = "iat";

    String JWT_ID          = "jti";
}
