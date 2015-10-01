package com.socialcast.modals;

import java.util.List;

/**
 * Created by sdarisi on 9/28/15.
 */
public class MediaListItem {

    public String id;
    public String createdTime;
    public String title;
    public String source; // Picture source URL
    public String authorName;
    public String authorId;
    public List<MediaImageSubItem> images; // List of sub
}
