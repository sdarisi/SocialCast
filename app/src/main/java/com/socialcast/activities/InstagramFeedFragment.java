package com.socialcast.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socialcast.R;
import com.socialcast.modals.MediaItemAuthor;
import com.socialcast.modals.MediaListItem;
import com.socialcast.utils.InstagramService;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.OnClick;

/**
 * Created by sdarisi on 8/19/15.
 */
public class InstagramFeedFragment extends SocialFeedFragment {

    public static final String INSTAGRAM_TOKEN_PREFS_NAME = "instagram.token";
    private static final String EXTRA_CUSTOM_TABS_SESSION = "android.support.customtabs.extra.SESSION";
    private static final String EXTRA_CUSTOM_TABS_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";

    private String nextMaxId; // max id of the media item - used for pagination

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        String token = getActivity().getSharedPreferences("com.socialcast", Context.MODE_PRIVATE).getString(INSTAGRAM_TOKEN_PREFS_NAME, null);

        if (token == null) {
            feedList.setVisibility(View.GONE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            getFeed(token, 30, null, null);
        }
        return v;
    }

    @OnClick(R.id.empty_text)
    public void loginToInstagram(View v) {
        try {
            String url = InstagramService.requestOAuthUrl(getString(R.string.instagram_client_id), getString(R.string.instagram_redirect_uri), InstagramService.Scope.basic, InstagramService.Scope.relationships, InstagramService.Scope.comments);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.putExtra(EXTRA_CUSTOM_TABS_TOOLBAR_COLOR, android.R.color.holo_blue_light);
            startActivityForResult(intent, 1000);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /*
     * Gets a feed of media items from Instagram /users/self/feed endpoint
     *
     * @param accessToken	A valid access token
     * @param count Count of media to return
     * @param minId	Return media later than this min_id
     * @param maxId Return media earlier than this max_id
     *
     */
    private void getFeed(String accessToken, int count, String minId, String maxId) {
        try {
            String url = new URI("https", "api.instagram.com", "/v1/users/self/feed", "access_token="+accessToken, null).toString();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject responseJson = new JSONObject(response.body().string());
                            Log.d(TAG, "instagram feed body: " + responseJson);
                            nextMaxId = ((JSONObject)responseJson.get("pagination")).getString("next_max_id");
                            JSONArray media = responseJson.getJSONArray("data");
                            if (media.length() > 0) {
                                final ArrayList<MediaListItem> listItems = new ArrayList<MediaListItem>();
                                for (int i = 0; i < media.length(); i++) {
                                    try {
                                        JSONObject dataItem = media.getJSONObject(i);
                                        MediaListItem item = new MediaListItem();
                                        item.id = dataItem.getString("id");
                                        item.createdTime = dataItem.getString("created_time");
                                        item.mediaType = dataItem.getString("type");
                                        // TODO figure out a better modal extraction
                                        item.source = ((JSONObject)((JSONObject)dataItem.get("images")).get("standard_resolution")).getString("url");
                                        item.title = ((JSONObject)dataItem.get("caption")).getString("text");
                                        JSONObject user = (JSONObject)dataItem.get("user");
                                        item.author = new MediaItemAuthor();
                                        item.author.displayName = user.getString("full_name");
                                        item.author.id = user.getString("id");
                                        item.author.profilePictureUrl = user.getString("profile_picture");
                                        item.author.userName = user.getString("username");
                                        listItems.add(item);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayList<MediaListItem> items = listItems;
                                        feedListAdapter.setMediaList(items);
                                        feedListAdapter.notifyDataSetChanged();
                                        emptyText.setVisibility(View.GONE);
                                        feedList.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
