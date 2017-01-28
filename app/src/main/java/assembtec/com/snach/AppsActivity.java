package assembtec.com.snach;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 16.05.15.
 */
public class AppsActivity extends ActionBarActivity implements AppsSupportedFragment.OnAppAddedListener {
    // UI:
    private RelativeLayout rl_apps_main;
    private PagerSlidingTabStrip psts_apps_tabs;
    private ViewPager vp_apps;
    private int showPage = 0;

    private AppsActiveFragment activeAppsFragment;
    private AppsSupportedFragment supportedAppsFragment;

    // Device:
    private int deviceProfileID = 0;
    private DevicesHandler devicesHandler;

    // Drawer:
    private DrawerHandler mdHandler;
    private Toolbar toolbar;
    private FrameLayout fl_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.drawer_layout);
        RelativeLayout rl_content = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_apps, fl_content);
        fl_content = (FrameLayout) findViewById(R.id.content_frame);
        fl_content.addView(rl_content);

        toolbar = (Toolbar) findViewById(R.id.tb_apps);
        mdHandler = new DrawerHandler(this, toolbar, false, false, true, false);

        devicesHandler = new DevicesHandler(getApplicationContext());
        deviceProfileID = devicesHandler.getCurrentDeviceProfileID();

        Intent adderIntent = getIntent();
        if(adderIntent.getBooleanExtra(Globals.ADD_NEW_APP, false)){
            devicesHandler.addNewApp(adderIntent.getStringExtra(SnachExtras.INTENT_EXTRA_APP_NAME),
                    adderIntent.getStringExtra(SnachExtras.INTENT_EXTRA_APP_PACKAGE),
                    adderIntent.getStringExtra(SnachExtras.INTENT_EXTRA_APP_BC_ACTION),
                    adderIntent.getStringExtra(SnachExtras.INTENT_EXTRA_APP_BC_EXTRA));
        }

        initViewPager();
    }

    private void initViewPager() {
        List<Fragment> fragments = getFragments();

        showPage = 0;

        vp_apps = (ViewPager) findViewById(R.id.vp_apps);
        vp_apps.setAdapter(new PagerAdapter(getSupportFragmentManager(), fragments));

        psts_apps_tabs = (PagerSlidingTabStrip) findViewById(R.id.psts_apps_tabs);
        psts_apps_tabs.setShouldExpand(true);
        psts_apps_tabs.setViewPager(vp_apps);
        vp_apps.setCurrentItem(showPage);

        psts_apps_tabs.setBackgroundColor(getResources().getColor(R.color.main500));
        psts_apps_tabs.setUnderlineColor(getResources().getColor(R.color.transparent));
        psts_apps_tabs.setIndicatorColor(getResources().getColor(R.color.titlewhite));
        psts_apps_tabs.setDividerColor(getResources().getColor(R.color.main500));

        ViewPager.OnPageChangeListener pageListener = new PageListener();
        psts_apps_tabs.setOnPageChangeListener(pageListener);

        float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        psts_apps_tabs.setIndicatorHeight((int) dp);

        LinearLayout ll = (LinearLayout) psts_apps_tabs.getChildAt(0);
        for(int p=0; p < ll.getChildCount(); p++) {
            TextView tv = (TextView) ll.getChildAt(p);
            if (p == showPage) {
                tv.setTextColor(getResources().getColor(R.color.titlewhite));
            } else {
                tv.setTextColor(getResources().getColor(R.color.titlewhite_alpha));
            }
        }
    }

    @Override
    public void updateActiveAppList() {
        activeAppsFragment.updateActiveAppsList();
    }

    @Override
    public void addActiveApp(SnachAppItem snachAppItem) {
        devicesHandler.addNewApp(snachAppItem.getAppName(),
                snachAppItem.getAppPackage(),
                snachAppItem.getAppBCAction(),
                snachAppItem.getAppBCExtra());
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;
        public PagerAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            if (position == 0)
            {
                return getString(R.string.tab_apps_active_title);
            }
            if (position == 1)
            {
                return getString(R.string.tab_apps_supported_title);
            }
            return null;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }
    }

    private class PageListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int i) {
            LinearLayout ll = (LinearLayout) psts_apps_tabs.getChildAt(0);
            for(int p=0; p < ll.getChildCount(); p++){

                TextView tv = (TextView) ll.getChildAt(p);
                if(p == i){
                    tv.setTextColor(getResources().getColor(R.color.titlewhite));//darkgrey
                } else {
                    tv.setTextColor(getResources().getColor(R.color.titlewhite_alpha));//darkgreyalpha
                }
            }

            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            View focusedView = getCurrentFocus();
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }

    public List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>();

//        Bundle args = new Bundle();
//        args.putInt(Globals.KEY_DEVICE_ID, deviceProfileID);


        activeAppsFragment = new AppsActiveFragment();
//        activeAppsFragment.setArguments(args);
        fragments.add(activeAppsFragment);

        supportedAppsFragment = new AppsSupportedFragment();
//        supportedAppsFragment.setArguments(args);
        fragments.add(supportedAppsFragment);

        return fragments;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
