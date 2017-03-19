package com.riseapps.xmusic.view.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.claudiodegio.msv.BaseMaterialSearchView;
import com.riseapps.xmusic.R;

/**
 * Created by kanishk on 17/03/17.
 */

public abstract class BaseMatSearchViewActivity extends AppCompatActivity {

    Toolbar mToolbar;
    BaseMaterialSearchView mSearchView;
    CoordinatorLayout mCoordinator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.main_menu);
        mSearchView = (BaseMaterialSearchView) findViewById(R.id.sv);
        mCoordinator = (CoordinatorLayout) findViewById(R.id.drawerLayout);
        mSearchView.setMenuItem(mToolbar.getMenu().findItem(R.id.action_search));

        /*mSearchView.postDelayed(new Runnable() {
            @Override
            public void run() {
                initCustom();
            }
        },1000);*/

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initCustom();
            }
        }, 4000);
        //;
    }

    public int getLayoutId() {
        return R.layout.search_simple;
    }

    protected void initCustom() {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mSearchView.setMenuItem(menu.findItem(R.id.action_search));
        return true;
    }

}