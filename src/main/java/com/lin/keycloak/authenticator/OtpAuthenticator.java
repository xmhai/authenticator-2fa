package com.lin.keycloak.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.CredentialValidator;
import org.keycloak.credential.CredentialProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.Random;

public class OtpAuthenticator implements Authenticator, CredentialValidator<OtpCredentialProvider> {
    private static Logger logger = Logger.getLogger(OtpAuthenticator.class);

    public static final String CREDENTIAL_TYPE = "OTP";

    private static enum CODE_STATUS {
        VALID,
        INVALID,
        EXPIRED
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.debug("authenticate called ... context = " + context);
        System.out.println("authenticate called ... context = " + context);

        // generate OTP
        String otp = getOtp();
        System.out.println("otp = " + otp);
        
        storeOtp(context, otp);
        System.out.println("save to db");
        
        // send otp (call digiGov service to send)
        sendOtp(context.getUser().getEmail(), otp);
        System.out.println("otp sent");

        Response challenge = context.form().createForm("otp.ftl");
        System.out.println(challenge);
        context.challenge(challenge);
    }

	private String getOtp() {
        double maxValue = Math.pow(10.0, 6);;
        Random r = new Random();
        long otp = (long)(r.nextFloat() * maxValue);
        logger.debug("OTP generated: " + otp);
        return Long.toString(otp);
    }

    private void storeOtp(AuthenticationFlowContext context, String otp) {
    	OtpCredentialModel credentialModel = new OtpCredentialModel(otp);
    	context.getSession().setAttribute("otp", otp);
    	context.getAuthenticationSession().setUserSessionNote("otp", otp);
    	//context.getSession().userCredentialManager().updateCredential(context.getRealm(), context.getUser(), credentialModel);
    }
    
    private void sendOtp(String email, String otp) {
    	logger.debugv("OTP {0} sent to {1}", otp, email);
	}

    @Override
    public void action(AuthenticationFlowContext context) {
        boolean validated = validateOtp(context);
        if (!validated) {
            Response challenge =  context.form()
                    .setError("Invalid OTP entered")
                    .createForm("otp.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }
        context.success();
    }

    protected boolean validateOtp(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String input_otp = formData.getFirst("otp");
        System.out.println("input otp = " + input_otp);
    	String otp = context.getAuthenticationSession().getUserSessionNotes().get("otp");
        System.out.println("otp = " + otp);
    	return otp.equals(input_otp);
        
        //String credentialId = context.getSelectedCredentialId();
        //if (credentialId == null || credentialId.isEmpty()) {
        //    credentialId = getCredentialProvider(context.getSession())
        //            .getDefaultCredential(context.getSession(), context.getRealm(), context.getUser()).getId();
        //    context.setSelectedCredentialId(credentialId);
        //}

        //UserCredentialModel input = new UserCredentialModel(credentialId, getType(context.getSession()), otp);
        //return getCredentialProvider(context.getSession()).isValid(context.getRealm(), context.getUser(), input);
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

	@Override
	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return getCredentialProvider(session).isConfiguredFor(realm, user, getType(session));
    }

    @Override
    public void close() {

    }

    public OtpCredentialProvider getCredentialProvider(KeycloakSession session) {
        return (OtpCredentialProvider)session.getProvider(CredentialProvider.class, OtpCredentialProviderFactory.PROVIDER_ID);
    }
}
