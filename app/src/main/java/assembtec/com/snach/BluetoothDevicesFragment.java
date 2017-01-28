package assembtec.com.snach;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Giorgio on 10.04.2015.
 */
public class BluetoothDevicesFragment extends Fragment {
    private RelativeLayout rl_base;
    private ListView lv_devices;
    private ArrayList<BluetoothDevice> items;
    private DevicesAdapter listAdapter;
    private OnAttemptConnectingListener connectDeviceListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rl_base = (RelativeLayout) inflater.inflate(R.layout.fragment_devices, container, false);
        lv_devices = (ListView) rl_base.findViewById(R.id.lv_devices);

        if(items != null && listAdapter != null) {
            lv_devices.setAdapter(listAdapter);
        }

        lv_devices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectDeviceListener.OnDeviceSelected(items.get(position));
            }
        });

        return rl_base;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        connectDeviceListener = (OnAttemptConnectingListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    public void setItems(ArrayList<BluetoothDevice> items) {
        this.items = items;
        listAdapter = new DevicesAdapter(items);
        if(lv_devices != null) {
            lv_devices.setAdapter(listAdapter);
        }
    }

    public void updateItemsList(ArrayList<BluetoothDevice> btDevices) {
        this.items = btDevices;
        listAdapter.setItems(items);
        listAdapter.notifyDataSetChanged();
    }

    private class ViewHolder {
        protected RelativeLayout rl_device;
        protected TextView tv_device_name;
    }

    private class DevicesAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> items;

        public DevicesAdapter(ArrayList<BluetoothDevice> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if(convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem_device, parent, false);
                holder.rl_device = (RelativeLayout) convertView;
                holder.tv_device_name = (TextView) convertView.findViewById(R.id.tv_deviceName);
                convertView.setTag(holder);
            }

            holder = (ViewHolder) convertView.getTag();
            holder.tv_device_name.setText(items.get(position).getName());

            return convertView;
        }

        public void setItems(ArrayList<BluetoothDevice> items) {
            this.items = items;
        }

        public ArrayList<BluetoothDevice> getItems() {
            return items;
        }
    }

    public interface OnAttemptConnectingListener {
        public void OnDeviceSelected(BluetoothDevice device);
    }
}
