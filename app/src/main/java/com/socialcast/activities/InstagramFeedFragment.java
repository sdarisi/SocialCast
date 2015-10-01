package com.socialcast.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.socialcast.R;

import butterknife.OnClick;

/**
 * Created by sdarisi on 8/19/15.
 */
public class InstagramFeedFragment extends SocialFeedFragment {

    public static final String INSTAGRAM_TOKEN_PREFS_NAME = "instagram.token";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        return v;
    }

    @OnClick(R.id.empty_text)
    public void loginToInstagram(View v) {

    }

}
