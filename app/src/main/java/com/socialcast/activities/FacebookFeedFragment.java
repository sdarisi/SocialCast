package com.socialcast.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.Utility;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.socialcast.R;
import com.socialcast.modals.MediaListItem;
import com.socialcast.ui.FeedListAdapter;
import com.socialcast.utils.UtilMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import butterknife.OnClick;

/**
 * Created by sdarisi on 8/19/15.
 */
public class FacebookFeedFragment extends SocialFeedFragment implements GraphRequest.Callback {

    public static final String FACEBOOK_TOKEN_PREFS_NAME = "facebook.token";

    // Constants related to Facebook token JSON serialization.
    private static final int CURRENT_JSON_FORMAT = 1;
    private static final String VERSION_KEY = "version";
    private static final String EXPIRES_AT_KEY = "expires_at";
    private static final String PERMISSIONS_KEY = "permissions";
    private static final String DECLINED_PERMISSIONS_KEY = "declined_permissions";
    private static final String TOKEN_KEY = "token";
    private static final String SOURCE_KEY = "source";
    private static final String LAST_REFRESH_KEY = "last_refresh";
    private static final String APPLICATION_ID_KEY = "application_id";
    public static final String USER_ID_KEY = "user_id";

    CallbackManager callbackManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        // Initialize facebook sdk, login and call back managers
        FacebookSdk.sdkInitialize(getActivity());
        AccessToken token = null;
        try {
            token = (AccessToken) createFromJSONObject(UtilMethods.getJSONFromPreferences(getActivity(), FACEBOOK_TOKEN_PREFS_NAME));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if ( token == null ) {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(LoginResult loginResult) {
                    AccessToken token = loginResult.getAccessToken();
                    Set<String> grantedPermissions = loginResult.getRecentlyGrantedPermissions();
                    Log.d(TAG, "access token: " + token.getToken() + ", granted permissions: " + grantedPermissions.toString());
                    try {
                        UtilMethods.saveJSONToPreferences(getActivity(), facebookTokentoJSONObject(token) ,FACEBOOK_TOKEN_PREFS_NAME);
                        getFacebookMedia(token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "canceled facebook login");
                }

                @Override
                public void onError(FacebookException e) {
                    Log.d(TAG, "facebook login error: " + e.toString());

                }
            });
            feedList.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            getFacebookMedia(token);
        }
        return v;
    }

    @OnClick(R.id.empty_text)
    public void loginToFacebook(View v) {
        LoginManager.getInstance().logInWithReadPermissions(FacebookFeedFragment.this, Arrays.asList("public_profile", "user_photos", "user_videos"));
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void getFacebookMedia(AccessToken token) {
        GraphRequest req = GraphRequest.newGraphPathRequest(token, "/" + token.getUserId() + "/photos", this);

        Log.d(TAG, "req object: " + req.toString());
        req.executeAsync();
    }

    @Override
    public void onCompleted(GraphResponse graphResponse) {
        Log.d(TAG, "response URL: " + graphResponse.getConnection().getURL());
        try {
            if (graphResponse.getConnection().getResponseCode() == 200) {
                JSONObject responseData = graphResponse.getJSONObject();
                if (responseData != null) {
                    Log.d(TAG, "FB graph response callback: " + responseData.toString());
                    JSONArray media = responseData.getJSONArray("data");
                    if (media.length() > 0) {
                        ArrayList<MediaListItem> listItems = new ArrayList<MediaListItem>();
                        for (int i = 0; i < media.length(); i++) {
                            try {
                                JSONObject dataItem = media.getJSONObject(i);
                                MediaListItem item = new MediaListItem();
                                item.id = dataItem.getString("id");
                                item.createdTime = dataItem.getString("created_time");
                                item.source = dataItem.getString("source");
                                item.title = dataItem.getString("name");
                                listItems.add(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        feedListAdapter.setMediaList(listItems);
                        feedListAdapter.notifyDataSetChanged();
                        emptyText.setVisibility(View.GONE);
                        feedList.setVisibility(View.VISIBLE);

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    JSONObject facebookTokentoJSONObject(AccessToken token) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(VERSION_KEY, CURRENT_JSON_FORMAT);
        jsonObject.put(TOKEN_KEY, token.getToken());
        jsonObject.put(EXPIRES_AT_KEY, token.getExpires().getTime());
        JSONArray permissionsArray = new JSONArray(token.getPermissions());
        jsonObject.put(PERMISSIONS_KEY, permissionsArray);
        JSONArray declinedPermissionsArray = new JSONArray(token.getDeclinedPermissions());
        jsonObject.put(DECLINED_PERMISSIONS_KEY, declinedPermissionsArray);
        jsonObject.put(LAST_REFRESH_KEY, token.getLastRefresh().getTime());
        jsonObject.put(SOURCE_KEY, token.getSource().name());
        jsonObject.put(APPLICATION_ID_KEY, token.getApplicationId());
        jsonObject.put(USER_ID_KEY, token.getUserId());

        return jsonObject;
    }

    AccessToken createFromJSONObject(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null)
            return null;
        int version = jsonObject.getInt(VERSION_KEY);
        if (version > CURRENT_JSON_FORMAT) {
            throw new FacebookException("Unknown AccessToken serialization format.");
        }

        String token = jsonObject.getString(TOKEN_KEY);
        Date expiresAt = new Date(jsonObject.getLong(EXPIRES_AT_KEY));
        JSONArray permissionsArray = jsonObject.getJSONArray(PERMISSIONS_KEY);
        JSONArray declinedPermissionsArray = jsonObject.getJSONArray(DECLINED_PERMISSIONS_KEY);
        Date lastRefresh = new Date(jsonObject.getLong(LAST_REFRESH_KEY));
        AccessTokenSource source = AccessTokenSource.valueOf(jsonObject.getString(SOURCE_KEY));
        String applicationId = jsonObject.getString(APPLICATION_ID_KEY);
        String userId = jsonObject.getString(USER_ID_KEY);

        return new AccessToken(
                token,
                applicationId,
                userId,
                Utility.jsonArrayToStringList(permissionsArray),
                Utility.jsonArrayToStringList(declinedPermissionsArray),
                source,
                expiresAt,
                lastRefresh);
    }
}
