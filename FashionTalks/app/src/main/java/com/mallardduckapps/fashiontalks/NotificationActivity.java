package com.mallardduckapps.fashiontalks;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mallardduckapps.fashiontalks.fragments.BasicFragment;
import com.mallardduckapps.fashiontalks.fragments.NotificationsFragment;
import com.mallardduckapps.fashiontalks.utils.FTUtils;

public class NotificationActivity extends BaseActivity implements BasicFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);// false
        tabToolbar.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        TextView tvName = (TextView) findViewById(R.id.toolbarName);
        tvName.setTypeface(FTUtils.loadFont(getAssets(), getString(R.string.font_avantgarde_bold)));
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams)mainLayout.getLayoutParams();
        param.topMargin = 0;
        topDivider.setVisibility(View.VISIBLE);
        //actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.hamburger_menu);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new NotificationsFragment())
                .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, String actionName) {
        super.onNavigationDrawerItemSelected(position, actionName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            menu.toggle();
        }
        return true;
    }

    @Override
    public void onFragmentInteraction(String tag) {

    }

    @Override
    public void onToolbarThemeChange(int themeId) {

    }

}
