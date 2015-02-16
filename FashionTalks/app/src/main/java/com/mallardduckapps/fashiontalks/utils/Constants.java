package com.mallardduckapps.fashiontalks.utils;

/**
 * Created by oguzemreozcan on 10/01/15.
 */
public class Constants {

    public final static int NO_CONNECTION = -1;
    public final static int AUTHENTICATION_FAILED = 0;
    public final static int AUTHENTICATION_CANCELED = 1;
    public final static int AUTHENTICATION_SUCCESSFUL = 2;
    public final static int WRONG_CREDENTIALS = 3;

    public final static int TOOLBAR_WITH_TITLE = 0;
    public final static int TOOLBAR_WITH_BACK_BUTTON = 1;
    public final static int TOOLBAR_WITH_BACK_AND_EXIT_BUTTON = 2;

    public final static String API_ADDRESS = "http://api.ft-api.com";
    public final static String LOGIN_PREFIX = "/users/login";
    public final static String GET_USER_PREFIX = "/members/profile";
    public final static String REGISTER_PREFIX = "/users/register";
    public final static String REGISTER_GCM_TOKEN = "/members/android-token";
    public final static String TEST_NOTIFICATIONS = "/members/test-push-notification";
    public final static String GALLERIES_PREFIX = "/posts/galleries";
    public final static String GALLERY_POSTS_PREFIX = "/posts/gallery-posts";
    public final static String CATEGORY_POSTS_PREFIX ="";
    public final static String POPULAR_PREFIX = "/posts/popular";
    public final static String FEED_PREFIX = "/posts/feed";
    public final static String POSTS_BY_USER_PREFIX = "/posts/user/";
    public final static String POST_COMMENTS = "/comments/comments/";
    public final static String GLAMMER_LIST_PREFIX= "/glam/glammers/";
    public final static String GLAM_TAG_PREFIX = "/glam/tag/";
    public final static String FOLLOW_USER_PREFIX = "/members/follow/";
    public final static String UNFOLLOW_USER_PREFIX = "/members/unfollow/";
    public final static String FOLLOWERS_PREFIX = "/members/followers/";
    public final static String FOLLOWING_PREFIX = "/members/following/";
    public final static String BLOCK_USER_PREFIX = "/members/block/";
    public final static String UNBLOCK_USER_PREFIX = "/members/unblock/";
    public final static String POPULAR_USERS_PREFIX = "/members/popular";
    public final static String NOTIFICATION_LIST_PREFIX ="/notifications/list";
    public final static String POSTS_BASE_URL = "http://api.ft-api.com/posts/galleries/";
    public final static String CLOUD_FRONT_URL = "http://d3lhyn1u5tugzg.cloudfront.net";

    public final static String CLIENT_ID = "2";
    public final static String CLIENT_SECRET = "ZtqYh3hkF6v=mn";

    public final static String SENDER_ID = "337533113430";

    public final static int FOLLOWERS_LOADER_ID = 12;
    public final static int FOLLOWING_LOADER_ID = 13;
    public final static int MY_POSTS_LOADER_ID = 10;
    public final static int USER_POSTS_LOADER_ID = 11;
    public final static int COMMENTS_LOADER_ID = 8;
    public final static int NOTICATIONS_LOADER_ID = 7;
    public final static int POPULAR_USERS_LOADER_ID = 6;
    public final static int GLAMMERS_LOADER_ID = 5;
    public final static int GALLERY_POSTS_LOADER_ID = 4;
    public final static int GALLERIES_LOADER_ID = 3;
    public final static int POPULAR_POSTS_LOADER_ID = 2;
    public final static int FEED_POSTS_LOADER_ID = 1;

}