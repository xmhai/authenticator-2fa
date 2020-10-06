package com.lin.keycloak.authenticator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OtpSecretData {

     private final String otp;

    @JsonCreator
     public OtpSecretData(@JsonProperty("otp") String otp) {
         this.otp = otp;
     }

    public String getOtp() {
        return otp;
    }
}
