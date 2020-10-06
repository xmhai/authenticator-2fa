package com.lin.keycloak.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.credential.UserCredentialStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

public class OtpCredentialProvider implements CredentialProvider<OtpCredentialModel>, CredentialInputValidator {
    private static final Logger logger = Logger.getLogger(OtpCredentialProvider.class);

    protected KeycloakSession session;

    public OtpCredentialProvider(KeycloakSession session) {
        this.session = session;
    }

    private UserCredentialStore getCredentialStore() {
        return session.userCredentialManager();
    }


    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        if (!(input instanceof UserCredentialModel)) {
            logger.debug("Expected instance of UserCredentialModel for CredentialInput");
            return false;
        }
        if (!input.getType().equals(getType())) {
            return false;
        }
        String challengeResponse = input.getChallengeResponse();
        if (challengeResponse == null) {
            return false;
        }
        CredentialModel credentialModel = getCredentialStore().getStoredCredentialById(realm, user, input.getCredentialId());
        OtpCredentialModel cm = getCredentialFromModel(credentialModel);
        return cm.getOtpSecretData().getOtp().equals(challengeResponse);
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return getType().equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
    	return true; // the credential is auto-created by system
    }

    @Override
    public CredentialModel createCredential(RealmModel realm, UserModel user, OtpCredentialModel credentialModel) {
        if (credentialModel.getCreatedDate() == null) {
            credentialModel.setCreatedDate(Time.currentTimeMillis());
        }
        return getCredentialStore().createCredential(realm, user, credentialModel);
    }

    @Override
    public void deleteCredential(RealmModel realm, UserModel user, String credentialId) {
        getCredentialStore().removeStoredCredential(realm, user, credentialId);
    }

    @Override
    public OtpCredentialModel getCredentialFromModel(CredentialModel model) {
        return OtpCredentialModel.createFromCredentialModel(model);
    }

    @Override
    public String getType() {
        return OtpCredentialModel.TYPE;
    }
}
