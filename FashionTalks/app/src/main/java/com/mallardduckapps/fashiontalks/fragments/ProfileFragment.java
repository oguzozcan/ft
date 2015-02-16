package com.mallardduckapps.fashiontalks.fragments;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makeramen.RoundedImageView;
import com.mallardduckapps.fashiontalks.ProfileActivity;
import com.mallardduckapps.fashiontalks.R;
import com.mallardduckapps.fashiontalks.adapters.GalleryGridAdapter;
import com.mallardduckapps.fashiontalks.components.GridListOnScrollListener;
import com.mallardduckapps.fashiontalks.loaders.PopularPostsLoader;
import com.mallardduckapps.fashiontalks.loaders.PostsLoader;
import com.mallardduckapps.fashiontalks.objects.GalleryItem;
import com.mallardduckapps.fashiontalks.objects.Post;
import com.mallardduckapps.fashiontalks.objects.User;
import com.mallardduckapps.fashiontalks.utils.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 15/02/15.
 */
public class ProfileFragment extends BasicFragment implements LoaderManager.LoaderCallbacks<ArrayList<Post>>
        ,GridListOnScrollListener.OnScrolledToBottom, GalleryGridAdapter.PostItemClicked {

    boolean myProfile = false;
    private static final String PROFILE_ID = "PROFILE_ID";
    private int profileId;
    private RoundedImageView profileImage;
    int itemCountPerLoad = 0;
    private ListView listView;
    private ArrayList<GalleryItem> dataList;
    private GalleryGridAdapter listAdapter;
    protected View loadMoreFooterView;
    User user;
    int index = 0;
    private int MAX_CARDS = 2;
    PostsLoader loader;
    int loaderId;
    boolean loading;

    public static ProfileFragment newInstance(int param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(PROFILE_ID, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
    }

    @Override
    public void setTag() {
        TAG = "Profile_Fragment";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            profileId = getArguments().getInt(PROFILE_ID);
            if(app.getMe().getId() == profileId){
                myProfile = true;
                loaderId = Constants.MY_POSTS_LOADER_ID;
                user = app.getMe();
            }else{
                myProfile = false;
                user = app.getOther();
                loaderId = Constants.USER_POSTS_LOADER_ID;
            }
        }else{
            myProfile = true;
            user = app.getMe();
            loaderId = Constants.MY_POSTS_LOADER_ID;
        }
        profileId = user.getId();
        listAdapter = new GalleryGridAdapter(getActivity(), this, MAX_CARDS, true);
        useLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        Button followButton = (Button) rootView.findViewById(R.id.followButton);
        Button followingButton = (Button) rootView.findViewById(R.id.followingButton);
        Button followersButton = (Button) rootView.findViewById(R.id.followersButton);
        TextView nameTv = (TextView) rootView.findViewById(R.id.nameTv);
        TextView userNameTv = (TextView) rootView.findViewById(R.id.userNameTv);
        TextView glamCountTv = (TextView) rootView.findViewById(R.id.glamCountTv);
        profileImage = (RoundedImageView) rootView.findViewById(R.id.profileThumbnail);
        listView = (ListView) rootView.findViewById(R.id.uploadsList);
        listView.setOnScrollListener(new GridListOnScrollListener(this));
        if(myProfile){
            followButton.setVisibility(View.INVISIBLE);
        }

        if(dataList != null){
            listAdapter.addItemsInGrid(dataList);
            listView.setAdapter(listAdapter);
        }

        followersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity act = (ProfileActivity)getActivity();
                act.openFollowListScreen(true);
            }
        });

        followingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileActivity act = (ProfileActivity)getActivity();
                act.openFollowListScreen(false);
            }
        });

        String url = new StringBuilder(Constants.CLOUD_FRONT_URL).append("/100x100/").append(user.getPhotoPath()).toString();
        ImageLoader.getInstance().displayImage(url, profileImage, app.options);
        nameTv.setText(user.getFirstName() +" " + user.getLastName());
        userNameTv.setText(user.getUserName());
        glamCountTv.setText(Integer.toString(user.getGlamCount()).concat(" Glam"));
        loadMoreFooterView = getLoadMoreView(inflater);
        return rootView;
    }

    @Override
    public Loader<ArrayList<Post>> onCreateLoader(int id, Bundle args) {
        loader = new PostsLoader(getActivity().getApplicationContext(), id, profileId);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Post>> loader, ArrayList<Post> data) {
        Log.d(TAG, "ON LOAD FINISHED: " + data.size() + " - profileId: " + profileId);
        itemCountPerLoad = 0;
        if(dataList == null){
            index = 0;
            loadData(data);
            if(listView != null)
                listView.setAdapter(listAdapter);
            if(canLoadMoreData()){
                listView.addFooterView(loadMoreFooterView);
                loadMoreFooterView.setVisibility(View.VISIBLE);
            }
        }else{
            //Log.d(TAG, "LOAD MORE DATA TO THE ADAPTER: ");
            loadData(data);
            listAdapter.notifyDataSetChanged();
        }

        if(!canLoadMoreData()){
            if(listView != null) {
                listView.removeFooterView(loadMoreFooterView);
                loadMoreFooterView.setVisibility(View.INVISIBLE);
            }
        }
        loading = false;
    }

    private void loadData(ArrayList<Post> data){
        dataList = new ArrayList<GalleryItem>();
        for (Post post : data){
            //Log.d(TAG, "COVER PATH: " + post.getPhoto());
            //ImagePathTask task = new ImagePathTask("galleries/1419693538.203064jpg");
            //String path = new StringBuilder(Constants.CLOUD_FRONT_URL).append("/300x300/").append(post.getPhoto()).toString();
            GalleryItem galleryItem = new GalleryItem(index, post.getId(), "", post.getPhoto());
            Log.d(TAG, "DATA PHOTO URL: " + post.getPhoto());
            index ++;
            dataList.add(galleryItem);
        }
        itemCountPerLoad = data.size();
        listAdapter.addItemsInGrid(dataList);
        //addToGlobalLists(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Post>> loader) {
        dataList = null;
        index = 0;
        loading = false;
    }

    @Override
    public void reachedToEnd() {
        //Load More Data
        loadMoreFooterView.setVisibility(View.VISIBLE);
        Log.d(TAG, "ON REACHED TO END");
        useLoader();
    }

    public boolean canLoadMoreData() {
        if(loader == null)
            return true;
        return loader.perPage > itemCountPerLoad  ? false : true;//listData.size() < getMaxAllowedItems();
    }

    public void calculateLoadValues(){
        if(dataList == null){
            loader.startIndex = 0;
        }else{
            loader.startIndex += loader.perPage;
        }
    }

    @SuppressLint("InflateParams")
    protected View getLoadMoreView(LayoutInflater inflater) {
        ProgressBar loadMoreProgress = (ProgressBar) inflater.inflate(
                R.layout.auto_load_more_view, null);
        //loadMoreProgress.setBackgroundColor(Color.LTGRAY);
        return loadMoreProgress;
    }

    @Override
    public void postOnItemClicked(int postId, int postItemPosition) {

    }
    public void useLoader() {
        if (this.canLoadMoreData() && !loading) {
            Log.d(TAG, "USE LOADER FRAGMENT POPULAR POSTS");
            loading = true;
            if(loader == null ){
                loader = (PostsLoader) getActivity().getLoaderManager()
                        .initLoader(loaderId, null, this);
                loader.forceLoad();

            }else{
                Log.d(TAG, "USE LOADER FRAGMENT POPULAR POSTS - ON CONTENT CHANGEDD");
                //loader.startLoading(); //= (PopularPostsLoader) getActivity().getLoaderManager()
                // .restartLoader(Constants.POPULAR_POSTS_LOADER_ID, null, this);
                // loader.onContentChanged();
                loader.forceLoad();
            }
            calculateLoadValues();
        }else{
            listView.removeFooterView(loadMoreFooterView);
        }
    }
}