package com.mallardduckapps.fashiontalks.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mallardduckapps.fashiontalks.FashionTalksApp;
import com.mallardduckapps.fashiontalks.objects.Gallery;
import com.mallardduckapps.fashiontalks.services.RestClient;
import com.mallardduckapps.fashiontalks.utils.Constants;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 17/01/15.
 */
public class GalleriesLoader extends AsyncTaskLoader<ArrayList<Gallery>> {

    final String TAG = "GALLERIES_LOADER";
    int loaderId;
    ArrayList<Gallery> galleryItems;
    //	public static int pageIndex = 1;
    //public static int perPage = 25;
    //public static boolean noMoreData = false;
    boolean loadingInProgress;
    FashionTalksApp app;

    public GalleriesLoader(Context context, FashionTalksApp app, int loaderId) {
        super(context);
        this.loaderId = loaderId;
        this.app = app;
    }

    @Override
    public ArrayList<Gallery> loadInBackground() {
        String response = "";
        RestClient restClient = new RestClient();
        galleryItems = new ArrayList<Gallery>();
        try {
            String token = null;
            if(app != null){
                token = app.dataSaver.getString(Constants.ACCESS_TOKEN_KEY);
            }
            response = restClient.doGetRequest(Constants.GALLERIES_PREFIX,token, null);
            Log.d(TAG, "RESPONSE FROM API: " + response);
        } catch (Exception e) {
            response = "NO_CONNECTION";
            e.printStackTrace();
            return null;
        }
        JsonObject object = null;
        try{
            object = new JsonParser().parse(response).getAsJsonObject();
        }catch(Exception e){
            return null;
        }

        //Gson gson = new GsonBuilder().create();
        Exclude ex = new Exclude();
        Gson gson = new GsonBuilder().addDeserializationExclusionStrategy(ex).addSerializationExclusionStrategy(ex).create();
        JsonArray dataObjects = object.getAsJsonArray("data");
        for (JsonElement item : dataObjects) {
            Gallery gallery = gson.fromJson(item, Gallery.class);
            //Log.d(TAG, "read cover path: " + gallery.getCover());
            galleryItems.add(gallery);
        }
        return galleryItems;
    }

    @Override
    protected void onStartLoading() {
        loadingInProgress = true;
        if (galleryItems != null) {
            if (galleryItems.size() != 0) { // && !refreshData
                // Deliver any previously loaded data immediately.
                deliverResult(galleryItems);
            }
        }
        if (takeContentChanged() || galleryItems == null) {
            // When the observer detects a change, it should call
            // onContentChanged()
            // on the Loader, which will cause the next call to
            // takeContentChanged()
            // to return true. If this is ever the case (or if the current data
            // is
            // null), we force a new load.
            forceLoad();
        }
    }
}
