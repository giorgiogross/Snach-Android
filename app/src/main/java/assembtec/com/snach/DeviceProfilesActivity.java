package assembtec.com.snach;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by Giorgio on 17.05.15.
 */
public class DeviceProfilesActivity extends ActionBarActivity implements OnDevicesChanges {
    private DeviceProfilesSelectFragment selectFragment;
    private DeviceProfilesAddFragment adderFragment;

    // Devices:
    private DevicesHandler devicesHandler;
    private int currentDeviceID = 0;
    private String currentDeviceName = "";

    // Drawer:
    private DrawerHandler mdHandler;
    private Toolbar toolbar;
    private FrameLayout fl_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO if no device profiles available / first start then create default profile and activate it

        setContentView(R.layout.drawer_layout);
        RelativeLayout rl_content = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_device_profiles, fl_content);
        fl_content = (FrameLayout) findViewById(R.id.content_frame);
        fl_content.addView(rl_content);

        toolbar = (Toolbar) findViewById(R.id.tb_apps);
        mdHandler = new DrawerHandler(this, toolbar, false, false, false, true);

        devicesHandler = new DevicesHandler(getApplicationContext());
        currentDeviceID = devicesHandler.getCurrentDeviceProfileID();
        currentDeviceName = devicesHandler.getCurrentDeviceName();

        initFragments();
        setSelectFragment();

    }

    private void setSelectFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.rl_devices_content, selectFragment);
        transaction.commit();

        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            View focusedView = getCurrentFocus();
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void setAdderFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.rl_devices_content, adderFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void initFragments() {
        selectFragment = new DeviceProfilesSelectFragment();
        adderFragment = new DeviceProfilesAddFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public ArrayList<DeviceProfileItem> getDeviceProfilesList() {
        return devicesHandler.getDeviceProfiles();
    }

    @Override
    public void activateDeviceProfile(DeviceProfileItem deviceProfileItem) {
        devicesHandler.activateDeviceProfile(deviceProfileItem);
        currentDeviceID = devicesHandler.getCurrentDeviceProfileID();
        currentDeviceName = devicesHandler.getCurrentDeviceName();
//        devicesHandler.addNewApp("","","",""); // for testing...
        mdHandler.updateDrawer();
    }

    @Override
    public String getCurrentDeviceProfileName() {
        return currentDeviceName;
    }

    @Override
    public int getCurrentDeviceProfileID() {
        return currentDeviceID;
    }

    @Override
    public void showAdderFragment() {
        setAdderFragment();
    }

    @Override
    public void showSelectFragment() {
        setSelectFragment();
    }

    @Override
    public void addNewDeviceProfile(DeviceProfileItem deviceProfileItem) {
        devicesHandler.addNewDeviceProfile(deviceProfileItem);
        setSelectFragment();
        selectFragment.updateProfilesList(devicesHandler.getDeviceProfiles());
    }

    @Override
    public void removeDeviceProfile(int deviceProfileID) {
        if(deviceProfileID != currentDeviceID) {
            devicesHandler.removeDeviceProfile(deviceProfileID);
            selectFragment.updateProfilesList(devicesHandler.getDeviceProfiles());
        } else {
            // TODO OUTPUT TOAST/ERROR.
        }
    }

    public void cancelAdder(View view) {
//        setSelectFragment();
        onBackPressed();
    }
}
