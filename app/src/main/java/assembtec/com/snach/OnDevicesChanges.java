package assembtec.com.snach;

import java.util.ArrayList;

/**
 * Created by Giorgio on 17.05.15.
 */
public interface OnDevicesChanges {
    ArrayList<DeviceProfileItem> getDeviceProfilesList();
    void activateDeviceProfile(DeviceProfileItem deviceProfileItem);
    String getCurrentDeviceProfileName();
    int getCurrentDeviceProfileID();
    void showAdderFragment();
    void showSelectFragment();
    void addNewDeviceProfile(DeviceProfileItem deviceProfileItem);
    void removeDeviceProfile(int deviceProfileID);
}
