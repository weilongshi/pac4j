package org.pac4j.jwt.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.text.ParseException;

/**
 * Elliptic curve signature configuration: http://connect2id.com/products/nimbus-jose-jwt/examples/jwt-with-ec-signature
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class ECSignatureConfiguration extends InitializableObject implements SignatureConfiguration {

    private ECPublicKey publicKey;

    private ECPrivateKey privateKey;

    private JWSAlgorithm algorithm = JWSAlgorithm.ES256;

    public ECSignatureConfiguration() {}

    public ECSignatureConfiguration(final KeyPair keyPair) {
        setKeyPair(keyPair);
    }

    public ECSignatureConfiguration(final KeyPair keyPair, final JWSAlgorithm algorithm) {
        setKeyPair(keyPair);
        this.algorithm = algorithm;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("algorithm", algorithm);

        if (!supports(this.algorithm)) {
            throw new TechnicalException("Only the ES256, ES384 and ES512 algorithms are supported for elliptic curve signature");
        }
    }


    @Override
    public boolean supports(final JWSAlgorithm algorithm) {
        return algorithm != null && ECDSAVerifier.SUPPORTED_ALGORITHMS.contains(algorithm);
    }

    @Override
    public SignedJWT sign(JWTClaimsSet claims) {
        init();
        CommonHelper.assertNotNull("privateKey", privateKey);

        try {
            final JWSSigner signer = new ECDSASigner(this.privateKey);
            final SignedJWT signedJWT = new SignedJWT(new JWSHeader(algorithm), claims);
            signedJWT.sign(signer);
            return signedJWT;
        } catch (final JOSEException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public boolean verify(final SignedJWT jwt) throws JOSEException {
        init();
        CommonHelper.assertNotNull("publicKey", publicKey);

        final JWSVerifier verifier = new ECDSAVerifier(this.publicKey);
        return jwt.verify(verifier);
    }

    public static ECSignatureConfiguration buildFromJwk(final String json) {
        CommonHelper.assertNotBlank("json", json);

        try {
            final ECKey ecKey = ECKey.parse(json);
            return new ECSignatureConfiguration(ecKey.toKeyPair());
        } catch (final JOSEException | ParseException e) {
            throw new TechnicalException(e);
        }
    }

    public void setKeyPair(final KeyPair keyPair) {
        CommonHelper.assertNotNull("keyPair", keyPair);
        this.privateKey = (ECPrivateKey) keyPair.getPrivate();
        this.publicKey = (ECPublicKey) keyPair.getPublic();
    }

    public ECPublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(final ECPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public ECPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(final ECPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public JWSAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(final JWSAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "keys", "[protected]", "algorithm", algorithm);
    }
}
