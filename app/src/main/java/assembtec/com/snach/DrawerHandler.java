package assembtec.com.snach;

/**
 Copyright (C) 2015 Giorgio Groß - All rights reserved.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT.
 BY NO MEANS SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY DAMAGES,
 CLAIM OR OTHER LIABILITY, WHETHER IN AN ACTION OF TORT, CONTRACT
 OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class DrawerHandler implements DrawerItemClickListener.OnDrawerItemSelectedListener {
    private Activity activity;

    // Device Profiles:
    private DevicesHandler devicesHandler;
    private String currentDeviceProfileName = "";
    private int currentDeviceProfileID = 0;

    // Drawer:
    private Toolbar toolbar;

    private ArrayList<String> drawerItems;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerListAdapter listAdapter;
    private ListView mDrawerList;
    private String[] foldersName;
    private FrameLayout fl_content;

    private boolean doActivateBluetooth = false;
    private boolean doActivateGestures = false;
    private boolean doActivateApps = false;
    private boolean doActivateDevices = false;
    private boolean selectBluetooth = false;
    private boolean selectGesture = false;
    private boolean selectApps = false;
    private boolean selectDevices = false;

    public DrawerHandler(Activity activity, Toolbar toolbar, boolean doActivateBluetooth, boolean doActivateGestures, boolean doActivateApps, boolean doActivateDevices) throws NullPointerException {
        this.activity = activity;
        this.toolbar = toolbar;

        getCurrentDevice();

        initializeDrawer();
        this.doActivateBluetooth = doActivateBluetooth;
        this.doActivateGestures = doActivateGestures;
        this.doActivateApps = doActivateApps;
        this.doActivateDevices = doActivateDevices;

        if(!doActivateBluetooth && !doActivateGestures && !doActivateApps && doActivateDevices){
            activateItemAtPosition(1);
        } else if(doActivateBluetooth && !doActivateGestures && !doActivateApps && !doActivateDevices){
            activateItemAtPosition(2);
        } else if(!doActivateBluetooth && doActivateGestures && !doActivateApps && !doActivateDevices){
            activateItemAtPosition(3);
        } else if(!doActivateBluetooth && !doActivateGestures && doActivateApps && !doActivateDevices){
            activateItemAtPosition(4);
        }
    }

    private void initializeDrawer(){
        if(activity != null) {
            mDrawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
            mDrawerList = (ListView) activity.findViewById(R.id.left_drawer);

            setUp();

            mDrawerList.setOnItemClickListener(new DrawerItemClickListener(this, mDrawerList, mDrawerLayout, activity));

            mDrawerToggle = new ActionBarDrawerToggle(activity, mDrawerLayout,
                    toolbar, R.string.drawer_open, R.string.drawer_open) {

                /**
                 * Called when a drawer has settled in a completely closed state.
                 */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                }

                /**
                 * Called when a drawer has settled in a completely open state.
                 */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }
            };
            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }
    }

    public void reset(){
        activity = null;
    }

    public void revive(Activity a){
        activity = a;
    }

    public void updateDrawer(){
        getCurrentDevice();
        listAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void onDrawerItemSelected(int position) {
    }

    @Override
    public void onStartBluetoothActivity() {
        if(activity != null && !doActivateBluetooth) {
            Intent theNextIntent = new Intent(activity.getApplicationContext(), BluetoothActivity.class);
            activity.startActivity(theNextIntent);
            activity.overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.fadeout);
            activity.finish();
        } else {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onStartGesturesActivity() {
        if(activity != null && !doActivateGestures) {
            Intent theNextIntent = new Intent(activity.getApplicationContext(), GesturesActivity.class);
            activity.startActivity(theNextIntent);
            activity.overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.fadeout);
            activity.finish();
        } else {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onStartAppsActivity() {
        if(activity != null && !doActivateApps) {
            Intent theNextIntent = new Intent(activity.getApplicationContext(), AppsActivity.class);
            activity.startActivity(theNextIntent);
            activity.overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.fadeout);
            activity.finish();
        } else {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onStartDevicesActivity() {
        if(activity != null && !doActivateDevices) {
            Intent theNextIntent = new Intent(activity.getApplicationContext(), DeviceProfilesActivity.class);
            activity.startActivity(theNextIntent);
            activity.overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.fadeout);
            activity.finish();
        } else {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public boolean drawerIsOpen() {
        return mDrawerLayout.isDrawerOpen(Gravity.LEFT);
    }

    public void setUp() {
        initializeItemList();
        listAdapter = new DrawerListAdapter(drawerItems);
        mDrawerList.setAdapter(listAdapter);
    }

    private void initializeItemList() {
        drawerItems = new ArrayList<String>();
        drawerItems.add(activity.getResources().getString(R.string.app_name));
        drawerItems.add(activity.getResources().getString(R.string.devices_title));
        drawerItems.add(activity.getResources().getString(R.string.bluetooth_title));
        drawerItems.add(activity.getResources().getString(R.string.gesture_title));
        drawerItems.add(activity.getResources().getString(R.string.apps_title));
    }

    public void activateItemAtPosition(int position) {
        /**
         * position: index in viewHolder; [-> total Position in drawer]
         */
        if (position >= 0) {
            switch (position){
                case 1:
                    // Devices:
                    selectBluetooth = false;
                    selectGesture = false;
                    selectApps = false;
                    selectDevices = true;
                    break;
                case 2:
                    // Bluetooth
                    selectBluetooth = true;
                    selectGesture = false;
                    selectApps = false;
                    selectDevices = false;
                    break;
                case 3:
                    // Gestures:
                    selectBluetooth = false;
                    selectGesture=true;
                    selectApps = false;
                    selectDevices = false;
                    break;
                case 4:
                    // Apps:
                    selectBluetooth = false;
                    selectGesture = false;
                    selectApps = true;
                    selectDevices = false;
                    break;
            }
        }
    }

    public void deactivateItemAtPosition(int position) {
        /**
         * position: index in viewHolder; [-> total Position in drawer]
         */
        if (position >= 0) {
            switch (position){
                case 1:
                    // Devices:
                    listAdapter.unselectDevices();
                    break;
                case 2:
                    // Bluetooth
                    listAdapter.unselectBluetooth();
                    break;
                case 3:
                    // Gestures:
                    listAdapter.unselectGestures();
                    break;
                case 4:
                    // Apps:
                    listAdapter.unselectApps();
                    break;

            }
        }
    }

    public void getCurrentDevice() {
        devicesHandler = new DevicesHandler(activity.getApplicationContext());
        currentDeviceProfileID = devicesHandler.getCurrentDeviceProfileID();
        currentDeviceProfileName = devicesHandler.getCurrentDeviceName();
    }

    private class ViewHolder {
        protected RelativeLayout rl_base;
        protected TextView tv_title;
        protected ImageView iv_icon;
        protected CustomDevicesProfilesSpinnerAdapter spinnerAdapter;
    }

    private class DrawerListAdapter extends BaseAdapter {
        ArrayList<String> drawerItems;
        ArrayList<ViewHolder> viewHolders;

        DrawerListAdapter (ArrayList<String> drawerItems){
            this.drawerItems = drawerItems;
            viewHolders = new ArrayList<ViewHolder>();
        }

        @Override
        public int getCount() {
            return drawerItems.size();
        }

        @Override
        public Object getItem(int position) {
            return drawerItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            switch (position){
                case 0:
                    return 0;
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    return 3;
                case 4:
                    return 4;
            }
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 5;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int viewType = getItemViewType(position);
            if(convertView == null){
                ViewHolder holder = new ViewHolder();
                switch (viewType){
                    case 0:
                        convertView = activity.getLayoutInflater().inflate(R.layout.drawer_header, parent, false);
                        holder.rl_base = (RelativeLayout) convertView.findViewById(R.id.rl_drawerheader_base);
                        holder.tv_title = (TextView) convertView.findViewById(R.id.tv_currentProfileName);
                        convertView.setTag(holder);
                        viewHolders.add(holder);
                        break;
                    case 1:
                        convertView = activity.getLayoutInflater().inflate(R.layout.drawer_devices, parent, false);
                        holder.rl_base = (RelativeLayout) convertView.findViewById(R.id.rl_drawerdevices_base);
                        holder.tv_title = (TextView) convertView.findViewById(R.id.tv_toDevices);
                        holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_devices);
                        convertView.setTag(holder);
                        viewHolders.add(holder);
                        break;
                    case 2:
                        convertView = activity.getLayoutInflater().inflate(R.layout.drawer_bluetooth, parent, false);
                        holder.rl_base = (RelativeLayout) convertView.findViewById(R.id.rl_drawerbluetooth_base);
                        holder.tv_title = (TextView) convertView.findViewById(R.id.tv_toBluetooth);
                        holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_bluetooth);
                        convertView.setTag(holder);
                        viewHolders.add(holder);
                        break;
                    case 3:
                        convertView = activity.getLayoutInflater().inflate(R.layout.drawer_gesture, parent, false);
                        holder.rl_base = (RelativeLayout) convertView.findViewById(R.id.rl_drawergesture_base);
                        holder.tv_title = (TextView) convertView.findViewById(R.id.tv_toGesture);
                        holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_gesture);
                        convertView.setTag(holder);
                        viewHolders.add(holder);
                        break;
                    case 4:
                        convertView = activity.getLayoutInflater().inflate(R.layout.drawer_apps, parent, false);
                        holder.rl_base = (RelativeLayout) convertView.findViewById(R.id.rl_drawerapps_base);
                        holder.tv_title = (TextView) convertView.findViewById(R.id.tv_toApps);
                        holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_apps);
                        convertView.setTag(holder);
                        viewHolders.add(holder);
                        break;
                }
            }

            final ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            switch (viewType){
                case 0:
                    viewHolder.tv_title.setText(currentDeviceProfileName);
                    break;
                case 1:
                    if(selectDevices) {
                        viewHolder.rl_base.setBackgroundColor(activity.getResources().getColor(R.color.lightgrey));
                    } else {
                        viewHolder.rl_base.setBackgroundResource(R.drawable.drawer_selector);
                    }
                    break;
                case 2:
                    if(selectBluetooth) {
                        viewHolder.rl_base.setBackgroundColor(activity.getResources().getColor(R.color.lightgrey));
                    } else {
                        viewHolder.rl_base.setBackgroundResource(R.drawable.drawer_selector);
                    }
                    break;
                case 3:
                    if(selectGesture) {
                        viewHolder.rl_base.setBackgroundColor(activity.getResources().getColor(R.color.lightgrey));
                    } else {
                        viewHolder.rl_base.setBackgroundResource(R.drawable.drawer_selector);
                    }
                    break;
                case 4:
                    if(selectApps) {
                        viewHolder.rl_base.setBackgroundColor(activity.getResources().getColor(R.color.lightgrey));
                    } else {
                        viewHolder.rl_base.setBackgroundResource(R.drawable.drawer_selector);
                    }
                    break;
            }

            return convertView;
        }

        public void unselectGestures() {
            /**
             * searches for the viewHolders with the rl_drawergesture_base Layout and sets its background to inactive
             */
            for (int i = 0; i < viewHolders.size(); i++) {
                if(viewHolders.get(i).rl_base.getId() == R.id.rl_drawergesture_base){
                    viewHolders.get(i).rl_base.setBackgroundResource(R.drawable.drawer_selector);
                }
            }
        }

        public void unselectBluetooth() {
            /**
             * searches for the viewHolders with the rl_drawerbluetooth_base Layout and sets its background to inactive
             */
            for (int i = 0; i < viewHolders.size(); i++) {
                if(viewHolders.get(i).rl_base.getId() == R.id.rl_drawerbluetooth_base){
                    viewHolders.get(i).rl_base.setBackgroundResource(R.drawable.drawer_selector);
                }
            }
        }

        public void unselectApps() {
            /**
             * searches for the viewHolders with the rl_drawerbluetooth_base Layout and sets its background to inactive
             */
            for (int i = 0; i < viewHolders.size(); i++) {
                if(viewHolders.get(i).rl_base.getId() == R.id.rl_drawerapps_base){
                    viewHolders.get(i).rl_base.setBackgroundResource(R.drawable.drawer_selector);
                }
            }
        }

        public void unselectDevices() {
            /**
             * searches for the viewHolders with the rl_drawerbluetooth_base Layout and sets its background to inactive
             */
            for (int i = 0; i < viewHolders.size(); i++) {
                if(viewHolders.get(i).rl_base.getId() == R.id.rl_drawerdevices_base){
                    viewHolders.get(i).rl_base.setBackgroundResource(R.drawable.drawer_selector);
                }
            }
        }

    }

}
