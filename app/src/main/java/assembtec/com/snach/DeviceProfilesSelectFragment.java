package assembtec.com.snach;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Giorgio on 17.05.15.
 */
public class DeviceProfilesSelectFragment extends Fragment {
    // UI:
    private RelativeLayout rl_base;
    private Button b_addDeviceProfile;
    private ListView lv_deviceProfiles;
    private TextView tv_currentProfileName;

    private ArrayList<DeviceProfileItem> profilesList;
    private DevicesAdapter listAdapter;

    private OnDevicesChanges onDevicesChanges;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onDevicesChanges = (OnDevicesChanges) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl_base = (RelativeLayout) inflater.inflate(R.layout.fragment_select_device_profile, container, false);

        tv_currentProfileName = (TextView) rl_base.findViewById(R.id.tv_currentProfileName);

        tv_currentProfileName.setText(onDevicesChanges.getCurrentDeviceProfileName());

        b_addDeviceProfile = (Button) rl_base.findViewById(R.id.b_addDeviceProfile);
        b_addDeviceProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDevicesChanges.showAdderFragment();
            }
        });

        initDeviceProfilesList();

        lv_deviceProfiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectDeviceProfile(profilesList.get(position));
            }
        });

        return rl_base;
    }

    private void selectDeviceProfile(DeviceProfileItem deviceProfileItem) {
        onDevicesChanges.activateDeviceProfile(deviceProfileItem);
        tv_currentProfileName.setText(deviceProfileItem.getName());
    }

    private void initDeviceProfilesList() {
        lv_deviceProfiles = (ListView) rl_base.findViewById(R.id.lv_deviceProfiles);
        profilesList = onDevicesChanges.getDeviceProfilesList();
        listAdapter = new DevicesAdapter(profilesList);
        lv_deviceProfiles.setAdapter(listAdapter);
    }

    public void updateProfilesList(ArrayList<DeviceProfileItem> profilesList){
        this.profilesList = profilesList;
        listAdapter.setProfilesList(profilesList);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class ViewHolder {
        protected TextView tv_deviceProfileName;
        protected ImageButton ib_removeDevice;
    }

    private class DevicesAdapter extends BaseAdapter {
        private ArrayList<DeviceProfileItem> profilesList;

        public DevicesAdapter (ArrayList<DeviceProfileItem> profilesList){
            this.profilesList = profilesList;
        }

        public void setProfilesList(ArrayList<DeviceProfileItem> profilesList){
            this.profilesList = profilesList;
        }

        @Override
        public int getCount() {
            return profilesList.size();
        }

        @Override
        public Object getItem(int position) {
            return profilesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();

                convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem_device_profile, parent, false);
                holder.tv_deviceProfileName = (TextView) convertView.findViewById(R.id.tv_deviceProfileName);
                holder.ib_removeDevice = (ImageButton) convertView.findViewById(R.id.ib_removeDevice);
                holder.ib_removeDevice.setFocusable(false);

                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();

            holder.tv_deviceProfileName.setText(profilesList.get(position).getName());
            holder.ib_removeDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDevicesChanges.removeDeviceProfile(profilesList.get(position).getID());
                }
            });

            return convertView;
        }
    }
}
