package com.lin.keycloak.authenticator;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.authentication.ConfigurableAuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class OtpAuthenticatorFactory implements AuthenticatorFactory, ConfigurableAuthenticatorFactory {

    public static final String PROVIDER_ID = "otp-authenticator";
    private static final OtpAuthenticator SINGLETON = new OtpAuthenticator();

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.ALTERNATIVE,
            AuthenticationExecutionModel.Requirement.DISABLED
    };
    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<ProviderConfigProperty>();

    static {
        ProviderConfigProperty property;
        property = new ProviderConfigProperty();
        property.setName("otp.ttl");
        property.setLabel("OTP time to live");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("The validity of the OTP in seconds.");
        configProperties.add(property);

        property = new ProviderConfigProperty();
        property.setName("otp.len");
        property.setLabel("Length of the OTP");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("Length of the OTP.");
        configProperties.add(property);

        property = new ProviderConfigProperty();
        property.setName("otp.url");
        property.setLabel("SMS/Email sending service URL");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        property.setHelpText("SMS/Email sending service URL");
        configProperties.add(property);
    }


    @Override
    public String getHelpText() {
        return "OTP sent to your mobile phone or email";
    }

    @Override
    public String getDisplayType() {
        return "Sms/Email OTP";
    }

    @Override
    public String getReferenceCategory() {
        return "Sms/Email OTP";
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }
}
