package com.mallardduckapps.fashiontalks;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.mallardduckapps.fashiontalks.R;
import com.mallardduckapps.fashiontalks.fragments.BasicFragment;
import com.mallardduckapps.fashiontalks.fragments.FollowFragment;
import com.mallardduckapps.fashiontalks.fragments.ProfileFragment;

public class ProfileActivity extends ActionBarActivity {

    Toolbar mainToolbar;
    FashionTalksApp app;
    ActionBar actionBar;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userId = getIntent().getIntExtra("PROFILE_ID", 0);
        mainToolbar = (Toolbar)findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        if (savedInstanceState == null) {
            ProfileFragment fragment = new ProfileFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("PROFILE_ID", userId);
            if(userId != 0){
                fragment.setArguments(bundle);
            }
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    public void openFollowListScreen(boolean followers){
        String name;
        if(followers){
            name = "followers/"+userId;
        }else{
            name = "following/"+userId;
        }
        FollowFragment fragment = FollowFragment.newInstance(userId, followers);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment).addToBackStack(name)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id == android.R.id.home){
            if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
                getSupportFragmentManager().popBackStack();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}