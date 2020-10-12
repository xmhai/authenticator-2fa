2FA Authenticator
===================================================

1. First, Build the jar

2. Copy authenticator-2fa.jar to <KEYCLOAK_HOME>/providers.

3. Copy otp.ftl to <KEYCLOAK_HOME>/themes/base/login.

4. Login to admin console. Select correct Realm.

5. Go to the Authentication menu item and go to the Flow tab, you will be able to view the currently
   defined flows. Copy the "Browser" flow to "Browser-2FA".

6. In "Browser-2FA" flow, click the "Actions" menu item of "Browser-2FA Forms" and "Add Execution".  Pick Sms/Email OTP

7. In the newly added Sms/Email OTP execution, click the "Actions" menu item and "Config", configure the url to send SMS/Email
Note: the request body is in the format of { "username":"admin", "otp":"2067" }

8. go to the Bindings tab, change Browser flow to "Browser-2FA"
