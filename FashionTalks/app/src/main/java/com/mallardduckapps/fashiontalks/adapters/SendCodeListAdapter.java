package com.mallardduckapps.fashiontalks.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.mallardduckapps.fashiontalks.FashionTalksApp;
import com.mallardduckapps.fashiontalks.R;
import com.mallardduckapps.fashiontalks.objects.User;
import com.mallardduckapps.fashiontalks.utils.Constants;
import com.mallardduckapps.fashiontalks.utils.FTUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 21/02/15.
 */

public class SendCodeListAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<User> data;
    private LayoutInflater inflater = null;
    private final String TAG = "SENDCODE_LIST_ADAPTER";
    Resources res;
    DisplayImageOptions options;
    String pathMainUrl;
    int[] selectedUserIds;
    AssetManager manager;
    String font;

    public SendCodeListAdapter(Activity act, ArrayList<User> userList){

        this.activity = act;
        data = userList;
        res = act.getResources();
        manager = activity.getAssets();
        font = activity.getString(R.string.font_helvatica_lt);
        inflater = (LayoutInflater) act
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = ((FashionTalksApp) act.getApplication()).options;
        pathMainUrl = new StringBuilder(Constants.CLOUD_FRONT_URL).append("/100x100/").toString();
        if(data != null){
            if(data.size() != 0){
                selectedUserIds = new int[data.size()];
            }
        }
    }

    public void addData(ArrayList<User> data){
        if(this.data == null){
            this.data = data;
        }else{
            // this.data.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    public int[] getSelectedUserIds(){
        return selectedUserIds;
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        final User user = data.get(position);
        if (vi == null) {
            vi = inflater.inflate(R.layout.toggled_list_row, parent,
                    false);
            holder = new ViewHolder();
            holder.nameTv = (TextView) vi.findViewById(R.id.nameTv);
            holder.nameTv.setTypeface(FTUtils.loadFont(manager, font));
            holder.thumbView = (RoundedImageView) vi.findViewById(R.id.thumbnailImage);
            holder.button = (CheckBox) vi.findViewById(R.id.followButton);
            vi.setTag(holder);
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.nameTv.setText(user.getUserName());
        String path = new StringBuilder(pathMainUrl).append(user.getPhotoPath()).toString();
        ImageLoader.getInstance()
                .displayImage(path, holder.thumbView, options);
        holder.button.setChecked(user.getIsFollowing() == 1);
        holder.button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Log.d(TAG, "BUTTON CHECKED is " + isChecked);
                if(isChecked){
                    selectedUserIds[position] = user.getId();
                }else{
                    selectedUserIds[position] = 0;
                }

            }
        });

        return vi;
    }

    public static class ViewHolder {
        RoundedImageView thumbView;
        TextView nameTv;
        CheckBox button;
    }
}

