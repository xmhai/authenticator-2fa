package com.lin.keycloak.authenticator;

import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialModel;
import org.keycloak.util.JsonSerialization;

import java.io.IOException;

public class OtpCredentialModel extends CredentialModel {
	private static final long serialVersionUID = 1L;

	public static final String TYPE = "OTP";

    private final OtpCredentialData credentialData;
    private final OtpSecretData secretData;

    public OtpCredentialModel(OtpCredentialData credentialData, OtpSecretData secretData) {
        this.credentialData = credentialData;
        this.secretData = secretData;
	}

    OtpCredentialModel(String otp) {
        credentialData = new OtpCredentialData();
        secretData = new OtpSecretData(otp);
    }

	public static OtpCredentialModel createOtp(String otp) {
        OtpCredentialModel credentialModel = new OtpCredentialModel(otp);
        credentialModel.fillCredentialModelFields();
        return credentialModel;
    }

    public static OtpCredentialModel createFromCredentialModel(CredentialModel credentialModel){
        try {
            OtpCredentialData credentialData = JsonSerialization.readValue(credentialModel.getCredentialData(), OtpCredentialData.class);
            OtpSecretData secretData = JsonSerialization.readValue(credentialModel.getSecretData(), OtpSecretData.class);

            OtpCredentialModel OtpCredentialModel = new OtpCredentialModel(credentialData, secretData);
            OtpCredentialModel.setUserLabel(credentialModel.getUserLabel());
            OtpCredentialModel.setCreatedDate(credentialModel.getCreatedDate());
            OtpCredentialModel.setType(TYPE);
            OtpCredentialModel.setId(credentialModel.getId());
            OtpCredentialModel.setSecretData(credentialModel.getSecretData());
            OtpCredentialModel.setCredentialData(credentialModel.getCredentialData());
            return OtpCredentialModel;
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public OtpCredentialData getOtpCredentialData() {
        return credentialData;
    }

    public OtpSecretData getOtpSecretData() {
        return secretData;
    }

    private void fillCredentialModelFields(){
        try {
            setCredentialData(JsonSerialization.writeValueAsString(credentialData));
            setSecretData(JsonSerialization.writeValueAsString(secretData));
            setType(TYPE);
            setCreatedDate(Time.currentTimeMillis());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
