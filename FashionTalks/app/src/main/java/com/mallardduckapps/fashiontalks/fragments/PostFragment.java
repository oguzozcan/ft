package com.mallardduckapps.fashiontalks.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mallardduckapps.fashiontalks.BaseActivity;
import com.mallardduckapps.fashiontalks.FashionTalksApp;
import com.mallardduckapps.fashiontalks.GalleryActivity;
import com.mallardduckapps.fashiontalks.MainActivity;
import com.mallardduckapps.fashiontalks.PostActivity;
import com.mallardduckapps.fashiontalks.PostsActivity;
import com.mallardduckapps.fashiontalks.ProfileActivity;
import com.mallardduckapps.fashiontalks.R;
import com.mallardduckapps.fashiontalks.WebActivity;
import com.mallardduckapps.fashiontalks.components.ExpandablePanel;
import com.mallardduckapps.fashiontalks.components.ExpandablePanelWrapper;
import com.mallardduckapps.fashiontalks.loaders.PostLoader;
import com.mallardduckapps.fashiontalks.objects.Pivot;
import com.mallardduckapps.fashiontalks.objects.Post;
import com.mallardduckapps.fashiontalks.objects.Tag;
import com.mallardduckapps.fashiontalks.objects.User;
import com.mallardduckapps.fashiontalks.services.RestClient;
import com.mallardduckapps.fashiontalks.utils.Constants;
import com.mallardduckapps.fashiontalks.utils.FTUtils;
import com.mallardduckapps.fashiontalks.utils.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
//import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends BasicFragment implements LoaderManager.LoaderCallbacks<Post>{

    int postId;
    int postIndex = -1;
    int loaderId;
    int width;
    int height;
    RelativeLayout layout;
    RelativeLayout bottomBar;
    ProgressBar progressBarMain;
    RelativeLayout shareMenu;
    ImageView postPhoto;
    boolean shareMenuVisible = false;
    boolean ownPost;

    int finalImageWidth;
    int finalImageHeight;
    int imageTopMargin = 0;
    PostLoader loader;
    User user;

    TextView tvUserName;
    TextView tvName;
    TextView tvGlamCount;
    TextView tvChatText;
    TextView tvPostTime;

    ProgressBar progressBar;
    ImageButton shareButton;
    LinearLayout chatLayout;
    LinearLayout glamLayout;

    ViewSwitcher switcher;
    boolean openComment = false;
    public final static int DELETE_POST = 667;
    RoundedImageView thumbnailView;
    int glamCount;
    public static int commentCount;
    Post post;
    boolean loadingFailed;
    View rootView;
    LinearLayout shareFb;
    LinearLayout shareTwitter;
    LinearLayout shareInstagram;
    LinearLayout reportOrDelete;
    ImageView reportOrDeleteIcon;
    TextView reportOrDeleteText;
    ArrayList<ExpandablePanel> panels;

    public PostFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        width = PostsActivity.width;
        height = PostsActivity.height;
    }

    @Override
    public void setTag() {
        TAG = "Post_Fragment";
    }

    private void showNextView(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switcher.showNext();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.post_layout, container, false);
        switcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher);
        layout = (RelativeLayout) rootView.findViewById(R.id.mainPostLayout);
        bottomBar = (RelativeLayout) rootView.findViewById(R.id.bottomBar);
        thumbnailView = (RoundedImageView)rootView.findViewById(R.id.thumbnail);
        postPhoto = (ImageView)rootView.findViewById(R.id.postImage);
        tvUserName = (TextView) rootView.findViewById(R.id.userName);
        tvName = (TextView) rootView.findViewById(R.id.name);
        tvGlamCount = (TextView) rootView.findViewById(R.id.glamCount);
        tvChatText = (TextView) rootView.findViewById(R.id.chatText);
        tvPostTime = (TextView) rootView.findViewById(R.id.postTime);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        shareButton = (ImageButton) rootView.findViewById(R.id.shareButton);
        chatLayout = (LinearLayout) rootView.findViewById(R.id.chatLayout);
        glamLayout = (LinearLayout) rootView.findViewById(R.id.glamLayout);

        Activity activity = getActivity();
        if(activity != null){
            app = (FashionTalksApp)activity.getApplication();
            tvPostTime.setTypeface(FTUtils.loadFont(activity.getAssets(),activity.getString(R.string.font_helvatica_md)));//thin
            tvGlamCount.setTypeface(FTUtils.loadFont(activity.getAssets(),activity.getString(R.string.font_helvatica_md)));
            tvChatText.setTypeface(FTUtils.loadFont(activity.getAssets(),activity.getString(R.string.font_helvatica_lt)));
            tvUserName.setTypeface(FTUtils.loadFont(activity.getAssets(),activity.getString(R.string.font_helvatica_md)));
            tvName.setTypeface(FTUtils.loadFont(activity.getAssets(),activity.getString(R.string.font_helvatica_lt)));
        }

        shareMenu = (RelativeLayout) rootView.findViewById(R.id.shareMenuLayoutParent);
        shareFb = (LinearLayout) rootView.findViewById(R.id.facebookShareButton);
        shareTwitter = (LinearLayout) rootView.findViewById(R.id.twitterShareButton);
        shareInstagram = (LinearLayout) rootView.findViewById(R.id.instagramShareButton);
        reportOrDelete = (LinearLayout) rootView.findViewById(R.id.reportOrDeleteButton);
        reportOrDeleteIcon = (ImageView) rootView.findViewById(R.id.reportOrDeleteIcon);
        reportOrDeleteText = (TextView) rootView.findViewById(R.id.reportorDeleteText);

        progressBarMain = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        postId = getArguments().getInt("POST_ID");
        postIndex = getArguments().getInt("POST_INDEX");
        loaderId = getArguments().getInt("LOADER_ID");
        openComment = getArguments().getBoolean("OPEN_COMMENT", false);
        bottomBar.setVisibility(View.VISIBLE);
       // Log.d(TAG, "POST FR: POST ID: " + postId);

        if(app != null){
            Log.d(TAG, "APP NOT NULL " + loaderId);
            if(loaderId != Constants.USER_FAVORITE_POST_LOADER_ID && loaderId != Constants.NOTIFICATIONS_LOADER_ID){
                //post = getPost();
                //if(postJson != null){
                    post = getArguments().getParcelable("POST");//FTUtils.convertStringToPost(postJson);
               // }
            }

        }
        //openComment = post.getCanComment() == 1 ? true: false;
//        if(loaderId == Constants.MY_POSTS_LOADER_ID){
//            user = app.getMe();
//            ownPost = true;
//            //shareButton.setImageResource(R.drawable.delete_icon);
//        }else if(loaderId == Constants.USER_POSTS_LOADER_ID){
//            user = app.getOther();
//            ownPost = false;
//            //shareButton.setImageResource(R.drawable.report_icon);
//        }else if(loaderId == Constants.NOTIFICATION_MY_POST_LOADER_ID){
//            user = app.getMe();
//            ownPost = true;
//            //shareButton.setImageResource(R.drawable.delete_icon);
//        }
//        else if(loaderId == Constants.NOTIFICATION_OTHER_POST_LOADER_ID){
//            user = app.getOther();
//            ownPost = false;
//            //shareButton.setImageResource(R.drawable.report_icon);
//        }
//        else{
//            user = post.getUser();
//            ownPost = false;
//            //shareButton.setImageResource(R.drawable.report_icon);
//        }
        if(post != null){
            setPostUserDrawables();
            fillPost(post);
            //user = post.getUser();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "FILL POST POST NOT NULL");
                    switcher.setDisplayedChild(1);//showNext();
                }
            });
            //showNextView();
        }else{
            progressBar.setVisibility(View.VISIBLE);
            setReportLayout(false);
            user = app.getMe();
            ownPost = true;
            useLoader();
        }
        initShare();
        //Log.d(TAG, "ON CREATE VIEW HIDE THAT FUCKING KEYBOARD");
        //hide_keyboard_from(getActivity(), tvChatText);
        return rootView;
        //listView = (ListView) rootView.findViewById(R.id.galleryList);
    }

    private void setReportLayout(boolean report){
        if(report){
            reportOrDeleteIcon.setImageResource(R.drawable.red_report_icon);//report_icon);
            reportOrDeleteText.setText(getString(R.string.report));
        }else{
            reportOrDeleteIcon.setImageResource(R.drawable.delete_circle); // delete_icon
            reportOrDeleteText.setText(getString(R.string.delete));
        }
    }

    private void initShare(){
        shareFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shareMenuOnClick();
                shrinkAllPanels(panels);
                shareMenu.setVisibility(View.GONE);
                shareMenu.getChildAt(0).setVisibility(View.GONE);
                shareMenu.requestLayout();
                rootView.requestLayout();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap image = FTUtils.screenShot(rootView);
                        Log.d(TAG, "TAKE SCREEN SHOT");
                        SharePhoto photo = new SharePhoto.Builder()
                                .setBitmap(image)
                                .build();
                        if (ShareDialog.canShow(SharePhotoContent.class)) {
                            SharePhotoContent content = new SharePhotoContent.Builder()
                                    .addPhoto(photo)
                                    .build();
                            Log.d(TAG, "SHARE PHOTO");
                            ShareDialog dialog = new ShareDialog(PostFragment.this);
                            dialog.show(content);
                        }
                        shareMenu.setVisibility(View.VISIBLE);
                        shareMenu.getChildAt(0).setVisibility(View.VISIBLE);
                    }
                }, 450);

            }
        });

        shareTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // shareMenuOnClick();
                shrinkAllPanels(panels);
                shareMenu.setVisibility(View.GONE);
                shareMenu.getChildAt(0).setVisibility(View.GONE);
                shareMenu.requestLayout();
                rootView.requestLayout();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap image = FTUtils.screenShot(rootView);
                        Log.d(TAG, "TAKE SCREEN SHOT");
                        String text = getString(R.string.share_pic_from_twitter);
                        if (!ownPost) {
                            text = getString(R.string.share_others_pic_from_twitter);
                        }
                        TweetComposer.Builder builder = new TweetComposer.Builder(getActivity())
                                .text(text)
                                .image(FTUtils.getImageUri(getActivity(), image, "tmpImage"));
                        builder.show();
                        shareMenu.setVisibility(View.VISIBLE);
                        shareMenu.getChildAt(0).setVisibility(View.VISIBLE);
                    }
                }, 450);

            }
        });
        shareInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(FTUtils.instagramAppInstalledOrNot(getActivity())){
                    shrinkAllPanels(panels);
                    shareMenu.setVisibility(View.GONE);
                    shareMenu.getChildAt(0).setVisibility(View.GONE);
                    //shareMenuOnClick();
                    shareMenu.requestLayout();
                    rootView.requestLayout();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap image = FTUtils.screenShot(rootView);
                            Log.d(TAG, "TAKE SCREEN SHOT");
                            createInstagramIntent("image/*", FTUtils.getImageUri(getActivity(), image, "tmpImage"));
                            shareMenu.setVisibility(View.VISIBLE);
                            shareMenu.getChildAt(0).setVisibility(View.VISIBLE);
                        }
                    }, 450);

                }else{
                    final String text = getString(R.string.instagram_app_not_available);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void setPostUserDrawables(){
        if(app.isUserMe(post.getUser().getId())){
            setReportLayout(false);
            user = app.getMe();
            ownPost = true;
        }else{
            setReportLayout(true);
            user = post.getUser();
            ownPost = false;
        }
        glamCount = post.getGlamCount();
        commentCount = post.getCommentCount();
    }

    private void shareMenuOnClick(){
        Animation slide;
        if(shareMenuVisible){
            slide = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
            Log.d(TAG,"ON CLICK - slide down");
        }else{
            shareMenu.getChildAt(0).setVisibility(View.VISIBLE);
            shareMenu.setVisibility(View.VISIBLE);
            shareMenu.bringToFront();
            bottomBar.bringToFront();
            shareMenu.getParent().requestLayout();
            bottomBar.getParent().requestLayout();
            slide = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
            Log.d(TAG,"ON CLICK - slide up");
        }
        if(slide != null){
            slide.reset();
            if(shareMenu != null){
                //Log.d(TAG,"ON CLICK - start animation");
                shareMenu.clearAnimation();
                shareMenu.startAnimation(slide);
                shareMenuVisible = !shareMenuVisible;
            }
        }
    }

 /*   private Post getPost(){
        Post post = null;
        switch (loaderId){
            case Constants.FEED_POSTS_LOADER_ID:
                post = app.getFeedPostArrayList().get(postIndex);
                break;
            case Constants.POPULAR_POSTS_LOADER_ID:
                Log.d(TAG, "APP me: " + app.getMe().getUserName());
                //Log.d(TAG, "POPULAR LIST SIZE: " + app.getPopularPostArrayList().size());
                post = app.getPopularPostArrayList().get(postIndex);
                break;
            case Constants.GALLERY_POSTS_LOADER_ID:
                post = app.getGalleryPostArrayList().get(postIndex);
                break;
            case Constants.USER_POSTS_LOADER_ID:
                post = app.getUserPostArrayList().get(postIndex);
                break;
            case Constants.MY_POSTS_LOADER_ID:
                post = app.getMyPostArrayList().get(postIndex);
                break;
            case Constants.NOTIFICATIONS_LOADER_ID:
                post = null;
                break;
            case Constants.GALLERY_POSTS_BY_TAG_LOADER_ID:
                post = app.getBrandGalleryPostList().get(postIndex);
                break;
            case Constants.USER_FAVORITE_POST_LOADER_ID:
                Log.d(TAG, "USER FAVORITE POST");
                post = null;//app.getUserFavoritePost();
                break;
        }
        return post;
    }*/

    private void createInstagramIntent(String type, Uri uri){ // String mediaPath
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);
        // Set the MIME type
        share.setType(type);
        // Create the URI from the media
        //File media = new File(mediaPath);
        //Uri uri = Uri.fromFile(media);
        // Add the URI to the Intent.
        String text =getString(R.string.share_pic_from_twitter);
        if(!ownPost){
            text = getString(R.string.share_others_pic_from_twitter);
        }
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_TEXT, text);
        share.putExtra(Intent.EXTRA_TITLE, text);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setPackage("com.instagram.android");
        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }

  /*  private void setPost(Post post){
        switch (loaderId){
            case Constants.FEED_POSTS_LOADER_ID:
                app.getFeedPostArrayList().set(postIndex, post);
                break;
            case Constants.POPULAR_POSTS_LOADER_ID:
                app.getPopularPostArrayList().set(postIndex, post);
                break;
            case Constants.GALLERY_POSTS_LOADER_ID:
                app.getGalleryPostArrayList().set(postIndex, post);
                break;
            case Constants.USER_POSTS_LOADER_ID:
                app.getUserPostArrayList().set(postIndex, post);
                break;
            case Constants.MY_POSTS_LOADER_ID:
                app.getMyPostArrayList().set(postIndex, post);
                break;
            case Constants.GALLERY_POSTS_BY_TAG_LOADER_ID:
                app.getBrandGalleryPostList().set(postIndex, post);
                break;
            case Constants.USER_FAVORITE_POST_LOADER_ID:
                app.setUserFavoritePost(post);
                break;
        }
    }*/

    public void incrementGlamCount(final int newGlamCount){
        //int glamCount = post.getGlamCount();
        try{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "NEW GLAM COUNT : " + newGlamCount);
                    post.setGlamCount(newGlamCount);
                    tvGlamCount.setText(new StringBuilder(post.getGlamCountPattern()).append(getString(R.string.glam)).toString());
                    //TODO callback to activity to refresh post
                    //setPost(post);
                    tvGlamCount.invalidate();
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void incrementCommentCount(){
        int comment = post.getCommentCount();
        post.setCommentCount(comment++);
        if(tvChatText != null){
            tvChatText.setText(new StringBuilder(Integer.toString(post.getCommentCount())).append(getString(R.string.comment)).toString());
        }
        //TODO callback to activity to refresh post
        //setPost(post);
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.d(TAG, "LIFE TIME ON PAUSE");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        //Log.d(TAG, "LIFE TIME ON VIEW STATE RESTORED");
    }

    boolean attached = false;
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        attached = true;
        //Log.d(TAG, "LIFE TIME ON ATTACH " + commentCount);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        attached = false;
    }

    String path;

    private void fillPost(final Post post){
        //path = new StringBuilder(Constants.CLOUD_FRONT_URL).append("/").append(width).append("x").append(width).append("/").append(post.getPhoto()).toString();
        path = new StringBuilder(Constants.CLOUD_FRONT_URL_V2).append(post.getPhotoBig()).toString();
        //TODO change 40x40
        String thumbPath = new StringBuilder(Constants.CLOUD_FRONT_URL).append("/").append(100).append("x").append(100).append("/").append(user.getPhotoPath()).toString();
        tvUserName.setText(post.getUser().getUserName());
        String tvNameTxt = post.getTitle();
        try{
            tvName.setText(StringEscapeUtils.unescapeJson(post.getTitle()));
        }catch(Exception e){
            e.printStackTrace();
            tvName.setText(tvNameTxt);
        }

        tvGlamCount.setText(new StringBuilder(post.getGlamCountPattern()).append(getString(R.string.glam)).toString());
        //Log.d(TAG, "POST CREATED AT: " + post.getCreatedAt());
        tvPostTime.setText(TimeUtil.compareDateWithToday(post.getCreatedAt(), getResources()));
        glamLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlammersFragment fragment = GlammersFragment.newInstance(Integer.toString(postId));
                FragmentTransaction fragmentTx = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTx.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left, R.anim.enter_from_left, R.anim.exit_from_right);
                fragmentTx.replace(R.id.container, fragment).addToBackStack(fragment.getTag())
                        .commit();
            }
        });
        tvChatText.setText(new StringBuilder(Integer.toString(post.getCommentCount())).append(getString(R.string.comment)).toString());
        chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(post.getCommentCount() != 0){
                if(post.getCanComment() == 0){
                    return;
                }
                //Bundle bundle = new Bundle();
                //bundle.putString("POST_ID", Integer.toString(postId));
                //bundle.putInt("POST_LOADER_ID", loaderId);
                CommentsFragment fragment = CommentsFragment.newInstance(Integer.toString(postId), loaderId, postIndex, ownPost);
                FragmentTransaction fragmentTx = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTx.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left, R.anim.enter_from_left, R.anim.exit_from_right);
                        fragmentTx.replace(R.id.container, fragment).addToBackStack(fragment.getTag())
                        .commit();
                //}
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                app.setOther(user);
                intent.putExtra("PROFILE_ID", user.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                BaseActivity.setTranslateAnimation(getActivity());
                //getActivity().overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
            }
        };
        tvUserName.setOnClickListener(onClickListener);
        thumbnailView.setOnClickListener(onClickListener);
        tvName.setOnClickListener(onClickListener);
        ImageLoader.getInstance().displayImage(thumbPath, thumbnailView,app.options);
        finalImageWidth = 0;

        ImageLoader.getInstance()
                .displayImage(path, postPhoto, app.options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        final ViewTreeObserver vto = postPhoto.getViewTreeObserver();
                        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                            public boolean onPreDraw() {
                                if(finalImageWidth == 0){
                                    finalImageHeight = postPhoto.getHeight();
                                    finalImageWidth = postPhoto.getWidth();
                                    imageTopMargin = (int)postPhoto.getY();
                                    //Log.e("IMAGE","(GLAM) Image Height: " + finalImageHeight + " Width: " + finalImageWidth + " - imageTopMargin : " + imageTopMargin);
                                        setGlamPosition(post);

                                }
                                return true;
                            }
                        });
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progressBar.setVisibility(View.GONE);
                        loadingFailed = true;
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressBar.setVisibility(View.GONE);
                        loadingFailed = false;
                    }
                });
        //Log.d(TAG, "POST FR: SELECTeD POST ID: " + post.getTag());
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG,"ON CLICK");
                // Share Menu is not active now
                shareMenuOnClick();

            }
        });

        reportOrDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportOrDeletePost();
            }
        });
    }

    private void reportOrDeletePost(){
        if (user != app.getMe()) {
            // REPORT
            Bundle bundle = new Bundle();
            bundle.putString("IMAGE_PATH", createImageFile());
            ReportDialog dialog = new ReportDialog();
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "ReportDialog");
            //FTUtils.sendMail(getString(R.string.email_send_report_content), getString(R.string.email_send_report_recipient), getString(R.string.email_send_report_subject), getActivity());
        } else {
            //DELETE
            app.openErasePicDialog(PostFragment.this.getActivity(), PostFragment.this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_home) {
            Log.d(TAG, "ACTION HOME: ownPost" + ownPost);
//            if (!ownPost) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
                BaseActivity.setBackwardsTranslateAnimation(getActivity());
                return true;
//            } else {
//                ClassificationDialog dialog = new ClassificationDialog();
//                Bundle bundle = new Bundle();
//                bundle.putInt("DIALOG_NO", ClassificationDialog.GET_MORE_GLAMS_DIALOG);
//                dialog.setArguments(bundle);
//                dialog.show(getActivity().getSupportFragmentManager(), "ClassificationDialog");
//                return true;
//            }
        }
        if (item.getItemId() == R.id.action_user_info){
            Log.d(TAG, "ACTION action_user_info: ownPost" + ownPost);
            ClassificationDialog dialog = new ClassificationDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("DIALOG_NO", ClassificationDialog.GET_MORE_GLAMS_DIALOG);
            dialog.setArguments(bundle);
            dialog.show(getActivity().getSupportFragmentManager(), "ClassificationDialog");
            return true;
        }
        return false;
    }

    private String createImageFile(){
        String TEMP_PHOTO_FILE_NAME = "report_photo.png";
        File mFileTemp;
        String state = Environment.getExternalStorageState();
        Bitmap bitmap = ((BitmapDrawable)postPhoto.getDrawable()).getBitmap();
       // Log.d(TAG, "BITMAP : " + bitmap.toString());
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        }
        else {
            mFileTemp = new File(getActivity().getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(mFileTemp);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "IMAGE PATH TO REPORT: " + mFileTemp.getPath());
        return mFileTemp.getAbsolutePath();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DELETE_POST && resultCode == 1){
            DeleteTask task = new DeleteTask();
            task.execute(Integer.toString(postId));
        }
    }

    private void setGlamPosition(Post post){
        int tagListSize = 0;
        if(post.getTags() != null){
            tagListSize = post.getTags().size();
        }

        panels = new ArrayList<>(tagListSize);
        boolean adPost = post.getIsAd() == 1 ? true : false;
        if(adPost){
            sendEventToGoogleAnalytics(Integer.toString(post.getId()), false);
        }

        for (Tag tag : post.getTags()){
            final Pivot pivot = tag.getPivot();
            int x = getRealX(pivot.getX());
            int y = getRealY(pivot.getY());
            int tagId = tag.getId();
            String brandName = tag.getTag();
            final ExpandablePanelWrapper panelWrapper = new ExpandablePanelWrapper(app, getActivity(), pivot, x, y, x > PostsActivity.width/2 ? false:true, ownPost, false, adPost);
            final ExpandablePanel panel = panelWrapper.getGlam();
           // final ExpandablePanel panel = new ExpandablePanel(app,getActivity(), pivot, x , y, x > PostsActivity.width/2 ? false:true, ownPost, false, adPost);
            panel.setTagId(tagId);
            panel.setBrandName(brandName);
            if (adPost){
                panel.setAdUrl(pivot.getAdUrl());
            }
            if(panel.isLhsAnimation()){
                panel.setTagText(new StringBuilder(pivot.getGlamCountPattern()).append(" | ").append(tag.getTag()).append(" ").toString() ,false);
            }else{
                panel.setTagText(new StringBuilder(tag.getTag()).append(" | ").append(pivot.getGlamCountPattern()).toString() ,false);
            }
            //panel.setTypeface(FTUtils.loadFont(getActivity().getAssets(), getActivity().getString(R.string.font_helvatica_lt)));
            //Log.d(TAG, "GLAM X: " + x + " - GLAM Y: " + y);
            panel.setOnExpandListener(new ExpandablePanel.OnExpandListener() {
                @Override
                public void onExpand(View handle) {
                }

                @Override
                public void onCollapse(View handle, int tagId, String brandName) {
                    if(!panel.isAdPost()){
                        Intent intent = new Intent(getActivity(), GalleryActivity.class);
                        intent.putExtra("GALLERY_ID", tagId);
                        intent.putExtra("GALLERY_NAME", brandName);
                        intent.putExtra("LOADER_ID", Constants.GALLERY_POSTS_BY_TAG_LOADER_ID);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        PostFragment.this.getActivity().startActivity(intent);
                        BaseActivity.setTranslateAnimation(getActivity());
                    }else{
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra("URL", panel.getAdUrl());
                        startActivity(intent);
                        BaseActivity.setTranslateAnimation(getActivity());
                        sendEventToGoogleAnalytics(Integer.toString(panel.getTagId()), true);
                    }
                }

                @Override
                public void onTagGlammed(int glamCount, int totalGlamCount) {
                    Log.d(TAG, "ON GLAM COUNT INCREASED"+totalGlamCount);
                    incrementGlamCount(totalGlamCount);
                }
            });
           // if(ownPost){
                //panel.animateExpand();
           // }
            panels.add(panel);
            layout.addView(panelWrapper);
        }

        postPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "ON CLICK TO LAYOUT SHRINK ALL ANIMS");
                if(loadingFailed){
                    ImageLoader.getInstance()
                            .displayImage(path, postPhoto, app.options);
                    return;
                }

                shrinkAllPanels(panels);
            }
        });

        shareMenu.bringToFront();
        bottomBar.bringToFront();
    }

    private void shrinkAllPanels(ArrayList<ExpandablePanel> panels){
        for (ExpandablePanel panel : panels) {
            if (panel.mExpanded) {
                panel.runShrinkAnimation(true);
            }
        }
    }

    private int getRealX(int x){
        return width*x/Constants.VIRTUAL_WIDTH;
    }

    //TODO it was height before, control the height
    private int getRealY(int y){
        return finalImageWidth*y/Constants.VIRTUAL_HEIGHT + imageTopMargin + (finalImageHeight - finalImageWidth)/2;
    }

    private void sendEventToGoogleAnalytics(String text, boolean glamClicked){
        if(glamClicked){
            app.sendAnalyticsEvent("Reklam Postu Glam Dot", "UX", "DOT_ID", text);
        }else{
            app.sendAnalyticsEvent("Reklam Postu", "UX", "AD_ID", text);
        }
    }

    @Override
    public Loader<Post> onCreateLoader(int id, Bundle args) {
        loader = new PostLoader(getActivity().getApplicationContext(), Constants.NOTIFICATIONS_LOADER_ID, postId);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Post> loader, Post data) {
        if(data != null){
            if(data.isInvalid()){
                Toast.makeText(PostFragment.this.getActivity().getApplicationContext(), getString(R.string.invalid_post_alert), Toast.LENGTH_SHORT).show();
                getActivity().finish();
                BaseActivity.setBackwardsTranslateAnimation(PostFragment.this.getActivity());
                return;
            }
            post = data;
            setPostUserDrawables();
            progressBar.setVisibility(View.GONE);
            fillPost(data);
            if(openComment){
                chatLayout.performClick();
            }else{
                showNextView();
            }
        }
        else{
            Log.d(TAG, "DATA IS NULL");
            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    app.openOKDialog(PostFragment.this.getActivity(), PostFragment.this, "no_connection");
                }
            });

            return;
        }
    }

    @Override
    public void onLoaderReset(Loader<Post> loader) {
    }

    private void useLoader() {
        if (loader == null) {
            Log.d(TAG, "USE LOADER");
            loader = (PostLoader) getActivity().getLoaderManager()
                    .initLoader(loaderId, null, this);
            loader.forceLoad();
        }
    }

    public class DeleteTask extends AsyncTask<String, Void, String> {

        private final String TAG = "SendPhotoTask";
        private int status = -1;

        public DeleteTask(){
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBarMain.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            RestClient restClient = new RestClient();
            try {
                String url = new StringBuilder(Constants.POST_DELETE).append(params[0]).toString();
                response = restClient.doGetRequest(url, null);
                Log.d(TAG, "User REQUEST RESPONSE: " + response);
                JSONObject object = new JSONObject(response);
                status = object.getInt("status");

            } catch (Exception e) {
                response = "NO_CONNECTION";
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String text) {
            super.onPostExecute(text);
            progressBarMain.setVisibility(View.GONE);
            if(status == 0){
                ProfileActivity.imageGalleryChanged = true;
                try{
                    getActivity().finish();
                    BaseActivity.setBackwardsTranslateAnimation(getActivity());
                }catch(Exception e){
                    e.printStackTrace();
                    //if(attached)
                        //Toast.makeText(PostFragment.this.getActivity(),getString(R.string.problem_occured), Toast.LENGTH_SHORT).show();
                }

            }else{
                if(attached)
                    Toast.makeText(PostFragment.this.getActivity(),getString(R.string.problem_occured), Toast.LENGTH_SHORT).show();
            }

        }
    }
}
