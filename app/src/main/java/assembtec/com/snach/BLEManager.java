package assembtec.com.snach;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import assembtec.com.snach_core_lib.ActionAppContentItem;
import assembtec.com.snach_core_lib.GesturePoint;
import assembtec.com.snach_core_lib.ListAppContentItem;
import assembtec.com.snach_core_lib.PopUpContentItem;
import assembtec.com.snach_core_lib.SnachExtras;
import assembtec.com.snach_core_lib.SnachNotification;

/**
 * Created by Giorgio on 14.04.2015.
 */
public class BLEManager implements GestureProcessingHandler.OnGestureAddedListener, OnNotificationEvent{
    // Snach System:
    private int CURRENT_MODE = 1;
    private int SNACH_SCREEN = -1;
    private int SNACH_SCROLL_STATE = 0;
    private boolean didInitialTransfer = false;
    private boolean isSnachConnected = false;
    private boolean isReceivingSnachData = false;
    private Context context;

    private OnConnectionEventListener mConnectionListener;

    // Gestures:
    private GestureProcessingHandler gestureHandler;
    private ArrayList<ArrayList<ArrayList<GesturePoint>>> gestureCharacteristicsData;
    private SharedPreferences sharedGesture;
    private SharedPreferences sharedGesturesSpecs;

    // Devices:
    private int currentProfileID = 0;
    private SharedPreferences sharedDevicesSpecs;
    private SharedPreferences sharedDevice;
    private ArrayList<SnachAppItem> snachAppScreens;
    private int SNACH_SCREENS_AMOUNT = 2; // 4; // + apps...
    private int deviceID = 0;

    // Bluetooth:
    private String DEVICE_ADDRESS = "";
    private BluetoothGatt gatt;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothDevice bt_device = null;
    private Handler connectionTimeoutChecker;
    private Runnable connectionTimeoutRunnable;
    private Handler dataLostChecker;
    private Runnable dataLostRunnable;

    // Notificatios:
    private NotificationListenerService mNotificationListener;
    private ArrayList<StatusBarNotification> sbNotifications;
    private ArrayList<SnachNotification> snachNotifications;
    private ArrayList<SnachNotificationStandard> snachNotificationsStandard;
    private boolean isPendingNotification = false;


    // raw byte buffer of whole data which has to be sent to snach. Up to 100 Bytes,
    // though maximum should be at 40Bytes for App screens and approx.60Bytes for watch faces
    private List<BufferItem> pendingBufferItems = Collections.synchronizedList(new ArrayList<BufferItem>());
    // General Variables:
    private boolean hasFinishedWriting = false;
    // Watchface
    private boolean hasSentClockData = true;
    // Other Apps:
//    private boolean doBroadcastApp = false;
    // List Layout:
    private ListAppContentItem LACI;
//    private boolean sendLACI;
//    private boolean sendListTitles = false;
    private boolean isSnachScreenScrollable = false;
    private boolean hasSentDataStart = true;
    private boolean hasSentDataEnd = true;
    private boolean hasSentListEnd = false; // upper border for scroll state..
    private int contentDataSentStart = 0;
    private int contentDataSentEnd = 0;
    private int currentListDataItem = 0;
    // Action Layout:
    private ActionAppContentItem AACI;
//    private boolean sendAACI = false;
    // Pop Up Layout
    private PopUpContentItem PUCI;
    private boolean isShowingPopUp = false;

    public BLEManager(String deviceAddress, Context applicationContext, Service mService) {
        this.DEVICE_ADDRESS = deviceAddress;
        this.context = applicationContext;
        this.mConnectionListener = (OnConnectionEventListener) mService;

        initSharedPref();
        initSavedGestures();
        initSnachScreens();
        gestureHandler = new GestureProcessingHandler(gestureCharacteristicsData, context, this);

        connectionTimeoutChecker = new Handler();
        connectionTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if(isSnachConnected) {
                    isSnachConnected = false;
                    mConnectionListener.ConnectionLost();
                }
            }
        };

        connectionTimeoutChecker.postDelayed(connectionTimeoutRunnable, Globals.DEFAULT_CONNECTED_TIMEOUT);

        dataLostChecker = new Handler();
        dataLostRunnable = new Runnable() {
            @Override
            public void run() {
                hasFinishedWriting = true;
            }
        };

        initSnachNotifications();
    }

    private void startDataLostChecker(){
        dataLostChecker.postDelayed(dataLostRunnable, 500);
    }

    private void stopDataLostChecker(){
        dataLostChecker.removeCallbacks(dataLostRunnable);
    }

    private void initSnachNotifications() {
        snachNotifications = new ArrayList<>();
        snachNotificationsStandard = new ArrayList<>();
//        mNotificationListener = new NotificationsService(this);
        NotificationsService.setOnNotificationEvent(this);
        sbNotifications = new ArrayList<>();//Arrays.asList(mNotificationListener.getActiveNotifications()));
    }

    private void resetConnectionTimeoutChecker(){
        /**
         * Resets the timeout checker.
         *
         * If this is not called the Snach is regarded as disconnected,
         * the Service ends and broadcasts that the connection was lost.
         */
        connectionTimeoutChecker.removeCallbacks(connectionTimeoutRunnable);
        connectionTimeoutChecker.postDelayed(connectionTimeoutRunnable, Globals.DEFAULT_CONNECTED_TIMEOUT);
    }

    private void initSavedGestures() {
        gestureCharacteristicsData = new ArrayList<ArrayList<ArrayList<GesturePoint>>>();

        sharedGesturesSpecs = context.getSharedPreferences(Globals.MAJOR_KEY_GLOBALGESTURES_SPECS, Context.MODE_PRIVATE);
        int totalGlobalGestures = sharedGesturesSpecs.getInt(Globals.KEY_GESTURES_AMOUNT, 0);

        for(int g = 1; g <= totalGlobalGestures; g++){
            sharedGesture = context.getSharedPreferences(g+Globals.MAJOR_KEY_GLOBALGESTURE, Context.MODE_PRIVATE);
            int gesture_ID = sharedGesture.getInt(Globals.KEY_GESTURE_ID, -1);
            int duration = sharedGesture.getInt(Globals.KEY_GESTURE_DURATION, 100);
            boolean isEnabled = sharedGesture.getBoolean(Globals.KEY_GESTURE_ENABLED, false);

            if(gesture_ID > 0 && isEnabled) {
                ArrayList<ArrayList<GesturePoint>> gesturePointsAttmpt = new ArrayList<ArrayList<GesturePoint>>();
//                int characteristicsAmount = sharedGesture.getInt(Globals.KEY_GESTURE_CARACTERISTICS_AMOUNT, 0);

                for (int a = 1; a <= Globals.GESTURE_RECORDING_ATTEMPTS+1; a++) {
                    ArrayList<GesturePoint> gesturePoints = new ArrayList<GesturePoint>();
                    String gestureAction = sharedGesture.getString(Globals.KEY_GESTURE_ACTION, SnachExtras.GESTURE_ACTION_HOMESCREEN);
                    String gestureApp = sharedGesture.getString(Globals.KEY_GESTURE_APP, SnachExtras.GESTURE_GLOBAL);
                    String gestureMode = sharedGesture.getString(Globals.KEY_GESTURE_MODE, SnachExtras.GESTURE_MODE_SCREEN_ON);

                    int characteristicsAmount = sharedGesture.getInt(Globals.KEY_GESTURE_CARACTERISTICS_AMOUNT+a, 0);
                    for (int i = 1; i <= characteristicsAmount; i++) {
                        GesturePoint gp = new GesturePoint();
                        int xA = sharedGesture.getInt(i + Globals.KEY_GESTURE_XA_DATA+a, 0);
                        gp.setxA(xA);
                        int yA = sharedGesture.getInt(i + Globals.KEY_GESTURE_YA_DATA+a, 0);
                        gp.setyA(yA);
                        int zA = sharedGesture.getInt(i + Globals.KEY_GESTURE_ZA_DATA+a, 0);
                        gp.setzA(zA);

                        int DxA = sharedGesture.getInt(i + Globals.KEY_GESTURE_dXA_DATA+a, 0);
                        Log.i("inititlized", "DxA :" + DxA+ " at attempt "+a+" at characterisics "+i);
                        gp.setD_xA(DxA);
                        int DyA = sharedGesture.getInt(i + Globals.KEY_GESTURE_dYA_DATA+a, 0);
                        Log.i("inititlized", "DyA :" + DyA+ " at attempt "+a+" at characterisics "+i);
                        gp.setD_yA(DyA);
                        int DzA = sharedGesture.getInt(i + Globals.KEY_GESTURE_dZA_DATA+a, 0);
                        Log.i("inititlized", "DzA :" + DzA+ " at attempt "+a+" at characterisics "+i);
                        gp.setD_zA(DzA);

                        int xG = sharedGesture.getInt(i + Globals.KEY_GESTURE_XG_DATA+a, 0);
                        gp.setxG(xG);
                        int yG = sharedGesture.getInt(i + Globals.KEY_GESTURE_YG_DATA+a, 0);
                        gp.setyG(yG);
                        int zG = sharedGesture.getInt(i + Globals.KEY_GESTURE_ZG_DATA+a, 0);
                        gp.setzG(zG);

                        int DxG = sharedGesture.getInt(i + Globals.KEY_GESTURE_dXG_DATA+a, 0);
                        Log.i("inititlized", "DxG :" + DxG+ " at attempt "+a+" at characterisics "+i);
                        gp.setD_xG(DxG);
                        int DyG = sharedGesture.getInt(i + Globals.KEY_GESTURE_dYG_DATA+a, 0);
                        Log.i("inititlized", "DyG :" + DyG+ " at attempt "+a+" at characterisics "+i);
                        gp.setD_yG(DyG);
                        int DzG = sharedGesture.getInt(i + Globals.KEY_GESTURE_dZG_DATA+a, 0);
                        gp.setD_zG(DzG);

                        gp.setdT(sharedGesture.getInt(i + Globals.KEY_GESTURE_DELTA_TIME_DATA+a, Globals.BT_DATA_DELAY));
                        gp.setID(gesture_ID);
                        gp.setDuration(duration);
//                        gp.setCharacteristics(sharedGesture.getBoolean(i + Globals.KEY_GESTURE_IS_CHARACTERISTICS+a, false));

                        gp.setAction(gestureAction);
                        gp.setApp(gestureApp);
                        gp.setMode(gestureMode);

                        gesturePoints.add(gp);
                    }
                    gesturePointsAttmpt.add(gesturePoints);
                }
//                if (gesturePointsAttmpt.size() == Globals.GESTURE_RECORDING_ATTEMPTS + 1) {
                    gestureCharacteristicsData.add(gesturePointsAttmpt);
//                }

            }

        }

        // include standard gestures


    }

    private void initSnachScreens() {
        snachAppScreens = new ArrayList<>();
        int amountOfApps =  sharedDevice.getInt(Globals.KEY_APPS_AMOUNT, 0);
        for(int a = 0 ; a < amountOfApps; a++){
            SharedPreferences sharedApp = context.getSharedPreferences(deviceID + "." + a + Globals.MAJOR_KEY_DEVICE_APP, Context.MODE_PRIVATE);
            int appID = sharedApp.getInt(Globals.KEY_APP_ID, -1);
            if(appID >= 0){
                SnachAppItem sap = new SnachAppItem();
                sap.setID(appID);
                sap.setAppName(sharedApp.getString(Globals.KEY_APP_NAME, Globals.DEFAULT_APP_NAME));
                sap.setAppPackage(sharedApp.getString(Globals.KEY_APP_PACKAGE, Globals.DEFAULT_APP_PACKAGE));
                sap.setSnachScreenIndex(sharedApp.getInt(Globals.KEY_APP_SNACH_SCREEN_INDEX, 2));
                sap.setAppBCAction(sharedApp.getString(Globals.KEY_APP_BROADCAST_ACTION, Globals.DEFAULT_BROADCAST_ACTION_SCREEN));
                sap.setAppBCExtra(sharedApp.getString(Globals.KEY_APP_BROADCAST_EXTRA, Globals.DEFAULT_BROADCAST_EXTRA_SCREEN));
                snachAppScreens.add(sap);

                Log.i("SNACHSCREEN", " NAME: "+sap.getAppName()+"   screenIndex: "+sap.getSnachScreenIndex());
            }
        }

        /*// remove later:
        SnachAppItem sap = new SnachAppItem();
        sap.setID(1);
        sap.setAppName("STOPWATCH");
        sap.setAppPackage(context.getPackageName());
        sap.setSnachScreenIndex(2);
        sap.setAppBCAction("com.assembtec.snach.STND_STOPWATCH");
        sap.setAppBCExtra("mCustomBroadcastExtra");
        snachAppScreens.add(sap);*/

        Collections.sort(snachAppScreens, new PosComparator());
        SNACH_SCREENS_AMOUNT = snachAppScreens.size() + Globals.DEFAULT_SNACH_SCREENS;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void OnNotificationPosted(StatusBarNotification[] statusBarNotifications) {
        // Todo create alternative to get content..?
        // updateNotifications(0);
        snachNotificationsStandard.clear();
        sbNotifications = new ArrayList<>(Arrays.asList(statusBarNotifications));
        for(StatusBarNotification sbn : sbNotifications){
            try {
                Bundle args = sbn.getNotification().extras;
                SnachNotificationStandard sns = new SnachNotificationStandard();
                sns.setID(sbn.getId());
                sns.setTitle(args.getCharSequence("android.title").toString());
                sns.setContent(args.getCharSequence("android.text").toString());
                sns.setPostTime(sbn.getPostTime());

                Log.i("NOTIFICATION", "TITLE: " + sns.getTitle());

                snachNotificationsStandard.add(sns);
            } catch (NullPointerException ex){
                ex.printStackTrace();
            }
        }

        updateNotifications(0);
//        pushNotificationInfo();
    }

    private void updateNotifications(int notificationPosition) {
        isPendingNotification = true;
        if(SNACH_SCREEN == 1){
            // update list, set current item to 0 and show the current item
            // check if user has currently selected the item when and if it is deleted.

            setUpNotifications(notificationPosition);
//            setUpListScreen(LACI, false, 0);
        } else {
            // show notification symbol if notification is added
            pushNotificationInfo();
        }
    }

    private void pushNotificationInfo() {
        byte [] snachReplyBuffer = new byte[2];
        snachReplyBuffer[0] = Globals.BYTE_ID_NOTIFICATION_INFO;
        snachReplyBuffer[1] = (byte) Globals.SNACH_EOF_BYTE;

        addPendingBufferData(false, true, false, snachReplyBuffer);
    }

    @Override
    public void OnNotificationRemoved(int ID) {
        for(int s = 0; s < sbNotifications.size(); s++){
            StatusBarNotification sbn = sbNotifications.get(s);
            if(sbn.getId() == ID){
                sbNotifications.remove(s);
                if(s == sbNotifications.size()){
                    s--;
                }
                //updateNotifications(s);
                break;
            }
        }

    }

    public void OnNotificationPushed(SnachNotification sn) {
        /**
         * Called when a client pushes an exclusive snach notification
         */
        sn.setPostTime(System.currentTimeMillis());
        snachNotifications.add(sn);
        updateNotifications(0);
    }

    public void OnNotificationRemoveRequested(String bcAction){
        for(int s = 0; s < snachNotifications.size(); s++){
            if(snachNotifications.get(s).getAppBCAction().equals(bcAction)){
                snachNotifications.remove(s);
                if(s == sbNotifications.size()){
                    s--;
                }
                updateNotifications(s);
                break;
            }
        }


    }

    public class PosComparator implements Comparator<SnachAppItem> {

        @Override
        public int compare(SnachAppItem i1, SnachAppItem i2) {
//            return Integer.valueOf(pi1.getPosition()).compareTo(Integer.valueOf(pi2.getPosition()));
            return i1.getSnachScreenIndex() - i2.getSnachScreenIndex();

        }
    }

    private boolean checkBluetoothState() {
        // TODO app freezes after enabling bluetooth through app instead of directly enabling it
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(enableBtIntent);
            Log.i("LOOP_THREAD", "Bluetooth not enabled..");
            return false;
        }
        return true;
    }

    public void startBLEConnection(){
        if(!isSnachConnected){
            connectSnach();
        }

    }

    private void initSharedPref() {
        sharedDevicesSpecs = context.getSharedPreferences(Globals.MAJOR_KEY_DEVICES_SPECS, Context.MODE_PRIVATE);
        currentProfileID = sharedDevicesSpecs.getInt(Globals.KEY_CURRENT_DEVICE_ID, 0);
        sharedDevice = context.getSharedPreferences(currentProfileID + Globals.MAJOR_KEY_DEVICE, Context.MODE_PRIVATE);
        deviceID = sharedDevice.getInt(Globals.KEY_DEVICE_ID, 0);
    }

    private void connectSnach() {
        /**
         * Connects to the currently specified device if Bluetooth is activated,
         * otherwise sends a request to the user to activate bluetooth and interrupts the thread
         */

        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (checkBluetoothState()) {

            bt_device = createBluetoothDevice(DEVICE_ADDRESS);
            Handler h = new Handler(context.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    gatt = bt_device.connectGatt(context, false, mGattCallback);
                }
            });

            // Timeout listener:
            Log.i("BLE", "Connecting to snach...");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!isSnachConnected){
                        mConnectionListener.ConnectionLost();
                    }
                }
            }, Globals.DEFAULT_CONNECTION_ATTEMPT_TIMEOUT);
        } else {
            this.shutDown();
        }


    }

    private final BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {

                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        resetConnectionTimeoutChecker();

                        isSnachConnected = true;

                        BLEManager.this.gatt = gatt; // TODO check if right device is discovered (instead of another BLE device with a different GATT..)
                        Log.i("BLE", "Connected to GATT server.");
                        Log.i("BLE", "Attempting to start service discovery:" + startServiceDiscovery(gatt));

                        sendBroadcastConnectionChanged(isSnachConnected);

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        isSnachConnected = false;
                        mConnectionListener.ConnectionLost();
                        Log.i("BLE", "Disconnected from GATT server.");
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    resetConnectionTimeoutChecker();

                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.w("BLE", "onServicesDiscovered " + gatt.getService(Globals.SNACH_SYSTEM_SERVICE_UUID));

                        for(BluetoothGattService se : gatt.getServices()){
                            Log.i("BLE", "discovered Service: "+se.getUuid()+"   "+se.getCharacteristics().toString());
                        }
                    } else {
                        Log.w("BLE", "onServicesDiscovered received: " + status);
                    }
                    setNotifySensor(gatt);
                    didInitialTransfer = false;
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    resetConnectionTimeoutChecker();

                    if (status == BluetoothGatt.GATT_SUCCESS) {

                        Log.w("BLE", "CharacteristicRead - characteristics uuid: " + characteristic.getUuid());
                        Log.w("BLE", "CharacteristicRead - service uuid: " + characteristic.getService().getUuid());
                        Log.w("BLE", "CharacteristicRead - string value: " + characteristic.getValue());

                        byte[] bytes = characteristic.getValue();
                        for(byte b : bytes){
                            Log.w("BLE", "CharacteristicRead - byte value: " + b);
                        }

                        if(Globals.SNACH_SYSTEM_UART_TX_UUID.equals(characteristic.getUuid())){
                            Log.w("BLE", "CharacteristicRead - xaccel service uuid: " + characteristic.getService().getUuid());
                            Log.w("BLE", "CharacteristicRead - xaccel value: " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,0));
                            Log.w("BLE", "CharacteristicRead - xaccel value: " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,0));
                            Log.w("BLE", "CharacteristicRead - xaccel value: " + characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8,1));
//                        setNotifySensor(gatt);
                        }

                    }



                }

                private void setNotifySensor(BluetoothGatt gatt) {
                    BluetoothGattCharacteristic characteristic = gatt.getService(Globals.SNACH_SYSTEM_SERVICE_UUID).getCharacteristic(Globals.SNACH_SYSTEM_UART_TX_UUID);
                    gatt.setCharacteristicNotification(characteristic, true);

                    BluetoothGattDescriptor desc = characteristic.getDescriptor(Globals.SNACH_DESCRIPTOR_UUID);
                    if(desc != null) {
                        desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        Log.i("BLE", "Descriptor write: " + gatt.writeDescriptor(desc));
                    }

                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    resetConnectionTimeoutChecker();

                    Log.i("BLE", "Received characteristics changed event : "+characteristic.getUuid());
                    if(Globals.SNACH_SYSTEM_UART_TX_UUID.equals(characteristic.getUuid())){
                        Log.i("BLE", "Received new update for SnachSystem.");

                        processRawByteData(characteristic.getValue());

                    }

                }

                @Override
                public void onCharacteristicWrite(final BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    resetConnectionTimeoutChecker();

                    Log.i("BLE", "Wrote characteristic : " + characteristic.getUuid() + "   - status: " + status);
                    hasFinishedWriting = true;
                    stopDataLostChecker();

                    // If there was an error (negative Acknowlodgement):
                    // 1) Check Connection -> onConnectionLost reset buffers
                    // 2) Add reset-function: Try to send 3 EOF-Bytes and let the Snach clear all temporary buffers.
                    //    Afterwards retry.
                    // 3) If Reset was not possible reconnect.

                    /*try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/

                    /*Log.i("checker_sendA", "aaci:"+sendAACI+" laci:"+sendLACI+
                            " doBroadcast:"+doBroadcastApp+" EOB:"+hasReachedEndOfBuffer+" replyCursor:"+replyCursor
                            + " sendMoreData:"+sendMoreData+" hasSentClock:"+hasSentClockData+" isPnedingNotif:"+isPendingNotification);
                    if(sendAACI){
                        sendAACI = false;
                        resetReplyBuffers();
                        setUpActionContent(AACI, 0);
                    } else if (sendLACI){
                        sendLACI = false;
                        resetReplyBuffers();
                        setUpListScreen(LACI, false, 0);
                    } else if(doBroadcastApp){
                        resetReplyBuffers();
//                        setUpTestAACI();// <- only for testing purpose!
                        broadcastSelectedApp(SNACH_SCREEN);
                        doBroadcastApp = false;
                    } else if(!hasReachedEndOfBuffer && replyCursor < 100 && sendMoreData){
                        // Continue sending byte packets:
                        sendReplyData(gatt);
                    } /*else if(sendListTitles){
                        sendListTitles = false;
                        resetReplyBuffers();
                        setUpListTitles(LACI);
                    }*/
                    /* // this if was active:
                        else if(!hasSentClockData){
                        hasSentClockData = true;
                        resetReplyBuffers();
                        setUpClockData();
                    }*/
                     /*else if (isPendingNotification){
                        isPendingNotification = false;
                        resetReplyBuffers();
                        pushNotificationInfo();
                    }*/
                    /* else {
                        resetReplyBuffers();
                    }
                    Log.i("checker_sendB", "aaci:"+sendAACI+" laci:"+sendLACI+
                            " doBroadcast:"+doBroadcastApp+" EOB:"+hasReachedEndOfBuffer+" replyCursor:"+replyCursor
                            + " sendMoreData:"+sendMoreData+" hasSentClock:"+hasSentClockData+" isPnedingNotif:"+isPendingNotification);*/
                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    resetConnectionTimeoutChecker();

                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.d("BLE", "Callback: Wrote GATT Descriptor successfully.");

                    } else {
                        Log.d("BLE", "Callback: Error writing GATT Descriptor: "+ status);
                    }
                    hasFinishedWriting = true;
                }
            };

    private void processRawByteData(byte[] data) {
        int[] MPUdata = new int[9];
        MPUdata[0] = (int) data[0];
        MPUdata[1] = (int) data[1];
        MPUdata[2] = (int) data[2];
        MPUdata[3] = (int) data[3];
        MPUdata[4] = (int) data[4];
        MPUdata[5] = (int) data[5];
        MPUdata[6] = (int) data[6];
        MPUdata[7] = (int) data[7];
        MPUdata[8] = (int) data[8];
        for(int md = 0; md < 9; md++){
            if(MPUdata[md] < 0){
                MPUdata[md] += 255;
            }
        }
        processMPUData(MPUdata);

        int[] SYSdata = new int[3];
        SYSdata[0] = (int) data[9]; // Current Screen
        SYSdata[1] = (int) data[10]; // Button Clicked
        SYSdata[2] = (int) data[11]; // Scroll State

        for(byte i : data){
            Log.i("BLE", "received byte: "+i+"   as int: "+(int)i);
        }
        processSnachSystemData(SYSdata);
    }

    private void processSnachSystemData(int[] sySdata) {
        // TODO send layout data and process button events.
        int snachScreenChange = sySdata[0];
        int snachButtonPressed = sySdata[1];
        int snachScrollState = sySdata[2]; //  to check.. remove later..

        int snachScreen = SNACH_SCREEN;
        switch(snachScreenChange){
            case 1:
                // next Screen:
                snachScreen++;
                break;
            case 2:
                // previous Screen:
                snachScreen--;
                break;
            case 3:
                // watch
                snachScreen = 0;
                break;
        }

        // reset screen index to start when end last index is overcome:
        if(snachScreen < 0){
            snachScreen = SNACH_SCREENS_AMOUNT-1;
        }
        if(snachScreen >= SNACH_SCREENS_AMOUNT){ // remove -2 after standard screens are added..
            snachScreen = 0;
        }

        if(!didInitialTransfer){
            SNACH_SCREEN = -1;
            snachScreen = 0;
            hasSentClockData = true;
            didInitialTransfer = true;
        }

        if(!hasSentClockData && SNACH_SCREEN == 0){
            setUpClockData();
            hasSentClockData = true;
        }

        // Log.i("BLE", "Snach Screen: "+SNACH_SCREEN+"  new screen: "+snachScreen+ "    snach screens amount: "+SNACH_SCREENS_AMOUNT);

        if(SNACH_SCREEN != snachScreen) {
            snachScrollState = 0;
            SNACH_SCREEN = snachScreen;
            resetSystemVariables();

            mConnectionListener.onScreenChanged();

            switch (snachScreen) {
                case 0:
                    // Watch
                    // Watch is set up when connection established, so no need to reset it here, just send current time data!
                    hasSentClockData = false;
                    setUpWatchFace();
                    break;
                case 1:
                    // Notifications
                     setUpNotifications(snachScrollState);

                    // setUpCoverScreen(1);// <- only for testing purpose!
                    // setUpTestPopUp();// <- only for testing purpose!

//                    setUpTestAACI();
//                    setUpTestLACI();
                    break;
                /*case 2:// todo caution remove later!!
                    setUpTestAACI();*/

                default:
                    // App or Fitness  / System (Battery...
                    onSnachScreenChanged(snachScreen);
                    break;
            }
        } else if(snachButtonPressed != 0 && pendingBufferItems.size() == 0) {
            /**
             * Do app interaction only if there are no more pending buffer items, so that
             * the user always interacts with the currently shown app.
             * This can be ignored when the global variables AACI, LACI, PUCI etc. are removed,
             * but needs to be done as long as they exist as the current screen mode and one of
             * these current variables are changed as soon as a new screen is set up.
             */
            switch (snachButtonPressed) {
                // todo check screen if tis the watchface or settingsscreen etc which do not need to broadcast an external app
                case 1:
                    // B1 pressed / scroll up
                    if(isSnachScreenScrollable && currentListDataItem >= 0) {
                        boolean doSetUp = false;
                        if(SNACH_SCROLL_STATE > 0) {
                            SNACH_SCROLL_STATE--;
                            doSetUp = true;
                        } else if(currentListDataItem > 0) {
                            currentListDataItem--;
                            broadcastSelectedItemChanged(currentListDataItem);
                            SNACH_SCROLL_STATE = 0;
                            doSetUp = true;
                        }
                        if(doSetUp) {
                            hasSentListEnd = false;
                            Log.i("SCROLLSTATE", "scroll state scrolled up: " + SNACH_SCROLL_STATE);
                            setUpListScreen(LACI, false);
                        }
                    } else {
                        broadcastButtonPressedEvent(SNACH_SCREEN, snachButtonPressed);
                    }
                    break;
                case 2:
                    // B2 pressed
                    if(isShowingPopUp){
                        broadcastPopUpApp(PUCI);
                    } else {
                        broadcastButtonPressedEvent(SNACH_SCREEN, snachButtonPressed);
                    }
                    break;
                case 3:
                    // B3 pressed / scroll down
                    if(isSnachScreenScrollable && !hasSentListEnd) {
                        boolean doSetUp = false;
                        if(currentListDataItem < LACI.getITEMS_TITLE().size() && hasSentDataEnd) {
                            currentListDataItem++;
                            broadcastSelectedItemChanged(currentListDataItem);
                            SNACH_SCROLL_STATE = 0;
                            doSetUp = true;
                        } else if (!hasSentDataEnd) {
                            SNACH_SCROLL_STATE++;
                            doSetUp = true;
                        }

                        if(doSetUp) {
                            Log.i("SCROLLSTATE", "scroll state scrolled down: " + SNACH_SCROLL_STATE);
                            setUpListScreen(LACI, true);
                        }
                    } else {
                        broadcastButtonPressedEvent(SNACH_SCREEN, snachButtonPressed);
                    }
                    break;
                case 4:
                    // B4 pressed
                    if(isShowingPopUp){
                        dismissPopUp();
                    } else {
                        broadcastButtonPressedEvent(SNACH_SCREEN, snachButtonPressed);
                    }
                    break;
            }
        }

        sendPendingBufferData();
    }

    private void broadcastSelectedItemChanged(int currentListDataItem) {
        // todo..
    }

    private void resetSystemVariables() {
        hasSentDataStart = true;
        hasSentDataEnd = true;
        hasSentListEnd = false;
    }

    private void setUpTestLACI() {
        ArrayList<String> titles = new ArrayList<>();
        titles.add("hi");
        titles.add("hii");
        ArrayList<String> contents = new ArrayList<>();
        contents.add("ho  this is a test. Probably you won't be able to see all of this without scrolling");
        contents.add("hoo");

        ListAppContentItem laci = new ListAppContentItem();
        laci.setITEMS_TITLE(titles);
        laci.setITEMS_CONTENT(contents);
        laci.setTOP_BUTTON_ICON((byte) 2);
        laci.setBOTTOM_BUTTON_ICON((byte) 3);
        laci.setSCREEN_MODE(SnachExtras.SCREENMODE_LISTLAYOUT_SCROLLANIM);
        laci.setCOLOR_BACK(SnachExtras.COLOR_WHITE);
        laci.setCOLOR_CONTENT(SnachExtras.COLOR_BLACK);
        laci.setCOLOR_TITLE(SnachExtras.COLOR_BLACK);
        laci.setCOLOR_HIGHLIGHT(SnachExtras.COLOR_BLACK);
        laci.setCOLOR_MAIN(SnachExtras.COLOR_WHITE);
        laci.setBACK_THEME((byte) 0);
        laci.setFONT_THEME((byte) 0);
        laci.setTOP_BUTTON_ICON_ENDSTATE((byte) 0);
        laci.setBOTTOM_BUTTON_ICON_ENDSTATE((byte) 0);
        setListAppData(laci);
    }

    public void updateListScreen(String title_top, String title_bottom, ArrayList TITLES, ArrayList CONTENTS){
        LACI.setListTitle_top(title_top);
        LACI.setListTitle_bottom(title_bottom);
        LACI.setITEMS_TITLE(TITLES);
        LACI.setITEMS_CONTENT(CONTENTS);

        setUpListScreen(LACI, false);
        setUpListTitles(LACI);
    }

    private void setUpListTitles(ListAppContentItem laci) {
        boolean hasData = false;
        byte [] snachReplyBuffer = new byte[Globals.SNACH_MAX_BYTE_TRANSFER];
        snachReplyBuffer[0] = (byte) Globals.BYTE_ID_UPDATE_LISTTITLES;
        snachReplyBuffer[1] = (byte) SNACH_SCREEN;
        snachReplyBuffer[2] = (byte) Globals.BYTE_ID_SCROLL_UP;
        int bufferCursor = 3;
        if(laci.getListTitle_top() != null && laci.getListTitle_top() != ""){
            hasData = true;

            if(laci.getListTitle_top().length() > Globals.SNACH_MAX_LISTTITLE_LENGTH){
                laci.setListTitle_top(laci.getListTitle_top().substring(0,Globals.SNACH_MAX_LISTTITLE_LENGTH));
            }

            snachReplyBuffer[bufferCursor] = (byte) Globals.BYTE_ID_LISTTITLE_TOP;
            bufferCursor++;
            char [] content = LACI.getListTitle_top().toCharArray();
            for(int c = 0; c < content.length; c++){
                snachReplyBuffer[bufferCursor] = (byte) content[c];
                bufferCursor++;
            }
            snachReplyBuffer[bufferCursor] = (byte) Globals.BYTE_ID_LISTTITLE_TOP;
            bufferCursor++;
        }
        if(laci.getListTitle_bottom() != null && laci.getListTitle_bottom() != ""){
            hasData = true;

            if(laci.getListTitle_bottom().length() > Globals.SNACH_MAX_LISTTITLE_LENGTH){
                laci.setListTitle_bottom(laci.getListTitle_bottom().substring(0, Globals.SNACH_MAX_LISTTITLE_LENGTH));
            }

            snachReplyBuffer[bufferCursor] = (byte) Globals.BYTE_ID_LISTTITLE_BOTTOM;
            bufferCursor++;
            char [] content = LACI.getListTitle_bottom().toCharArray();
            for(int c = 0; c < content.length; c++){
                snachReplyBuffer[bufferCursor] = (byte) content[c];
                bufferCursor++;
            }
            snachReplyBuffer[bufferCursor] = (byte) Globals.BYTE_ID_LISTTITLE_BOTTOM;
            bufferCursor++;
        }

        snachReplyBuffer[bufferCursor] = (byte) Globals.SNACH_EOF_BYTE;
        bufferCursor++;
        if(hasData){
            addPendingBufferData(true, false, true, snachReplyBuffer);
        }
    }

    private void setUpTestAACI(){
        ActionAppContentItem aaci = new ActionAppContentItem();
        aaci.setSCREEN_TITLE("TITLE");
        aaci.setSCREEN_CONTENT("Content");
        aaci.setSCREEN_MODE(2);
        aaci.setCOLOR_BACK(SnachExtras.COLOR_WHITE);
        aaci.setCOLOR_CONTENT(SnachExtras.COLOR_BLACK);
        aaci.setCOLOR_TITLE(SnachExtras.COLOR_BLACK);
        aaci.setCOLOR_HIGHLIGHT(SnachExtras.COLOR_BLACK);
        aaci.setCOLOR_MAIN(SnachExtras.COLOR_WHITE);
        aaci.setBACK_THEME((byte) 0);
        aaci.setFONT_THEME((byte) 0);
        aaci.setICON_THEME((byte) 0);
        setActionAppData(aaci);
    }
    private void setUpTestPopUp(){
        PopUpContentItem puci = new PopUpContentItem();
        puci.setLevel(1);
        puci.setPopup_theme(0);
        puci.setIcon_b_left(0);
        puci.setIcon_b_right(0);
        puci.setContent("Content");
        puci.setTitle("PopUp");

        setPopUpScreen(puci);
    }

    private void setUpNotifications(int snachScrollState) {
//        snachReplyBuffer = new byte[Globals.SNACH_MAX_BUFFER_SIZE];

        // get notifications and fill them in Arraylists with title and subtitle

        /*ArrayList<String> demoTitleList = new ArrayList<>();
        ArrayList<String> demoContentList = new ArrayList<>();

        demoTitleList.add("Title1");
        demoTitleList.add("Title2");
        demoTitleList.add("Title3");
        demoContentList.add("abc def ghi");
        demoContentList.add("Content2");
        demoContentList.add("Content3");*/

//        setListAppData(demoTitleList, demoContentList, Globals.SNACH_LIST_LAYOUT_LIGHT);

        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> contents = new ArrayList<>();

        for(SnachNotification sn : snachNotifications){
            titles.add(sn.getTitle());
            contents.add(sn.getContent());
        }
        for(SnachNotificationStandard sns : snachNotificationsStandard){
            titles.add(sns.getTitle());
            contents.add(sns.getContent());
        }

        ListAppContentItem laci = new ListAppContentItem();
        laci.setITEMS_TITLE(titles);
        laci.setITEMS_CONTENT(contents);
        laci.setTOP_BUTTON_ICON((byte) 2);
        laci.setBOTTOM_BUTTON_ICON((byte) 3);
        laci.setSCREEN_MODE(SnachExtras.SCREENMODE_LISTLAYOUT_SCROLLANIM);
        laci.setListTitle_top("Notification");
        laci.setDefaultText("No Notifications");
        laci.setCOLOR_BACK(SnachExtras.COLOR_WHITE);
        laci.setCOLOR_CONTENT(SnachExtras.COLOR_BLACK);
        laci.setCOLOR_TITLE(SnachExtras.COLOR_BLACK);
        laci.setCOLOR_HIGHLIGHT(SnachExtras.COLOR_BLACK);
        laci.setCOLOR_MAIN(SnachExtras.COLOR_WHITE);
        laci.setBACK_THEME((byte) 0);
        laci.setFONT_THEME((byte) 0);
        laci.setTOP_BUTTON_ICON_ENDSTATE((byte)0);
        laci.setBOTTOM_BUTTON_ICON_ENDSTATE((byte)0);
        setListAppData(laci);

    }

    public void setActionAppData(ActionAppContentItem aaci) {
        /**
         * Sets up and pushes a new screen to the Snach.
         */
        Log.i("SET AACI", "title: " + aaci.getSCREEN_TITLE());
        Log.i("SET AACI", "back color: " + aaci.getCOLOR_BACK());

        CURRENT_MODE = aaci.getSCREEN_MODE();

        byte [] layoutBuffer = setUpActionLayout(aaci);
        byte [] contentBuffer = setUpActionContent(aaci);
        addPendingBufferData(layoutBuffer, contentBuffer, aaci);

    }

    public void updateActionData(ActionAppContentItem aaci){
        /**
         * Called to update only the content
         */
        AACI = aaci;
    }

    public void updateListData(ListAppContentItem laci){
        /**
         * Called to update only the content
         */
        LACI = laci;
    }

    private byte [] setUpActionContent(ActionAppContentItem aaci) {
        isSnachScreenScrollable = false;

        int bufferCursor = 0;
        byte [] snachReplyBuffer = new byte[Globals.SNACH_MAX_ACTIONBYTE_TRANSFER];
        snachReplyBuffer[bufferCursor] = (byte)Globals.SNACH_CONTENT_ID_BYTE;
        bufferCursor++;
        snachReplyBuffer[bufferCursor] = (byte)SNACH_SCREEN;
        bufferCursor++;
        snachReplyBuffer[bufferCursor] = (byte) Globals.SNACH_ACTION_TITLE_START_BYTE;
        bufferCursor++;
        int takenSpace = 0;
        String actionAppTitle = aaci.getSCREEN_TITLE();
        if(actionAppTitle.length() > Globals.SNACH_MAX_BYTE_TRANSFER){
            takenSpace = Globals.SNACH_MAX_BYTE_TRANSFER;
            actionAppTitle = actionAppTitle.substring(0, takenSpace);
            takenSpace += bufferCursor;
        } else {
            takenSpace = bufferCursor + actionAppTitle.length();
        }
        char [] charTitle = actionAppTitle.toCharArray();
        for (char c : charTitle) {
            snachReplyBuffer[bufferCursor] = (byte) c;
            bufferCursor++;
        }
        snachReplyBuffer[bufferCursor] = (byte) Globals.SNACH_ACTION_TITLE_END_BYTE;
        bufferCursor++;
        takenSpace++;

        snachReplyBuffer[bufferCursor] = (byte) Globals.SNACH_ACTION_CONTENT_START_BYTE;
        bufferCursor++;
        takenSpace++;
        int remainingSpace = Globals.SNACH_MAX_ACTIONBYTE_TRANSFER - (takenSpace + 2);
        String actionAppSubtitle = aaci.getSCREEN_CONTENT();
        if(actionAppSubtitle.length() > remainingSpace){
            actionAppSubtitle = actionAppSubtitle.substring(0, remainingSpace);
        }
        char [] charSubtitle = actionAppSubtitle.toCharArray();
        for (char c : charSubtitle){
            snachReplyBuffer[bufferCursor] = (byte) c;
            bufferCursor++;
        }
        snachReplyBuffer[bufferCursor] = (byte) Globals.SNACH_ACTION_CONTENT_END_BYTE;
        bufferCursor++;
        snachReplyBuffer[bufferCursor] = (byte) Globals.SNACH_EOF_BYTE;
        bufferCursor++;

        return snachReplyBuffer;
    }

    private byte[] setUpActionLayout(ActionAppContentItem aaci) {
        byte color_back = (byte) aaci.getCOLOR_BACK();
        byte color_main = (byte) aaci.getCOLOR_MAIN();
        color_back = (byte) (color_back << 5);
        color_main = (byte) (color_main << 2);

        byte color_highlight = (byte) aaci.getCOLOR_HIGHLIGHT();
        byte color_hlA = (byte) (color_highlight >> 2);
        byte color_hlB = (byte) (color_highlight & 3);

        byte color_title = (byte) aaci.getCOLOR_TITLE();
        byte color_content = (byte) aaci.getCOLOR_CONTENT();
        color_title = (byte) (color_title << 5);
        color_content = (byte) (color_content << 2);
        byte COLOR_ASSEMB_1 = (byte) (color_back | color_main | color_hlA);
        byte COLOR_ASSEMB_2 = (byte) (color_title | color_content | color_hlB);

        byte [] snachReplyBuffer = new byte[Globals.SNACH_MAX_BYTE_TRANSFER];
        snachReplyBuffer[0] = (byte) aaci.getSCREEN_MODE();

        snachReplyBuffer[1] = aaci.getBACK_THEME();
        snachReplyBuffer[2] = aaci.getICON_THEME();
        snachReplyBuffer[3] = aaci.getFONT_THEME();

        snachReplyBuffer[4] = COLOR_ASSEMB_1;
        snachReplyBuffer[5] = COLOR_ASSEMB_2;

        snachReplyBuffer[6] = (byte) aaci.getMAIN_ICON();
        byte mainIconColors = (byte) ((aaci.getMAIN_ICON_COLOR() << 3) | (aaci.getMAIN_ICON_BACK()));
        snachReplyBuffer[7] = mainIconColors;

        snachReplyBuffer[8] = aaci.getTOP_BUTTON_ICON();
        snachReplyBuffer[9] = aaci.getRIGHT_BUTTON_ICON();
        snachReplyBuffer[10] = aaci.getBOTTOM_BUTTON_ICON();
        snachReplyBuffer[11] = aaci.getLEFT_BUTTON_ICON();

        snachReplyBuffer[12] = aaci.getTOP_BUTTON_STYLE();
        snachReplyBuffer[13] = aaci.getRIGHT_BUTTON_STYLE();
        snachReplyBuffer[14] = aaci.getBOTTOM_BUTTON_STYLE();
        snachReplyBuffer[15] = aaci.getLEFT_BUTTON_STYLE();

        byte colors_topButton = (byte) ((aaci.getTOP_BUTTON_COLOR() << 3) | (aaci.getTOP_BUTTON_BACK()));
        byte colors_rightButton = (byte) ((aaci.getRIGHT_BUTTON_COLOR() << 3) | (aaci.getRIGHT_BUTTON_BACK()));
        byte colors_bottomButton = (byte) ((aaci.getBOTTOM_BUTTON_COLOR() << 3) | (aaci.getBOTTOM_BUTTON_BACK()));
        byte colors_leftButton = (byte) ((aaci.getLEFT_BUTTON_COLOR() << 3) | (aaci.getLEFT_BUTTON_BACK()));

        snachReplyBuffer[16] = colors_topButton;
        snachReplyBuffer[17] = colors_rightButton;
        snachReplyBuffer[18] = colors_bottomButton;
        snachReplyBuffer[19] = colors_leftButton;

        return snachReplyBuffer;
    }

    private void dismissPopUp(){
        switch (PUCI.getPOP_UP_LAYER()){
            case 1:
                // Call dismissed
                break;
        }
    }

    private void sendPopupDismiss(){
        byte [] snachReplyBuffer = new byte[2];
        snachReplyBuffer[0] = Globals.SNACH_DISMISS_POPUP;
        snachReplyBuffer[1] = (byte) Globals.SNACH_EOF_BYTE;

        addPendingBufferData(false, true, false, snachReplyBuffer);
    }

    private void onPopUpActionPressed(){
        /**
         * Called when right button is pressed while the pop up is displayed.
         */
        switch(PUCI.getPOP_UP_LAYER()){
            case 0:
                // App action
                broadcastPopUpApp(PUCI);
                break;
            case 1:
                // Telephone call accepted
                break;
        }
        sendPopupDismiss();
    }

    public void setPopUpScreen(PopUpContentItem puci){
        isShowingPopUp = true;
        // todo remove global variables LACI AACI and PUCI or set them when the corresponding bufferitem is sent
        this.PUCI = puci;

        byte [] snachReplyBuffer = new byte[Globals.SNACH_MAX_POPUPBYTE_TRANSFER];
        snachReplyBuffer[0] = SnachExtras.POP_UP_SCREEN;
        snachReplyBuffer[1] = (byte) puci.getPopup_theme();
        snachReplyBuffer[2] = (byte) puci.getIcon_b_left();
        snachReplyBuffer[3] = (byte) puci.getIcon_b_right();
        snachReplyBuffer[4] = (byte) puci.getLevel();

        char[] title = puci.getTitle().toCharArray();
        int bufferCursor = 5;
        for(int c = 0; c < title.length; c++){
            if(bufferCursor == Globals.SNACH_MAX_BYTE_TRANSFER-2){
                break;
            }
            snachReplyBuffer[bufferCursor] = (byte) title[c];
            bufferCursor++;
        }

        snachReplyBuffer[bufferCursor] = Globals.SNACH_ITEM_SAPARATOR_BYTE;
        bufferCursor++;

        char[] content = puci.getContent().toCharArray();
        for(int c = 0; c < content.length; c++){
            if(bufferCursor == Globals.SNACH_MAX_BYTE_TRANSFER-1){
                break;
            }
            snachReplyBuffer[bufferCursor] = (byte) content[c];
            bufferCursor++;
        }

        snachReplyBuffer[bufferCursor] = (byte) Globals.SNACH_EOF_BYTE;

        addPendingBufferData(false, true, false, snachReplyBuffer);
    }

    public void setListAppData(ListAppContentItem laci){
        /**
         * Sets up and pushes a new screen to the Snach.
         */
        CURRENT_MODE = laci.getSCREEN_MODE();

        LACI = laci;

        SNACH_SCROLL_STATE = 0;
        currentListDataItem = 0;
        hasSentDataStart = true;

        isSnachScreenScrollable = true;
        hasSentListEnd = false;

        byte [] layoutBuffer = setUpListLayout(laci);
        byte [] contentBuffer = setUpListContent(currentListDataItem, SNACH_SCROLL_STATE, laci, false);
        addPendingBufferData(layoutBuffer, contentBuffer, laci);
    }

    private byte [] setUpListLayout(ListAppContentItem laci) {
        byte color_back = (byte) laci.getCOLOR_BACK();
        byte color_main = (byte) laci.getCOLOR_MAIN();
        color_back = (byte) (color_back << 6);
        color_main = (byte) (color_main << 2);

        byte color_highlight = (byte) laci.getCOLOR_HIGHLIGHT();
        byte color_hlA = (byte) (color_highlight & 9);
        byte color_hlB = (byte) (color_highlight & 4);

        byte color_title = (byte) laci.getCOLOR_TITLE();
        byte color_content = (byte) laci.getCOLOR_CONTENT();
        color_title = (byte) (color_title << 6);
        color_content = (byte) (color_content << 2);
        byte COLOR_ASSEMB_1 = (byte) (color_back | color_main | color_hlA);
        byte COLOR_ASSEMB_2 = (byte) (color_title | color_content | color_hlB);

        byte [] snachReplyBuffer = new byte[Globals.SNACH_MAX_BYTE_TRANSFER];
        snachReplyBuffer[0] = (byte) laci.getSCREEN_MODE();

        snachReplyBuffer[1] = laci.getBACK_THEME();
        snachReplyBuffer[2] = laci.getFONT_THEME();

        snachReplyBuffer[3] = COLOR_ASSEMB_1;
        snachReplyBuffer[4] = COLOR_ASSEMB_2;

        snachReplyBuffer[5] = laci.getITEM_THEME();

        snachReplyBuffer[6] = laci.getTOP_BUTTON_ICON();
        snachReplyBuffer[7] = laci.getRIGHT_BUTTON_ICON();
        snachReplyBuffer[8] = laci.getBOTTOM_BUTTON_ICON();
        snachReplyBuffer[9] = laci.getLEFT_BUTTON_ICON();
        snachReplyBuffer[10] = laci.getTOP_BUTTON_ICON_ENDSTATE();
        snachReplyBuffer[11] = laci.getBOTTOM_BUTTON_ICON_ENDSTATE();

        snachReplyBuffer[12] = laci.getTOP_BUTTON_STYLE_ENDSTATE();
        snachReplyBuffer[13] = laci.getRIGHT_BUTTON_STYLE();
        snachReplyBuffer[14] = laci.getBOTTOM_BUTTON_STYLE_ENDSTATE();
        snachReplyBuffer[15] = laci.getLEFT_BUTTON_STYLE();

        byte colors_topButton = (byte) ((laci.getTOP_BUTTON_COLOR() << 3) | (laci.getTOP_BUTTON_BACK()));
        byte colors_rightButton = (byte) ((laci.getRIGHT_BUTTON_COLOR() << 3) | (laci.getRIGHT_BUTTON_BACK()));
        byte colors_bottomButton = (byte) ((laci.getBOTTOM_BUTTON_COLOR() << 3) | (laci.getBOTTOM_BUTTON_BACK()));
        byte colors_leftButton = (byte) ((laci.getLEFT_BUTTON_COLOR() << 3) | (laci.getLEFT_BUTTON_BACK()));

        snachReplyBuffer[16] = colors_topButton;
        snachReplyBuffer[17] = colors_rightButton;
        snachReplyBuffer[18] = colors_bottomButton;
        snachReplyBuffer[19] = colors_leftButton;

        return snachReplyBuffer;
    }

    private void setUpListScreen(ListAppContentItem laci, boolean scrollDown) {
        isSnachScreenScrollable = true;

        byte [] bufferArray;
        if (scrollDown) {
            if (hasSentDataEnd) {
                bufferArray = setUpListContent(currentListDataItem, SNACH_SCROLL_STATE, laci, scrollDown);
            } else {
                bufferArray = sendMissingItemPartsContent(currentListDataItem, SNACH_SCROLL_STATE, SNACH_SCREEN, laci, scrollDown);
            }
        } else {
            if(hasSentDataStart) {
                bufferArray = setUpListContent(currentListDataItem, SNACH_SCROLL_STATE, laci, scrollDown);
            } else {
                bufferArray = sendMissingItemPartsContent(currentListDataItem, SNACH_SCROLL_STATE, SNACH_SCREEN, laci, scrollDown);
            }
        }

        for (Byte b : bufferArray) {
            Log.i("BYTE", "" + b);
        }

        addPendingBufferData(false, true, false, bufferArray);
    }

    private void addPendingBufferData(byte[] layoutBuffer, byte[] contentBuffer, ActionAppContentItem aaci){
        BufferItem bufferItem = new BufferItem();
        bufferItem.setHasLayout(true);
        bufferItem.setHasContent(true);
        bufferItem.setHasReachedEOB(false);
        bufferItem.setActionAppContentItem(aaci);
        bufferItem.setMODE(aaci.getSCREEN_MODE());
        bufferItem.setBUFFER_CONTENT(contentBuffer);
        bufferItem.setBUFFER_LAYOUT(layoutBuffer);
        if(pendingBufferItems.size() < Globals.MAX_PENDING_BUFFERITEMS) {
            pendingBufferItems.add(bufferItem);
        }
    }

    private void addPendingBufferData(byte[] layoutBuffer, byte[] contentBuffer, ListAppContentItem laci){
        BufferItem bufferItem = new BufferItem();
        bufferItem.setHasLayout(true);
        bufferItem.setHasContent(true);
        bufferItem.setHasReachedEOB(false);
        bufferItem.setListAppContentItem(laci);
        bufferItem.setMODE(laci.getSCREEN_MODE());
        bufferItem.setBUFFER_CONTENT(contentBuffer);
        bufferItem.setBUFFER_LAYOUT(layoutBuffer);
        if(pendingBufferItems.size() < Globals.MAX_PENDING_BUFFERITEMS) {
            pendingBufferItems.add(bufferItem);
        }
    }

    private void addPendingBufferData(boolean hasLayout, boolean hasContent, boolean hasReachedEOB, byte [] bufferArray){
        BufferItem bufferItem = new BufferItem();
        bufferItem.setHasLayout(hasLayout);
        bufferItem.setHasContent(hasContent);
        bufferItem.setHasReachedEOB(hasReachedEOB);
        if(hasContent) {
            bufferItem.setBUFFER_CONTENT(bufferArray);
        } else if (hasLayout){
            bufferItem.setBUFFER_LAYOUT(bufferArray);
        }

        if(pendingBufferItems.size() < Globals.MAX_PENDING_BUFFERITEMS) {
            // todo add callback for fail of this if loop
            pendingBufferItems.add(bufferItem);
        }
    }

    private byte [] sendMissingItemPartsContent(int currentListDataItem, int snachScrollState, int snachScreen,
                                                ListAppContentItem laci, boolean scrollDown) {
        // TODO  Read data from contentDataSentStart to contentDataSentEnd, update those two vairables and send it. If the buffer reached the end of the item, set
        // TODO  hasSentDataEnd to true and set hasSentDataStart to false.
        //hasSentListEnd = false; //<- todo check for listend..

        byte [] snachReplyBuffer = new byte[Globals.SNACH_MAX_LISTBYTE_TRANSFER];
        snachReplyBuffer[0] = (byte)Globals.BYTE_ID_ATTACH_CONTENT_TO_ITEM;
        snachReplyBuffer[1] = (byte) SNACH_SCREEN;
        if(scrollDown) {
            snachReplyBuffer[2] = (byte) Globals.BYTE_ID_SCROLL_DOWN;
        } else {
            snachReplyBuffer[2] = (byte) Globals.BYTE_ID_SCROLL_UP;
        }
        int bufferCursor = 3;

        byte contentStartByte = Globals.SNACH_LISTITEM_CONTENT_START_BYTE;
        byte contentEndByte = Globals.SNACH_LISTITEM_CONTENT_END_BYTE;
        if(currentListDataItem == laci.getITEMS_TITLE().size()-1){
            contentEndByte = Globals.SNACH_LISTITEM_CONTENT_END_BYTE_LASTITEM;
        }

        boolean overflow = false;
        String content = (String) laci.getITEMS_CONTENT().get(currentListDataItem);

        if(scrollDown) {
            snachReplyBuffer[bufferCursor] = contentStartByte;
            bufferCursor++;


            contentDataSentStart = contentDataSentEnd;
            content = content.substring(contentDataSentStart);
            char [] charContent = content.toCharArray();
            for(int c = 0; c < content.length(); c++){
                contentDataSentEnd = contentDataSentStart + c;
                if(bufferCursor == Globals.SNACH_MAX_LISTBYTE_TRANSFER-1) {
                    overflow = true;
                    //contentDataSentEnd = contentDataSentStart + c;
                    break;
                }
                snachReplyBuffer[bufferCursor] = (byte) charContent[c];
                bufferCursor++;
            }
            if(!overflow){
                snachReplyBuffer[bufferCursor] = contentEndByte;
                hasSentDataEnd = true;
                hasSentDataStart = false;
                bufferCursor++;
                if(laci.getITEMS_CONTENT().size() == currentListDataItem+1){
                    hasSentListEnd = true;
                }
            } else {
                hasSentDataStart = false;
                hasSentDataEnd = false;
            }
        } else {
            hasSentDataStart = false;

            if(snachScrollState == 0) {
                // Title data:
                snachReplyBuffer[bufferCursor] = Globals.SNACH_LISTITEM_TITLE_START_BYTE;
                if (currentListDataItem == 0) {
                    snachReplyBuffer[bufferCursor] = Globals.SNACH_LISTITEM_TITLE_START_BYTE_FIRSTITEM;
                }
                bufferCursor++;
                String title = (String) laci.getITEMS_TITLE().get(currentListDataItem);
                char[] charTitle = title.toCharArray();
                for (int c = 0; c < charTitle.length; c++) {
                    if (c == Globals.SNACH_MAX_BYTE_TRANSFER - 1) {
                        // Max length of title = 20
                        break;
                    }
                    snachReplyBuffer[bufferCursor] = (byte) charTitle[c];
                    bufferCursor++;
                }
                snachReplyBuffer[bufferCursor] = Globals.SNACH_LISTITEM_TITLE_END_BYTE;
                bufferCursor++;
            }

            snachReplyBuffer[bufferCursor] = contentStartByte;
            bufferCursor++;

            // set beginning to the raw text position without id-bytes:
            int beginning = contentDataSentStart - (Globals.SNACH_MAX_LISTBYTE_TRANSFER - bufferCursor - 1);
            if(beginning <= 0){
                beginning = 0;
                hasSentDataStart = true;
            }

            content = content.substring(beginning);//, contentDataSentStart); // try to send as much as possible, if there is too much then
            contentDataSentStart = beginning;

            char [] charContent = content.toCharArray();
            for(int c = 0; c < charContent.length; c++){
                if(bufferCursor == Globals.SNACH_MAX_LISTBYTE_TRANSFER-1) {
                    overflow = true;
                    contentDataSentEnd = contentDataSentStart + c;
                    break;
                }
                snachReplyBuffer[bufferCursor] = (byte) charContent[c];
                bufferCursor++;
            }
            if(!overflow){
                snachReplyBuffer[bufferCursor] = contentEndByte;
                hasSentDataEnd = true;
                bufferCursor++;
                if(laci.getITEMS_CONTENT().size() == currentListDataItem+1){
                    hasSentListEnd = true;
                }
            } else {
                hasSentDataEnd = false;
            }
        }

        snachReplyBuffer[bufferCursor] = (byte) Globals.SNACH_EOF_BYTE;// [bufferCursor
        bufferCursor++;

        return snachReplyBuffer;
    }

    private byte [] setUpListContent(int currentListDataItem, int snachScrollState, ListAppContentItem laci, boolean scrollDown) {
        /**
         * Set up and send a new ListItem. This will attach a start byte to the sent data
         * and reset all variables set in the "sendMissingItemPartsContent" method.
         *
         * TODO:
         * This also prepares the content data according to the item theme specified in the ListAppContentItem,
         * so that the Snach can react on id-bytes, e.g. if the endbyte is missing it will send a scroll request on
         * its on scroll event, but if the data is cropped to the "right" size in this method the Snach will be able to
         * detect the end byte and thus send a NewItem request on its on scroll event.
         */
        byte [] snachReplyBuffer = new byte [Globals.SNACH_MAX_LISTBYTE_TRANSFER];
        int bufferCursor = 0;
        snachReplyBuffer[bufferCursor] = (byte)Globals.SNACH_CONTENT_ID_BYTE;
        bufferCursor++;
        snachReplyBuffer[bufferCursor] = (byte)SNACH_SCREEN;
        bufferCursor++;
        if(scrollDown) {
            snachReplyBuffer[bufferCursor] = (byte) Globals.BYTE_ID_SCROLL_DOWN;
        } else {
            snachReplyBuffer[bufferCursor] = (byte) Globals.BYTE_ID_SCROLL_UP;
        }
        bufferCursor++;

        /*if(!scrollDown) {
            SNACH_SCROLL_STATE = currentListDataItem;
        }*/

        // Title data:
        snachReplyBuffer[bufferCursor] = Globals.SNACH_LISTITEM_TITLE_START_BYTE;
        if(currentListDataItem == 0){
            snachReplyBuffer[bufferCursor] = Globals.SNACH_LISTITEM_TITLE_START_BYTE_FIRSTITEM;
        }
        bufferCursor++;
        String title = "";
        if(laci.getITEMS_TITLE().size() > 0) {
            title = (String) laci.getITEMS_TITLE().get(currentListDataItem);
        }
        char[] charTitle = title.toCharArray();
        for(int c  = 0; c < charTitle.length; c++){
            if(c==Globals.SNACH_MAX_BYTE_TRANSFER-1){
                // Max length of title = 20
                break;
            }
            snachReplyBuffer[bufferCursor] = (byte)charTitle[c];
            bufferCursor++;
        }
        snachReplyBuffer[bufferCursor] = Globals.SNACH_LISTITEM_TITLE_END_BYTE;
        bufferCursor++;

        byte contentStartByte = Globals.SNACH_LISTITEM_CONTENT_START_BYTE;
        byte contentEndByte = Globals.SNACH_LISTITEM_CONTENT_END_BYTE;
        if(currentListDataItem == laci.getITEMS_TITLE().size()-1){
            contentEndByte = Globals.SNACH_LISTITEM_CONTENT_END_BYTE_LASTITEM;
        }

        // Content data:
        boolean overflow = false;
        String content = laci.getDefaultText();
        if(laci.getITEMS_CONTENT() != null){
            if(laci.getITEMS_CONTENT().size() > 0){
                content = (String) laci.getITEMS_CONTENT().get(currentListDataItem);
            }

            if(content.length() != 0) {
                snachReplyBuffer[bufferCursor] = contentStartByte;
                hasSentDataStart = true;
                bufferCursor++;

                char[] charContent = content.toCharArray();
                contentDataSentStart = 0;
                for(int c = 0; c < charContent.length; c++){
                    if(bufferCursor == Globals.SNACH_MAX_LISTBYTE_TRANSFER-1) {
                        overflow = true;
                        contentDataSentEnd = c;
                        break;
                    }
                    snachReplyBuffer[bufferCursor] = (byte) charContent[c];
                    bufferCursor++;
                }
                if(!overflow) {
                    snachReplyBuffer[bufferCursor] = contentEndByte;
                    hasSentDataEnd = true;
                    //contentDataSentStart = 0;
                    contentDataSentEnd = 0;
                    bufferCursor++;
                    if(laci.getITEMS_CONTENT().size() == currentListDataItem+1){
                        hasSentListEnd = true;
                    }
                } else {
                    // todo set contentDataSentEnd to last complete word and add EOF byte directly behind.
                    hasSentDataEnd = false;
                }
                Log.i("datatrans", "isOverflow: "+overflow+"    data sent start: "+contentDataSentStart+"    data sent end: "+contentDataSentEnd+ "   content length: "+content.length());
            }
        }
        // If overflow then ending byte will be missing, so snach will know that there is more data to expect when user scrolls
        snachReplyBuffer[bufferCursor] = (byte) Globals.SNACH_EOF_BYTE;// [itemCursor
        bufferCursor++;

        return snachReplyBuffer;
    }

    private void onSnachScreenChanged(int snachScreen) {
        /*if(snachScreen == SNACH_SCREENS_AMOUNT-2){
            // fitness
            isSnachScreenScrollable = false;
        } else if(snachScreen == SNACH_SCREENS_AMOUNT-1){
            // system data
            isSnachScreenScrollable = false;
        } else {*/
            // app
            setUpCoverScreen(snachScreen);
            broadcastSelectedApp(SNACH_SCREEN);
            // setUp Screen is called by StramService;
            // Just show cover screen on snach with title of the app
            // which was provided when it registered itself for the
            // snach screen.
        /*}*/
    }

    private void setUpCoverScreen(int snachScreen) {
        isSnachScreenScrollable = false;

        String title = "Loading";
        if(snachScreen == 1){
            // Notifications
            title = "Notification";
        } else {
            int appIndex = snachScreen-2;
            SnachAppItem sap= snachAppScreens.get(appIndex);
            title = sap.getAppName();
        }

        byte [] snachReplyBuffer = new byte[20];
        snachReplyBuffer[0] = Globals.SNACH_COVER_LAYOUT;// CoverLayout
        snachReplyBuffer[1] = (byte)snachScreen;// Snach Screen

        byte colors = (byte)((SnachExtras.COLOR_BLACK << 3) | ((byte)SnachExtras.COLOR_WHITE));
        snachReplyBuffer[2] = colors;
        snachReplyBuffer[3] = (byte) SnachExtras.THEME_COVER_STANDARD;

        int bufferCursor = 4;

        //snachReplyBuffer[2] = (byte)Globals.SNACH_APP_TITLE_START_BYTE;
        char[] charTitle = title.toCharArray();
        for(int c  = 0; c < charTitle.length; c++){
            if(bufferCursor==Globals.SNACH_MAX_BYTE_TRANSFER-1){
                break;
            }
            snachReplyBuffer[bufferCursor] = (byte)charTitle[c];
            bufferCursor++;
        }
        snachReplyBuffer[bufferCursor] = (byte) Globals.SNACH_EOF_BYTE;
        CURRENT_MODE = Globals.SNACH_COVER_LAYOUT;

        addPendingBufferData(false, true, false, snachReplyBuffer);
    }

    private void broadcastSelectedApp(int snachScreen) {
        int appIndex = snachScreen-2;
        SnachAppItem sap= snachAppScreens.get(appIndex);

        Intent appIntent = new Intent();
        appIntent.setAction(sap.getAppBCAction());
        appIntent.putExtra(SnachExtras.APP_SPECIAL_EXTRA, sap.getAppBCExtra());
        appIntent.putExtra(SnachExtras.APP_SNACH_EVENT, SnachExtras.APP_SCREEN_REQUEST);

        Log.i("broadcaster", "app action: "+sap.getAppBCAction());
        context.sendBroadcast(appIntent);
    }

    private void broadcastPopUpApp(PopUpContentItem puci){
        /**
         * Called when the user chooses to do the pop up action instead of dismissing it
         */

        Intent appIntent = new Intent();
        appIntent.setAction(puci.getAppBCAction());
        context.sendBroadcast(appIntent);
    }

    private void broadcastButtonPressedEvent(int snachScreen, int buttonID){
        int appIndex = snachScreen-2;
        SnachAppItem sap= snachAppScreens.get(appIndex);

        Intent appIntent = new Intent();
        appIntent.setAction(sap.getAppBCAction());
        mConnectionListener.onButtonPressed(buttonID);

        switch(buttonID){
            case 1:
                appIntent.putExtra(SnachExtras.APP_SNACH_EVENT,  SnachExtras.APP_BUTTON_TOP);
                break;
            case 2:
                appIntent.putExtra(SnachExtras.APP_SNACH_EVENT,  SnachExtras.APP_BUTTON_RIGHT);
                break;
            case 3:
                appIntent.putExtra(SnachExtras.APP_SNACH_EVENT,  SnachExtras.APP_BUTTON_BOTTOM);
                break;
            case 4:
                appIntent.putExtra(SnachExtras.APP_SNACH_EVENT,  SnachExtras.APP_BUTTON_LEFT);
                break;
        }

        context.sendBroadcast(appIntent);
    }

    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    private int dayOfMonth = 0;
    private int month = 0;
    private int year = 0;
    private void setUpClockData() {
        isSnachScreenScrollable = false;

        Calendar cal = Calendar.getInstance();

        byte [] snachReplyBuffer = new byte[7];
        snachReplyBuffer[0] = (byte) Globals.SNACH_CONTENT_ID_BYTE;
        snachReplyBuffer[1] = 0; // snach screen
        snachReplyBuffer[2] = (byte) cal.get(Calendar.HOUR);
        snachReplyBuffer[3] = (byte) cal.get(Calendar.MINUTE);
        snachReplyBuffer[4] = (byte) cal.get(Calendar.SECOND);
        snachReplyBuffer[5] = 0; // Millis starting from 0.
        snachReplyBuffer[6] = (byte)254;
        // send current time and date..

        addPendingBufferData(false, true, false, snachReplyBuffer);
    }

    private boolean onSecondChanged() {
        // TODO compare last sent time to current time and return true if difference is greater than one second
        return false;
    }

    private void setUpWatchFace() {
        byte [] snachReplyBuffer = new byte[72];
        // System Data:
        snachReplyBuffer[0]= Globals.SNACH_WATCHFACE;// ScreenMode
        snachReplyBuffer[1]= (byte) SNACH_SCREEN;// reserveByte -> current Screen
        // Watchface Layout Data:
        /*
            TODO
            Provide Watch Face section in App to let user choose a watch face.
            After selecting, save the shared preferences specifying the watch face
            locally in the SnachBackgroundProcess (Like this also other apps can specify
            watch faces without needing to share all of their shared preferences with the
            Snach App)
        */
        // Type:
        snachReplyBuffer[2] = Globals.WATCHFACE_CLASSIC; // 1, 2: Watchface; 3, 4: Custom Watch
        // Clock Face:
        snachReplyBuffer[3] = (byte)1;
        snachReplyBuffer[4] = (byte)64;
        snachReplyBuffer[5] = (byte)1;
        snachReplyBuffer[6] = 12;
        snachReplyBuffer[7] = 3;
        snachReplyBuffer[8] = 1;
        snachReplyBuffer[9] = (byte)(128 & (0xff));
        snachReplyBuffer[10] = (byte)(128 & (0xff));
        snachReplyBuffer[11] = (byte)(1 & (0xff));
        snachReplyBuffer[12] = (byte)(1 & (0xff));
        snachReplyBuffer[13] = (byte)(2 & (0xff));
        snachReplyBuffer[14] = (byte)(2 & (0xff));
        snachReplyBuffer[15] = (byte)(2 & (0xff));
        snachReplyBuffer[16] = (byte)(1 & (0xff));
        snachReplyBuffer[17] = (byte)(0 & (0xff)); // End of Clock Face
        // Object
        snachReplyBuffer[18] = (byte)(3 & (0xff));
        snachReplyBuffer[19] = (byte)(4 & (0xff));
        snachReplyBuffer[20] = (byte)(128 & (0xff));
        snachReplyBuffer[21] = (byte)(128 & (0xff));
        snachReplyBuffer[22] = (byte)(128 & (0xff));
        snachReplyBuffer[23] = (byte)(74 & (0xff));
        snachReplyBuffer[24] = (byte)(4 & (0xff));
        snachReplyBuffer[25] = (byte)(1 & (0xff));
        snachReplyBuffer[26] = (byte)(2 & (0xff));
        snachReplyBuffer[27] = (byte)(6 & (0xff));
        snachReplyBuffer[28] = (byte)(17 & (0xff));// 1 round
        snachReplyBuffer[29] = (byte)(3 & (0xff));// per minute
        snachReplyBuffer[30] = (byte)(128 & (0xff));
        snachReplyBuffer[31] = (byte)(128 & (0xff));
        snachReplyBuffer[32] = (byte)(1 & (0xff));
        snachReplyBuffer[33] = (byte)(10 & (0xff)); // move each 10*10^3ms
        snachReplyBuffer[34] = (byte)(1 & (0xff));
        snachReplyBuffer[35] = (byte)(0 & (0xff));
        // Object
        snachReplyBuffer[36] = (byte)(3 & (0xff));
        snachReplyBuffer[37] = (byte)(4 & (0xff));
        snachReplyBuffer[38] = (byte)(128 & (0xff));
        snachReplyBuffer[39] = (byte)(128 & (0xff));
        snachReplyBuffer[40] = (byte)(128 & (0xff));
        snachReplyBuffer[41] = (byte)(84 & (0xff));
        snachReplyBuffer[42] = (byte)(2 & (0xff));
        snachReplyBuffer[43] = (byte)(1 & (0xff));
        snachReplyBuffer[44] = (byte)(2 & (0xff));
        snachReplyBuffer[45] = (byte)(6 & (0xff));
        snachReplyBuffer[46] = (byte)(33 & (0xff));// 2 rounds
        snachReplyBuffer[47] = (byte)(1 & (0xff));// per day
        snachReplyBuffer[48] = (byte)(128);
        snachReplyBuffer[49] = (byte)(128);
        snachReplyBuffer[50] = (byte)(1 & (0xff));
        snachReplyBuffer[51] = (byte)(10 & (0xff)); // move each 1*10^3ms
        snachReplyBuffer[52] = (byte)(1 & (0xff));
        snachReplyBuffer[53] = (byte)(0 & (0xff));
        // Object
        snachReplyBuffer[54] = (byte)(3 & (0xff));
        snachReplyBuffer[55] = (byte)(4 & (0xff));
        snachReplyBuffer[56] = (byte)(128 & (0xff));
        snachReplyBuffer[57] = (byte)(128 & (0xff));
        snachReplyBuffer[58] = (byte)(128 & (0xff));
        snachReplyBuffer[59] = (byte)(84 & (0xff));
        snachReplyBuffer[60] = (byte)(2 & (0xff));
        snachReplyBuffer[61] = (byte)(1 & (0xff));
        snachReplyBuffer[62] = (byte)(2 & (0xff));
        snachReplyBuffer[63] = (byte)(6 & (0xff));
        snachReplyBuffer[64] = (byte)(17 & (0xff));// 1 round
        snachReplyBuffer[65] = (byte)(2 & (0xff));// per hour
        snachReplyBuffer[66] = (byte)(128);
        snachReplyBuffer[67] = (byte)(128);
        snachReplyBuffer[68] = (byte)(1 & (0xff));
        snachReplyBuffer[69] = (byte)(10 & (0xff)); // move each 1*10^3ms
        snachReplyBuffer[70] = (byte)(1 & (0xff));
        snachReplyBuffer[71] = (byte) (254); // EOF

        addPendingBufferData(false, true, false, snachReplyBuffer);
    }

    private void sendPendingBufferData(){
        if(pendingBufferItems.size() > 0 && hasFinishedWriting){

            BufferItem bufferItem = pendingBufferItems.get(0);
            byte [] buffer = new byte[20];

            if(bufferItem.isHasLayout()){
                buffer = bufferItem.getBUFFER_LAYOUT();
                pendingBufferItems.get(0).setHasLayout(false);
            } else if(bufferItem.isHasContent() && !bufferItem.isHasReachedEOB()){
                int bufferMaxIndex = bufferItem.getBUFFER_CONTENT().length-1;
                if(bufferMaxIndex < bufferItem.getBufferIndex2()){
                    bufferItem.setBufferIndex2(bufferMaxIndex);
                }

                Log.i("sender", "bi1: "+bufferItem.getBufferIndex1() + "    bi2: " + bufferItem.getBufferIndex2());
                try {
                    buffer = Arrays.copyOfRange(bufferItem.getBUFFER_CONTENT(), bufferItem.getBufferIndex1(), bufferItem.getBufferIndex2() + 1);

                    if (bufferItem.getBufferIndex2() >= bufferMaxIndex || checkForEOB(buffer)) {
                        pendingBufferItems.get(0).setHasContent(false);
                        pendingBufferItems.get(0).setHasReachedEOB(true);
                    } else {
                        pendingBufferItems.get(0).setBufferIndex1(bufferItem.getBufferIndex1() + 20);
                        pendingBufferItems.get(0).setBufferIndex2(bufferItem.getBufferIndex2() + 20);
                    }
                } catch (IllegalArgumentException ae){
                    ae.printStackTrace();
                    // Remove item and stop execution of this method to prevent blocking the data stream:
                    pendingBufferItems.remove(0);
                    return;
                }
            }

            sendBufferedData(buffer, gatt);

            Log.i("sender", "hasLayout: "+pendingBufferItems.get(0).isHasLayout()+"    hasContent: "+pendingBufferItems.get(0).isHasContent()+ "   hasEOB:"+pendingBufferItems.get(0).isHasReachedEOB());
            if(!pendingBufferItems.get(0).isHasLayout() && !pendingBufferItems.get(0).isHasContent() && pendingBufferItems.get(0).isHasReachedEOB()){
                pendingBufferItems.remove(0);
            }
        }
    }

    private boolean checkForEOB(byte[] buffer){
        for(int b = 0; b < buffer.length; b++){
            if(buffer[b] == (byte)Globals.SNACH_EOF_BYTE){
                return true;
            }
        }
        return false;
    }

    private void sendBufferedData(byte [] bufferedData, BluetoothGatt gatt){
        /**
         * Called by sendPendingBufferData(). Do not call separately!
         */
        for(int i = 0; i < bufferedData.length; i++) {
            Log.i("BLE", "buffer value at index " + i + " is "+bufferedData[i]);
        }
        BluetoothGattCharacteristic characteristic = gatt.getService(Globals.SNACH_SYSTEM_SERVICE_UUID).getCharacteristic(Globals.SNACH_SYSTEM_UART_RX_UUID);
        if (characteristic != null) {
            hasFinishedWriting = false;
            startDataLostChecker();
            characteristic.setValue(bufferedData);
            gatt.writeCharacteristic(characteristic);
        }
    }

    private void disconnectSnach() {
        closeGatt();
    }

    private void saveConnectionStatus(boolean isConnected) {
        sharedDevice.edit().putBoolean(Globals.KEY_DEVICE_ISPAIRED, isConnected).apply();
    }

    private void sendBroadcastConnectionChanged(boolean isSnachConnected) {
        /**
         * Broadcasts all interested apps when a connectionChange event occurs
         */
        saveConnectionStatus(isSnachConnected);
        Intent intent = new Intent();
        intent.setAction(SnachExtras.INTENT_ACTION_CONNECTION);
        intent.putExtra(SnachExtras.INTENT_EXTRA_CONNECTED, isSnachConnected);
        context.sendStickyBroadcast(intent);
    }

    private BluetoothDevice createBluetoothDevice(String DEVICE_ADRESS) {
        return mBluetoothAdapter.getRemoteDevice(DEVICE_ADRESS);
    }

    public void shutDown(){
        disconnectSnach();
        sendBroadcastConnectionChanged(isSnachConnected);
    }

    public boolean isConnected(){
        return isSnachConnected;
    }

    public void startGestureRecording(String extraApp, String extraMode, String gestureName, String gestureAction) {
        gestureHandler.prepareGestureRecording(extraApp, extraMode, gestureName, gestureAction);
    }

    @Override
    public void OnGestureAdded() {
        Intent recordIntent = new Intent();
        recordIntent.setAction(SnachExtras.INTENT_ACTION_GESTURE_RECORDING);
        recordIntent.putExtra(SnachExtras.INTENT_EXTRA_RECORDING_ATTEMPT, -2);
        recordIntent.putExtra(SnachExtras.INTENT_EXTRA_IS_RECORDING_COMPLETE, true);
        context.sendBroadcast(recordIntent);

        initSavedGestures();
        gestureHandler = new GestureProcessingHandler(gestureCharacteristicsData, context, this);

        /*gestureProcess.resetHandler(gestureHandler);*/
    }

    @Override
    public void OnGestureAttempt(int attempt) {
        Intent recordIntent = new Intent();
        recordIntent.setAction(SnachExtras.INTENT_ACTION_GESTURE_RECORDING);
        recordIntent.putExtra(SnachExtras.INTENT_EXTRA_RECORDING_ATTEMPT, attempt);
        context.sendBroadcast(recordIntent);
    }

    // G-Data:
    private void processMPUData(int[] MPUdata) {
        GesturePoint gp_rec = new GesturePoint();
        gp_rec.setdT(0);
        gp_rec.setxG(MPUdata[0]+MPUdata[1]-180);
        gp_rec.setyG(MPUdata[2] + MPUdata[3] - 180);
        gp_rec.setzG(MPUdata[4] + MPUdata[5] - 180);
        gp_rec.setxA((MPUdata[6] - Globals.MIDDLER_CONST) * 100);
        gp_rec.setyA((MPUdata[7] - Globals.MIDDLER_CONST) * 100);
        gp_rec.setzA((MPUdata[8] - Globals.MIDDLER_CONST) * 100);

        gestureHandler.setNewInputData(gp_rec);
    }

    public boolean startServiceDiscovery(BluetoothGatt gatt) {
        return gatt.discoverServices();
    }

    public void closeGatt() {
        new Handler(context.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(gatt != null) {
                    gatt.close();
                }
            }
        });
    }

    public interface OnConnectionEventListener {
        public void ConnectionLost ();
        void onScreenChanged();
        void onButtonPressed(int BUTTON_ID);
    }
}
