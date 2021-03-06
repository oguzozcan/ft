package com.mallardduckapps.fashiontalks.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.mallardduckapps.fashiontalks.services.RestClient;
import com.mallardduckapps.fashiontalks.utils.Constants;

import org.json.JSONObject;

/**
 * Created by oguzemreozcan on 03/02/15.
 */
public class BlockTask extends AsyncTask<Void, Void, String> {

    private final boolean block;
    private final int userId;
    private final String TAG = "Block_TASK";
    private final IsBlocked callback;

    public BlockTask(IsBlocked callback, boolean block, int userId) {
        this.block = block;
        this.userId = userId;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... params) {
        String response = "";
        RestClient restClient = new RestClient();
        try {
            String url = null;
            if (block) {
                url = new StringBuilder(Constants.BLOCK_USER_PREFIX).append(userId).toString();
            } else {
                url = new StringBuilder(Constants.UNBLOCK_USER_PREFIX).append(userId).toString();
            }
            response = restClient.doGetRequest(url, null);
            JSONObject object = new JSONObject(response);
            int status = object.getInt("status");
            if(status != 0){
                callback.isUserBlocked(false, block);
            }else{
                callback.isUserBlocked(true, block);
            }
            Log.d(TAG, "RESPONSE FROM API: " + response);
        } catch (Exception e) {
            response = "NO_CONNECTION";
            e.printStackTrace();
        }
        return response;
    }

    public interface IsBlocked{
        void isUserBlocked(boolean success,boolean blocked);
    }
}
