package com.socialcast.ui;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.socialcast.R;
import com.socialcast.modals.MediaListItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdarisi on 7/24/15.
 */
public class FeedListAdapter extends BaseAdapter implements View.OnClickListener {

    final LayoutInflater inflater;

    private List<MediaListItem> mediaList;

    public FeedListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setMediaList(List<MediaListItem> list) {
        mediaList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mediaList.size();
    }

    @Override
    public Object getItem(int position) {
        return mediaList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.feed_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            convertView.setOnClickListener(this);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MediaListItem item = (MediaListItem) getItem(position);
        holder.mediaListItem = item;
        holder.userName.setText(item.title);
        holder.image.setImageURI(Uri.parse(item.source));

        return convertView;
    }

    @Override
    public void onClick(View v) {
        try {
            ViewHolder viewHolder = (ViewHolder) v.getTag();
            VideoCastManager castManager = VideoCastManager.getInstance();
            castManager.loadMedia(buildCastMedia(viewHolder.mediaListItem), true, 0);
        } catch (NoConnectionException |
                TransientNetworkDisconnectionException e) {
            Log.e("SocialCast", "Failed to add item to queue or play remotely", e);
        }
    }

    private MediaInfo buildCastMedia(MediaListItem item) {
        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_PHOTO);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, item.title);

        return new MediaInfo.Builder(item.source)
                .setStreamType(MediaInfo.STREAM_TYPE_NONE)
                .setContentType("image/jpeg")
                .setMetadata(mediaMetadata)
                .build();

    }

    class ViewHolder {
        @Bind(R.id.avatar_image)
        ImageView avatarImage;
        @Bind(R.id.user_name)
        TextView userName;
        @Bind(R.id.image_view)
        SimpleDraweeView image;
        MediaListItem mediaListItem;

        ViewHolder(View root) {
            ButterKnife.bind(this, root);
        }

    }

}
