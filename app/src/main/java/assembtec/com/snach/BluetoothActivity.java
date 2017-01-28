package assembtec.com.snach;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import assembtec.com.snach_core_lib.SnachRemoteHandler;
import assembtec.com.snach_core_lib.SnachServiceEvent;

/**
 * Created by Giorgio on 09.04.2015.
 */
public class BluetoothActivity extends ActionBarActivity implements BluetoothDevicesFragment.OnAttemptConnectingListener, ServiceManager.OnServiceConnectionListener {
    // UI:
    private ImageButton ib_searchBtDevices;
    private RelativeLayout rl_bluetooth_content;
    private ButtonClickListener clickListener;

    // Device Profiles
    private ArrayList<DeviceProfileItem> profileItems;
    private DevicesHandler devicesHandler;

    // Fragments
    private BluetoothDevicesFragment devicesFragment;
    private BluetoothConnectionFragment connectionFragment;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;
    private android.support.v4.app.FragmentManager fragmentManager;

    private boolean isPaired = false; // set true if device is already saved, connected and paired

    // Bluetooth:
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> btDevices;
    private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectThread connectThread;

    private ServiceManager mServiceManager;

    // Drawer:
    private DrawerHandler mdHandler;
    private Toolbar toolbar;
    private FrameLayout fl_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        RelativeLayout rl_content = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_bluetooth, fl_content);
        fl_content = (FrameLayout) findViewById(R.id.content_frame);
        fl_content.addView(rl_content);

        toolbar = (Toolbar) findViewById(R.id.tb_bluetooth);
        mdHandler = new DrawerHandler(this, toolbar, true, false, false, false);

        clickListener = new ButtonClickListener();
        initSearcher();

        mServiceManager = new ServiceManager(getApplicationContext(), this);
        initBluetooth();
        rl_bluetooth_content = (RelativeLayout) findViewById(R.id.rl_bluetooth_content);
        mServiceManager.sendServiceRunningRequest();
        initFragments();

        devicesHandler = new DevicesHandler(getApplicationContext());

//        startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")); // needed to ask user to accept that Snach can read notifications
    }

    private void initFragments() {
        devicesFragment = new BluetoothDevicesFragment();
        connectionFragment = new BluetoothConnectionFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        if(isPaired){
            setConnectionFragment();
        } else {
            setDevicesFragment();
            queryPairedDevices();
            devicesFragment.setItems(btDevices);
        }
    }

    private void queryPairedDevices() {
        btDevices = new ArrayList<BluetoothDevice>();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                btDevices.add(device);
            }
        }
    }

    public void setConnectionFragment() {
        isPaired = true;

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.rl_bluetooth_content, connectionFragment);
        transaction.commit();

        modifyFABIcon(isPaired);
    }

    private void modifyFABIcon(boolean isPaired) {
        if(isPaired) {
            ib_searchBtDevices.setImageResource(R.drawable.ic_action_disconnect);
        } else {
            ib_searchBtDevices.setImageResource(R.drawable.abc_ic_search_api_mtrl_alpha);
        }
    }

    public void setDevicesFragment() {
        isPaired = false;
        modifyFABIcon(isPaired);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.rl_bluetooth_content, devicesFragment);
        transaction.commit();
    }

    private void initBluetooth() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_no_bluetooth_support), Toast.LENGTH_SHORT).show();
            this.finish();
        }

    }

    private void initSearcher() {
        ib_searchBtDevices = (ImageButton) findViewById(R.id.ib_searchBtDevices);
        ib_searchBtDevices.setOnClickListener(clickListener);
    }

    @Override
    public void OnDeviceSelected(BluetoothDevice device) {
        /**
         * Connects the selected device and checks if it is a Snach
         */

        mScanning = false;
        mBluetoothAdapter.stopLeScan(mLeScanCallback);

        Log.i("BT_ACTIVITY", "Device selected with address "+device.getAddress());

        devicesHandler.setCurrentDeviceAddress(device.getAddress());
        mServiceManager.startService(device.getAddress());
    }

    @Override
    public void isServiceRunning(boolean isRunning) {
        if (isRunning) {
            setConnectionFragment();
        } else {
            setDevicesFragment();
        }
    }

    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ib_searchBtDevices:{
                    if(!isPaired) {
                        startBluetoothQuery();
                    } else {
                        disconnectDevice();
                    }
                    break;
                }
            }
        }
    }

    private void disconnectDevice() {
        /**
         * Stops the SnachStreamService, queries paired devices and
         * removes the current specified device from SharedPreferences.
         */

        devicesHandler.removeCurrentDeviceAddress();

        queryPairedDevices();
        devicesFragment.setItems(btDevices);
        isPaired = false;
        mServiceManager.stopService();
    }

    private void startBluetoothQuery() {
        if(!activateBluetooth()){
            queryAndDiscoverDevices();
            devicesFragment.setItems(btDevices);
        }
    }

    private boolean mScanning;
    private Handler mHandler;
    private void queryAndDiscoverDevices() {
        btDevices = new ArrayList<BluetoothDevice>();

        // TODO Show progress bar on start, hide progress bar btReceiver on end

        if (mScanning) {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }

        // Stops scanning after a pre-defined scan period.
        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mScanning) {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }
        }, 12000);

        mScanning = true;
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    private List<UUID> parseUUIDs(final byte[] advertisedData) {
        List<UUID> uuids = new ArrayList<UUID>();

        int offset = 0;
        while (offset < (advertisedData.length - 2)) {
            int len = advertisedData[offset++];
            if (len == 0)
                break;

            int type = advertisedData[offset++];
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (len > 1) {
                        int uuid16 = advertisedData[offset++];
                        uuid16 += (advertisedData[offset++] << 8);
                        len -= 2;
                        uuids.add(UUID.fromString(String.format(
                                "%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                    }
                    break;
                case 0x06:// Partial list of 128-bit UUIDs
                case 0x07:// Complete list of 128-bit UUIDs
                    // Loop through the advertised 128-bit UUID's.
                    while (len >= 16) {
                        try {
                            // Wrap the advertised bits and order them.
                            ByteBuffer buffer = ByteBuffer.wrap(advertisedData,
                                    offset++, 16).order(ByteOrder.LITTLE_ENDIAN);
                            long mostSignificantBit = buffer.getLong();
                            long leastSignificantBit = buffer.getLong();
                            uuids.add(new UUID(leastSignificantBit,
                                    mostSignificantBit));
                        } catch (IndexOutOfBoundsException e) {
                            // Defensive programming.
                            Log.e("ERRROR", e.toString());
                            continue;
                        } finally {
                            // Move the offset to read the next uuid.
                            offset += 15;
                            len -= 16;
                        }
                    }
                    break;
                default:
                    offset += (len - 1);
                    break;
            }
        }

        return uuids;
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    btDevices.add(device);
                    devicesFragment.setItems(btDevices);

                    /*List<UUID> uuids = parseUUIDs(scanRecord);
                    for(UUID ui : uuids){
                        Log.i("DISCOVERY", "scanRecord: "+ui);
                    }*/

                }
            };

    private boolean activateBluetooth() {
        // TODO app freezes after enabling bluetooth through app instead of directly enabling it
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**If enabling Bluetooth succeeds, your activity receives the RESULT_OK result code in the onActivityResult() callback.
          If Bluetooth was not enabled due to an error (or the user responded "No") then the result code is RESULT_CANCELED.*/
        if(resultCode == RESULT_OK){
//            startBluetoothQuery();
        } else {
            // Do some default stuff, toast, notification, etc. ...
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mServiceManager != null){
            mServiceManager.unregisterReceiver();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mServiceManager != null){
            mServiceManager.registerReceiver();
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            /*try {
                tmp = createBluetoothSocket(device);
            } catch (IOException e1) {
                e1.printStackTrace();
            }*/

            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { e.printStackTrace(); }

            /*try {
                tmp = createBluetoothSocket(device);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            mmSocket = tmp;
        }

        public void run() {
            try {
                mmSocket.connect();
                setConnectionFragment();
            } catch (IOException connectException) {
                connectException.printStackTrace();
                makeError(getResources().getString(R.string.error_bluetooth_socket));
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            manageConnectedSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private void makeError(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    private BluetoothSocket createSocket(BluetoothDevice device){
        BluetoothSocket socket = null;
        BluetoothDevice hxm = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getAddress());
        Method m;
        try {
            m = hxm.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
            socket = (BluetoothSocket)m.invoke(hxm, Integer.valueOf(1));
            socket.connect();
        } catch (Exception e) {
            makeError(getResources().getString(R.string.error_bluetooth_socket));
            e.printStackTrace();
        }

        return socket;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        /**
         * Not working properly as channel may be wrong (?)
         */
        Class<?> clazz = device.getClass();
        Class<?>[] paramTypes = new Class<?>[] {Integer.TYPE};

        Method m = null;
        try {
            m = clazz.getMethod("createRfcommSocket", paramTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Object[] params = new Object[] {2};

        try {
            return (BluetoothSocket) m.invoke(device, params);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
    }

    private void manageConnectedSocket(BluetoothSocket mmSocket) {
        ConnectedThread connectedThread = new ConnectedThread(mmSocket);
        connectedThread.write('n');
        String s = connectedThread.read();
        while(!s.startsWith("0")) {
            Log.i("BT_STREAM", s);
            s = connectedThread.read();
        }
//        connectedThread.run();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    /*mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();*/
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(char bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

        public String read() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
//            while (mmInStream.) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    /*mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();*/
                } catch (IOException e) {
                    e.printStackTrace();
//                    break;
                }

//            }
            String stream = "";
            for(int i = 0; i<1024; i++){
                stream += buffer[i];
            }
            return ""+stream;
        }
    }
}
