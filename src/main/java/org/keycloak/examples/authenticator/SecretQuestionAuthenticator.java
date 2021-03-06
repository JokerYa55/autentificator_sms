/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.examples.authenticator;

import java.math.BigInteger;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.common.util.ServerCookie;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.jboss.logging.Logger;
import org.keycloak.common.util.Time;
import org.keycloak.credential.CredentialModel;
import static org.keycloak.examples.authenticator.SecretQuestionCredentialProvider.SECRET_QUESTION;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SecretQuestionAuthenticator implements Authenticator {

    public static final String CREDENTIAL_TYPE = "secret_question";
    private final Logger log = Logger.getLogger(getClass().getName());
    public static final String SECRET_QUESTION = "SECRET_QUESTION";

    protected boolean hasCookie(AuthenticationFlowContext context) {
        log.info("hasCookie => " + context);
        Cookie cookie = context.getHttpRequest().getHttpHeaders().getCookies().get("SECRET_QUESTION_ANSWERED");
        boolean result = cookie != null;
        if (result) {
            System.out.println("Bypassing secret question because cookie as set");
        }
        return result;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        log.info("authenticate => " + context);
        /*if (hasCookie(context)) {
            context.success();
            return;
        }*/
        KeycloakSession session = context.getSession();
        UserModel user = context.getUser();
        RealmModel realm = context.getRealm();

        String answer = genSalt().substring(0, 6);
        user.setSingleAttribute(SECRET_QUESTION, answer);
        

//        List<CredentialModel> creds = session.userCredentialManager().getStoredCredentialsByType(realm, user, SECRET_QUESTION);
////        if (creds.isEmpty()) {
//        // ���������� ��� t
//        CredentialModel secret = new CredentialModel();
//        secret.setType(SECRET_QUESTION);
//        
//        secret.setValue(answer);
//        secret.setCreatedDate(Time.currentTimeMillis());
//
//        session.userCredentialManager().createCredential(realm, user, secret);
        session.userCache().evict(realm, user);
//        } else {
//        }

        Response challenge = context.form().createForm("secret-question.ftl");
        context.challenge(challenge);
    }

    @Override
    public void action(AuthenticationFlowContext context) {
        log.info("action => " + context);
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        if (formData.containsKey("cancel")) {
            context.cancelLogin();
            return;
        }
        boolean validated = validateAnswer(context);
        if (!validated) {
            Response challenge = context.form()
                    .setError("�������� ���")
                    .createForm("secret-question.ftl");
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }
        setCookie(context);
        context.success();
    }

    protected void setCookie(AuthenticationFlowContext context) {
        log.info("setCookie => " + context);
        AuthenticatorConfigModel config = context.getAuthenticatorConfig();
        int maxCookieAge = 60 * 60 * 24 * 30; // 30 days
        if (config != null) {
            maxCookieAge = Integer.valueOf(config.getConfig().get("cookie.max.age"));

        }
        URI uri = context.getUriInfo().getBaseUriBuilder().path("realms").path(context.getRealm().getName()).build();
        addCookie("SECRET_QUESTION_ANSWERED", "true",
                uri.getRawPath(),
                null, null,
                maxCookieAge,
                false, true);
    }

    public static void addCookie(String name, String value, String path, String domain, String comment, int maxAge, boolean secure, boolean httpOnly) {
        HttpResponse response = ResteasyProviderFactory.getContextData(HttpResponse.class);
        StringBuffer cookieBuf = new StringBuffer();
        ServerCookie.appendCookieValue(cookieBuf, 1, name, value, path, domain, comment, maxAge, secure, httpOnly);
        String cookie = cookieBuf.toString();
        response.getOutputHeaders().add(HttpHeaders.SET_COOKIE, cookie);
    }

    protected boolean validateAnswer(AuthenticationFlowContext context) {
        log.info("validateAnswer => " + context);
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        String secret = formData.getFirst("secret_answer");
        UserCredentialModel input = new UserCredentialModel();
        input.setType(SecretQuestionCredentialProvider.SECRET_QUESTION);
        input.setValue(secret);
        return context.getSession().userCredentialManager().isValid(context.getRealm(), context.getUser(), input);
    }

    @Override
    public boolean requiresUser() {
        log.info("requiresUser");
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        log.info("***********************");
        log.info("configuredFor => session = " + session + " realm => " + realm + " userModel => " + user);
        boolean res = false;
        //res = session.userCredentialManager().isConfiguredFor(realm, user, SecretQuestionCredentialProvider.SECRET_QUESTION);;
        res = true;
        log.info(String.format("res = %s", res));
        log.info("***********************");

        return res;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
        log.info("setRequiredActions => session = " + session + " realm => " + realm + " userModel => " + user);
        user.addRequiredAction(SecretQuestionRequiredAction.PROVIDER_ID);
    }

    @Override
    public void close() {

    }

    /**
     *
     * @param bytes
     * @return
     */
    public static String encodeToHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    /**
     * ������� ���������� "����" ��� ��������� hash-� ������
     *
     * @return
     */
    public String genSalt() {
        int idx = (int) (Math.random() * 10);
        String res = encodeToHex(UUID.randomUUID().toString().getBytes());
        log.info("res = " + res + " idx = " + idx);
        int len = res.length();
        if (idx > 5) {
            res = res.substring(len - 11, len - 1);
        } else {
            res = res.substring(idx, 10 + idx);
        }
        log.info("res = " + res);
        return res.toUpperCase();
    }

}
