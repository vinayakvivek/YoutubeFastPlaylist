package com.crossroads.youtubefastplaylist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by crossroads on 6/3/16.
 */
public class VideoListAdapter extends ArrayAdapter<VideoItem> {

    private List<VideoItem> items;
    private int layoutResourceId;
    private Context context;
    LayoutInflater inflater;
    public static final int SEARCH_ITEM_LAYOUT = 0;
    public static final int PLAYLIST_ITEM_LAYOUT = 1;
    private int layoutType;

    int activeIndex;

    public VideoListAdapter(Context context, int resource, List<VideoItem> items,
                            LayoutInflater inflater,
                            int layoutType,
                            int activeIndex) {
        super(context, resource, items);
        layoutResourceId = resource;
        this.context = context;
        this.items = items;
        this.inflater = inflater;
        this.layoutType = layoutType;
        this.activeIndex = activeIndex;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        VideoItemHolder holder = null;

        if (convertView == null) {
            row = inflater.inflate(layoutResourceId, parent, false);
        }

        if (position == activeIndex) {
            row.setBackgroundColor(context.getResources().getColor(R.color.blue));
            Log.i("AppInfo", "changed back color - activeIndex : " + activeIndex);
        } else {
            row.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

       /* switch (position) {
            case 0 :
                row.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                break;
            case 1 :
                row.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case 2 : row.setBackgroundColor(context.getResources().getColor(R.color.blue));
                break;
            case 3 :
                row.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                break;
            default:
                row.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        }*/

        Log.i("AppInfo", "position in getView() : " + position);

        holder = new VideoItemHolder();
        holder.videoItem = items.get(position);
        holder.imageButton = (ImageButton) row.findViewById(R.id.add_button);
        holder.imageButton.setTag(holder.videoItem);

        holder.thumbnail = (ImageView) row.findViewById(R.id.video_thumbnail);
        holder.title = (TextView) row.findViewById(R.id.video_title);
//        holder.description = (TextView) row.findViewById(R.id.video_description);
        holder.position = position;
        row.setTag(holder);

        setupItem(holder);

        return row;

    }

    private void setupItem(VideoItemHolder holder) {
        holder.title.setText(holder.videoItem.getTitle());
//        holder.description.setText(holder.videoItem.getDescription());
        Picasso.with(context).load(holder.videoItem.getThumbnailURL()).into(holder.thumbnail);

        switch (layoutType) {
            case SEARCH_ITEM_LAYOUT:
                holder.imageButton.setImageResource(android.R.drawable.ic_menu_add);
                break;
            case PLAYLIST_ITEM_LAYOUT:
                holder.imageButton.setImageResource(android.R.drawable.ic_delete);
                break;
            default:
        }
    }

    public static class VideoItemHolder {
        VideoItem videoItem;
        ImageView thumbnail;
        TextView title;
        TextView description;
        //Button button;
        ImageButton imageButton;
        int position;
    }

}
