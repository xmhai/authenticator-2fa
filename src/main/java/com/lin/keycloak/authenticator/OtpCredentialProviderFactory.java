package com.lin.keycloak.authenticator;

import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.CredentialProviderFactory;
import org.keycloak.models.KeycloakSession;

public class OtpCredentialProviderFactory implements CredentialProviderFactory<OtpCredentialProvider> {

    public static final String PROVIDER_ID =  "OTP";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public CredentialProvider create(KeycloakSession session) {
        return new OtpCredentialProvider(session);
    }
}
