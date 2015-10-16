package com.socialcast.utils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by sdarisi on 10/2/15.
 */
public class InstagramService {

    public enum Scope {

        basic, comments, relationships, likes

    }

    public static String requestOAuthUrl(final String clientId, final String redirectUri, final Scope... scopes) throws URISyntaxException {
        final StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("response_type=").append("token");
        urlBuilder.append("&client_id=").append(clientId);
        urlBuilder.append("&redirect_uri=").append(redirectUri);
        if (scopes != null) {
            final StringBuilder scopeBuilder = new StringBuilder();
            for (int i = 0; i < scopes.length; i++) {
                scopeBuilder.append(scopes[i]);
                if (i < scopes.length - 1) {
                    scopeBuilder.append(' ');
                }
            }
            urlBuilder.append("&scope=").append(scopeBuilder.toString());
        }
        return new URI("https", "instagram.com", "/oauth/authorize", urlBuilder.toString(), null).toString();
    }


}
