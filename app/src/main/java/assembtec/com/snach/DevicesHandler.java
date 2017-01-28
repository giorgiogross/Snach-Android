package assembtec.com.snach;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Giorgio on 16.05.15.
 */
public class DevicesHandler {
    private Context context;
    private SharedPreferences sharedDevice;
    private SharedPreferences sharedDevicesSpecs;

    private int deviceProfileID = 0;
    private int devicesAmount = 0;

    private int maxScreens = 0;
    private int appsAmount = 0;

    public DevicesHandler (Context context) {
        this.context = context;

        setUpSharedPreferences();
    }

    private void setUpSharedPreferences() {
        sharedDevicesSpecs = context.getSharedPreferences(Globals.MAJOR_KEY_DEVICES_SPECS, Context.MODE_MULTI_PROCESS);
        deviceProfileID = sharedDevicesSpecs.getInt(Globals.KEY_CURRENT_DEVICE_ID, 0);
        devicesAmount = sharedDevicesSpecs.getInt(Globals.KEY_DEVICES_AMOUNT, 0);

        sharedDevice = context.getSharedPreferences(deviceProfileID + Globals.MAJOR_KEY_DEVICE, Context.MODE_MULTI_PROCESS);
        maxScreens = sharedDevice.getInt(Globals.KEY_MAX_SNACH_SCREENS, Globals.DEFAULT_SNACH_SCREENS);
        appsAmount = sharedDevice.getInt(Globals.KEY_APPS_AMOUNT, 0);
    }

    public int getCurrentDeviceProfileID() {
        return deviceProfileID;
    }

    public String getCurrentDeviceName(){
        return sharedDevicesSpecs.getString(Globals.KEY_CURRENT_DEVICE_NAME, Globals.DEFAULT_DEVICE_NAME);
    }

    public void addNewApp(String appName, String appPackage, String appBCAction, String appBCExtra){
        appsAmount++;
        maxScreens++;
        int newAppID = appsAmount-1;

        sharedDevice.edit().putInt(Globals.KEY_APPS_AMOUNT, appsAmount).apply();
        sharedDevice.edit().putInt(Globals.KEY_MAX_SNACH_SCREENS, maxScreens).apply();

        SharedPreferences sharedApp = context.getSharedPreferences(deviceProfileID+"."+newAppID+Globals.MAJOR_KEY_DEVICE_APP, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedApp.edit();
        editor.putInt(Globals.KEY_APP_ID, newAppID);
        editor.putInt(Globals.KEY_APP_SNACH_SCREEN_INDEX, maxScreens - 2);
        editor.putString(Globals.KEY_APP_NAME, appName);
        editor.putString(Globals.KEY_APP_PACKAGE, appPackage);
        editor.putString(Globals.KEY_APP_BROADCAST_ACTION, appBCAction);
        editor.putString(Globals.KEY_APP_BROADCAST_EXTRA, appBCExtra);

        editor.apply();

        sharedApp = context.getSharedPreferences(deviceProfileID+"."+newAppID+Globals.MAJOR_KEY_DEVICE_APP, Context.MODE_MULTI_PROCESS);
        Log.i("DEVICESADDER", "ID:" + sharedApp.getInt(Globals.KEY_APP_ID, -1));

        for(SnachAppItem sap : getActiveApps()){
            Log.i("DEVICESADDER", "sap name: "+sap.getAppName());
        }

    }

    public ArrayList<SnachAppItem> getActiveApps(){
        ArrayList<SnachAppItem> appList = new ArrayList<>();

        for(int a = 0; a < appsAmount; a++){
            SharedPreferences sharedApp = context.getSharedPreferences(deviceProfileID + "." + a + Globals.MAJOR_KEY_DEVICE_APP, Context.MODE_MULTI_PROCESS);
            Log.i("DEVICESGETTER", "sharedApp ID: "+sharedApp.getInt(Globals.KEY_APP_ID, -1));
            int ID = sharedApp.getInt(Globals.KEY_APP_ID, -1);
            if(ID >= 0){
                SnachAppItem sap = new SnachAppItem();
                sap.setID(ID);
                sap.setAppName(sharedApp.getString(Globals.KEY_APP_NAME, Globals.DEFAULT_APP_NAME));
                sap.setAppPackage(sharedApp.getString(Globals.KEY_APP_PACKAGE, Globals.DEFAULT_APP_PACKAGE));

                appList.add(sap);
            }
        }

        return appList;
    }

    public void removeApp(int deviceProfileID, int appID){
        SharedPreferences sharedApp = context.getSharedPreferences(deviceProfileID+""+appID+Globals.MAJOR_KEY_DEVICE_APP, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedApp.edit();
        editor.clear();
        editor.apply();
    }

    public ArrayList<DeviceProfileItem> getDeviceProfiles() {
        ArrayList<DeviceProfileItem> profiles = new ArrayList<>();

        for(int d = 0; d < devicesAmount; d++){
            SharedPreferences sharedDevice = context.getSharedPreferences(d+Globals.MAJOR_KEY_DEVICE, Context.MODE_MULTI_PROCESS);
            int devID = sharedDevice.getInt(Globals.KEY_DEVICE_ID, -1);
            Log.i("DEVICE", "ID: "+devID);
            if(devID >= 0){
                DeviceProfileItem devItem = new DeviceProfileItem();
                devItem.setID(devID);
                devItem.setName(sharedDevice.getString(Globals.KEY_DEVICE_NAME, Globals.DEFAULT_DEVICE_NAME));
                devItem.setAddress(sharedDevice.getString(Globals.KEY_DEVICE_ADDRESS, Globals.DEFAULT_DEVICE_ADDRESS));

                profiles.add(devItem);
            }
        }

        return profiles;
    }

    public void activateDeviceProfile(DeviceProfileItem devItem) {
        SharedPreferences.Editor editor = sharedDevicesSpecs.edit();

        editor.putInt(Globals.KEY_CURRENT_DEVICE_ID, devItem.getID());
        editor.putString(Globals.KEY_CURRENT_DEVICE_NAME, devItem.getName());
        editor.putString(Globals.KEY_CURRENT_DEVICE_ADDRESS, devItem.getAddress());

        editor.apply();
        setUpSharedPreferences();
    }

    public void removeDeviceProfile(int deviceProfileID) {
        SharedPreferences sharedDevice = context.getSharedPreferences(deviceProfileID + Globals.MAJOR_KEY_DEVICE, Context.MODE_MULTI_PROCESS);
        sharedDevice.edit().clear().apply();
        setUpSharedPreferences();
    }

    public void addNewDeviceProfile(DeviceProfileItem deviceProfileItem) {
        devicesAmount++;
        sharedDevicesSpecs.edit().putInt(Globals.KEY_DEVICES_AMOUNT, devicesAmount).apply();
        int newID = devicesAmount-1;

        SharedPreferences sharedDevice = context.getSharedPreferences(newID+Globals.MAJOR_KEY_DEVICE, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedDevice.edit();
        editor.putInt(Globals.KEY_DEVICE_ID, newID);
        editor.putString(Globals.KEY_DEVICE_NAME, deviceProfileItem.getName());
        editor.putInt(Globals.KEY_MAX_SNACH_SCREENS, Globals.DEFAULT_SNACH_SCREENS);
        editor.putInt(Globals.KEY_APPS_AMOUNT, Globals.DEFAULT_APPS_AMOUNT);

        editor.apply();
        setUpSharedPreferences();

    }

    public void setCurrentDeviceAddress(String address) {
        sharedDevicesSpecs.edit().putString(Globals.KEY_CURRENT_DEVICE_ADDRESS, address).apply();
        sharedDevice.edit().putString(Globals.KEY_DEVICE_ADDRESS, address).apply();
    }

    public String getCurrentDeviceAddress() {
        return sharedDevicesSpecs.getString(Globals.KEY_CURRENT_DEVICE_ADDRESS, null);
    }

    public void removeCurrentDeviceAddress() {
        sharedDevicesSpecs.edit().putString(Globals.KEY_CURRENT_DEVICE_ADDRESS, null).apply();
    }

}
