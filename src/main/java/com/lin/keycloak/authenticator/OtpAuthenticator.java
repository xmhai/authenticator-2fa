package com.lin.keycloak.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.util.Date;
import java.util.Random;

public class OtpAuthenticator implements Authenticator {
    private static Logger logger = Logger.getLogger(OtpAuthenticator.class);

    public static final String CREDENTIAL_TYPE = "OTP";

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        logger.debug("authenticate called ... context = " + context);

        String otp = getOtp();
        storeOtp(context, otp);
        sendOtp(context, otp);

        Response challenge = context.form().createForm("otp.ftl");
        context.challenge(challenge);
    }

	private String getOtp() {
        double maxValue = Math.pow(10.0, 6);;
        Random r = new Random();
        long otp = (long)(r.nextFloat() * maxValue);
        return Long.toString(otp);
    }

    private void storeOtp(AuthenticationFlowContext context, String otp) {
    	context.getAuthenticationSession().setUserSessionNote("otp", otp);

    	// set expiry time
    	int ttl = 60; // default to 60 seconds
    	String value = getConfig(context, "otp.ttl");
        try {
        	ttl = Integer.parseInt(value);
        } catch(Exception e) { }
        
        long expiry = new Date().getTime() + ttl * 1000;
    	context.getAuthenticationSession().setUserSessionNote("expiry", Long.toString(expiry));
    }
    
    private String getConfig(AuthenticationFlowContext context, String key) {
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        if (config != null && config.getConfig() != null) {
	        return config.getConfig().get(key);
        }
        
    	return null;
    }
    
    private void sendOtp(AuthenticationFlowContext context, String otp) {
    	String data = String.format("{ \"username\":\"%s\", \"otp\":\"%s\" }", context.getUser().getUsername(), otp);
        System.out.println(data);
        
    	String url = getConfig(context, "otp.url");
        if (url == null) {
        	logger.error("SMS/Email service url not configured");
        	return;
        }
        
        //TODO: invoke sending service
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        String error = validateOtp(context);
        if (error!=null) {
            Response challenge =  context.form()
                    .setError(error)
                    .createForm("otp.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }
        context.success();
    }

    protected String validateOtp(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String input_otp = formData.getFirst("otp");
    	String otp = context.getAuthenticationSession().getUserSessionNotes().get("otp");
    	if (otp==null) {
    		logger.error("Cannot find OTP for user");
    		return "Error";
    	} else if (!otp.equals(input_otp)) {
    		return "Invalid OTP";
    	}

    	String expiry = context.getAuthenticationSession().getUserSessionNotes().get("expiry");
        if (new Date().getTime() > Long.parseLong(expiry)) {
    		return "OTP expired";
        }
    	
    	return null;
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

	@Override
	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
	}

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void close() {

    }
}
