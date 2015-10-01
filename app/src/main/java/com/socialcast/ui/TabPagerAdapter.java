package com.socialcast.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.view.ViewGroup;


import com.socialcast.R;
import com.socialcast.activities.FacebookFeedFragment;
import com.socialcast.activities.InstagramFeedFragment;
import com.socialcast.activities.SocialFeedFragment;

/**
 * Created by sdarisi on 7/19/15.
 */
public class TabPagerAdapter extends FragmentPagerAdapter{

    public static final int POSITION_FACEBOOK = 0;
    public static final int POSITION_TWITTER = POSITION_FACEBOOK + 1;
    public static final int POSITION_GOOGLEPLUS = POSITION_TWITTER + 1;
    public static final int POSITION_INSTAGRAM = POSITION_GOOGLEPLUS + 1;
    public static final int COUNT = POSITION_INSTAGRAM + 1;

    private Context context;
    private SparseArray<Fragment> fragments = new SparseArray<>();

    public TabPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int iconResId;

        switch (position) {
            case POSITION_FACEBOOK:
                iconResId = R.drawable.facebook;
                break;
            case POSITION_TWITTER:
                iconResId = R.drawable.twitter;
                break;
            case POSITION_GOOGLEPLUS:
                iconResId = R.drawable.googleplus;
                break;
            case POSITION_INSTAGRAM:
                iconResId = R.drawable.instagram;
                break;
            default:
                iconResId = -1;
                break;

        }
        Drawable image = context.getResources().getDrawable(iconResId);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, sb.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Override
    public Fragment getItem(int position) {
        SocialFeedFragment fragment;
        switch (position) {
            case POSITION_FACEBOOK:
                fragment = new FacebookFeedFragment();
                break;
            case POSITION_INSTAGRAM:
                fragment = new InstagramFeedFragment();
                break;
            default:
                fragment = new SocialFeedFragment();
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragments.remove(position);
        super.destroyItem(container, position, object);
    }

}
