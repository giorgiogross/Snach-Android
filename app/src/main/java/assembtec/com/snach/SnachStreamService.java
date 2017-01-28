/**
 * Copyright 2015 Giorgio Gross
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 package assembtec.com.snach;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import assembtec.com.snach_core_lib.ActionAppContentItem;
import assembtec.com.snach_core_lib.ISnachRemoteService;
import assembtec.com.snach_core_lib.ISnachRemoteServiceCallback;
import assembtec.com.snach_core_lib.ListAppContentItem;
import assembtec.com.snach_core_lib.PopUpContentItem;
import assembtec.com.snach_core_lib.SnachExtras;
import assembtec.com.snach_core_lib.SnachNotification;

/**
 * Created by Giorgio on 11.04.2015.
 */
public class SnachStreamService extends Service implements BLEManager.OnConnectionEventListener {
    public static SnachStreamService instance = null;
    public static boolean isInstatiated(){
        return instance != null;
    }

    private BroadcastReceiver ServiceBoradcaster = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(SnachExtras.INTENT_ACTION_GESTURE_RECORDING)){
                /**
                 * Records a new gesture and saves it if not canceled.
                 */
                String app = intent.getStringExtra(SnachExtras.INTENT_EXTRA_GESTURE_APP);
                String mode = intent.getStringExtra(SnachExtras.INTENT_EXTRA_GESTURE_MODE);
                String name = intent.getStringExtra(SnachExtras.INTENT_EXTRA_GESTURE_NAME);
                String gestureAction = intent.getStringExtra(SnachExtras.INTENT_EXTRA_GESTURE_ACTION);
                if(app != null && mode != null && !app.equals("") && !mode.equals("")) {
                    startGestureRecording(app, mode, name, gestureAction);
                    Log.i("GESTURE_RECORD", "intent received.. " + intent.getStringExtra(SnachExtras.INTENT_EXTRA_GESTURE_APP) + "   " + intent.getStringExtra(SnachExtras.INTENT_EXTRA_GESTURE_MODE));
                }
            }
            if(action.equals(SnachExtras.INTENT_ACTION_GESTURE_REGISTRATION)){
                // TODO add FLAGS, KEYS and corresponding actions...
                /**
                 * Apps can register for common gestures like "swipe right" or "scroll down" etc.
                 * (Note: Users need to have set them up before. To set them up, SnachApp will use
                 * above BroadcastAction...)
                 */
            }
            if(action.equals(Globals.INTENT_ACTION_STOP_SNACH_STRAM_SERVICE)){
                unbindAllClients();
                stopMainLoop();
                stopSelf();
            }
        }
    };

    private void startGestureRecording(String extraApp, String extraMode, String gestureName, String gestureAction) {
        if(bleManager != null){
            bleManager.startGestureRecording(extraApp, extraMode, gestureName, gestureAction);
        } else {
            makeError(getResources().getString(R.string.error_looper));
        }
    }

    // Thread:
    private boolean isLooping = false;

    // Bluetooth
//    private DevicesHandler devicesHandler;
    private String deviceAddress = null;

    private BLEManager bleManager;

    // Notification:
    private NotificationCompat.Builder starterNotificationBuilder;
    private NotificationManager mNotifyMgr;

    private ISnachRemoteServiceCallback serviceCallback = null;
    private final ISnachRemoteService.Stub mBinder = new ISnachRemoteService.Stub(){
        @Override
        public void setUpActionAppContent(ActionAppContentItem appContentItem) throws RemoteException {
            synchronized (BLEManager.class) {
                bleManager.setActionAppData(appContentItem);
            }
        }

        @Override
        public void setUpListAppContent(ListAppContentItem appContentItem) throws RemoteException {
            synchronized (BLEManager.class) {
                bleManager.setListAppData(appContentItem);
            }
        }

        @Override
        public void setUpPopUpContent(PopUpContentItem popupContentItem) throws RemoteException {
            synchronized (BLEManager.class) {
                bleManager.setPopUpScreen(popupContentItem);
            }
        }

        @Override
        public void pushNotification(SnachNotification snachNotification) throws RemoteException {

        }

        @Override
        public void removeNotification(String bcAction) throws RemoteException {

        }
        // Add methods to just update content of list and action screens
        // Add timeline screen

        @Override
        public void registerCallback(ISnachRemoteServiceCallback mCallback) throws RemoteException {
            serviceCallback = mCallback;
        }

        @Override
        public void unregisterCallback(ISnachRemoteServiceCallback mCallback) throws RemoteException {
            serviceCallback = null;
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        devicesHandler = new DevicesHandler(getApplicationContext());
        deviceAddress = intent.getStringExtra(Globals.KEY_DEVICE_ADDRESS);

        goToForeground();
        if(bleManager == null){
            startMainLoop();
        }
        return START_STICKY;
    }

    private void goToForeground() {
        Intent resultIntent = new Intent(this, BluetoothActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        starterNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.service_started));
        starterNotificationBuilder.setAutoCancel(true);
        starterNotificationBuilder.setContentIntent(resultPendingIntent);

        Notification starterNote = starterNotificationBuilder.build();
        startForeground(Globals.SERVICE_STARTED_NOTIFICATION_ID, starterNote);
    }

    private void startMainLoop() {
        /**
         * Checks if there is a device currently specified to connect to.
         * If so, the BLEManager will connect, otherwise a notification is
         * created and the service is killed.
         */

        bleManager = new BLEManager(deviceAddress, getApplicationContext(), this);
        if(deviceAddress != null) {
            bleManager.startBLEConnection();
        } else {
            // TODO make notification that user has to set up a device and end service
            makeError("No device specified: "+ deviceAddress);
            bleManager.shutDown();
            bleManager = null;

            stopForeground(true);
            mNotifyMgr.cancel(Globals.SERVICE_STARTED_NOTIFICATION_ID);

            stopSelf();
        }
    }

    private void makeError(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }

    private void stopMainLoop() {
        /**
         * Shuts down the Looper.
         */
        if(bleManager !=null){
            bleManager.shutDown();
            bleManager = null;
        } else {
            bleManager = new BLEManager(null, getApplicationContext(), this);
            bleManager.shutDown();
            bleManager = null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        IntentFilter filter = new IntentFilter();
        filter.addAction(SnachExtras.INTENT_ACTION_GESTURE_RECORDING);
        filter.addAction(SnachExtras.INTENT_ACTION_GESTURE_REGISTRATION);
        filter.addAction((Globals.INTENT_ACTION_STOP_SNACH_STRAM_SERVICE));
        registerReceiver(ServiceBoradcaster, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        Log.i("SERVICE", "BIND SERVICE IS CALLED");
        return super.bindService(service, conn, flags);
    }

    @Override
    public boolean stopService(Intent name) {
        Log.i("SERVICE", "Service stopped by system");
        stopMainLoop();
        return super.stopService(name);
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
    }

    @Override
    public void onDestroy() {
        instance = null;
        Log.i("SERVICE", "Service destroyed");
        unregisterReceiver(ServiceBoradcaster);

        stopMainLoop();
        super.onDestroy();
    }

    @Override
    public void ConnectionLost() {
        stopMainLoop();
        unbindAllClients();
        stopSelf();
    }

    @Override
    public void onScreenChanged() {
        unbindAllClients();
    }

    @Override
    public void onButtonPressed(int BUTTON_ID) {
        try {
            serviceCallback.OnButtonPressed(BUTTON_ID);
        } catch (DeadObjectException de) {
            de.printStackTrace();
        }catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unbindAllClients(){
        if(serviceCallback != null){
            try {
                serviceCallback.StopClient();
            } catch (DeadObjectException de){
                de.printStackTrace();
            } catch (RemoteException re){
                re.printStackTrace();
            }
        }
    }
}
