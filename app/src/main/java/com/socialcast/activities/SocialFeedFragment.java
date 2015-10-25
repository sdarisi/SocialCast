package com.socialcast.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.socialcast.R;
import com.socialcast.modals.MediaListItem;
import com.socialcast.ui.FeedListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdarisi on 7/19/15.
 */
public class SocialFeedFragment extends Fragment {

    @Bind(R.id.feed_list)
    ListView feedList;

    @Bind(R.id.empty_text)
    TextView emptyText;

    FeedListAdapter feedListAdapter;
    ArrayList<MediaListItem> mediaList;

    public static final String TAG = "SocialFeedFragment";
    public static final int COUNT = 10;
    public static final String[] URLS = {"http://www.counselingcenteroftherockies.com/rockies.jpg",
            "http://www.hdimagewallpaper.com/wp-content/uploads/2015/02/Rocky-Mountain-Images-13-HD-Images-Wallpapers-1024x768.jpg",
            "http://www.hdimagewallpaper.com/wp-content/uploads/2015/02/Rocky-Mountain-Images-7-HD-Images-Wallpapers-1024x761.jpg",
            "http://www.hdimagewallpaper.com/wp-content/uploads/2015/02/Rocky-Mountain-Images-3-HD-Images-Wallpapers-1024x765.jpg",
            "http://www.hdimagewallpaper.com/wp-content/uploads/2015/02/Rocky-Mountain-Images-28-Cool-Wallpapers-HD-1024x768.jpg",
            "http://www.hdimagewallpaper.com/wp-content/uploads/2015/02/Rocky-Mountain-Images-11-HD-Images-Wallpapers-1024x741.jpg",
            "http://www.hdimagewallpaper.com/wp-content/uploads/2015/01/Beautiful-Sunsets-Beach-22-HD-Images-Wallpapers-1024x576.jpg",
            "http://www.hdimagewallpaper.com/wp-content/uploads/2015/07/Beautiful-Clouds-7-18080-HD-Images-Wallpapers-1024x576.jpg",
            "http://www.hdimagewallpaper.com/wp-content/uploads/2015/06/Mountain-Beautiful-HD-11-48851-HD-Images-Wallpapers-1024x640.jpg",
            "http://www.hdimagewallpaper.com/wp-content/uploads/2015/06/Beautiful-Mountain-HD-4-35196-Cool-Wallpapers-HD-1024x576.jpg"
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_social_feed, container, false);
        ButterKnife.bind(this, v);

        feedListAdapter = new FeedListAdapter(getActivity());
//        feedListAdapter.setMediaList(buildCastMedia());
        feedList.setAdapter(feedListAdapter);

        View headerView = new View(getActivity());
        headerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        feedList.addHeaderView(headerView);
        feedList.addFooterView(headerView);

        emptyText.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public List<MediaListItem> buildCastMedia() {
        if (null != mediaList) {
            return mediaList;
        }
        mediaList = new ArrayList<MediaListItem>();

        for (int i = 0; i < URLS.length; i++) {
            MediaListItem item = new MediaListItem();
            item.source = URLS[i];
            item.title = "Image " + i;
            item.id = "" + i;
            mediaList.add(item);
        }
        return mediaList;
    }

}
