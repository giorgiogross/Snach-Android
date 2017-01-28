package assembtec.com.snach;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;

import java.lang.reflect.Method;

import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 14.04.2015.
 */
public class ServiceManager {
    private SnachStreamService mStreamService;
    private OnServiceConnectionListener mServiceListener;
    private Context context;

    private ServiceConnection mStreamServiceCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public ServiceManager(Context context, Activity activity) {
        this.context = context;
        mServiceListener =(OnServiceConnectionListener) activity;
    }

    public ServiceManager(Context context) {
        /**
         * Empty constructor for Bluetooth Broadcast Receiver and OnBoot Broadcast Receiver
         */
        this.context = context;
    }

    public void startService(String deviceAddress){
        Intent intent = new Intent(context, SnachStreamService.class);
        intent.putExtra(Globals.KEY_DEVICE_ADDRESS, deviceAddress);
        context.startService(intent);
    }

    public void stopService(){
        Intent intent = new Intent(Globals.INTENT_ACTION_STOP_SNACH_STRAM_SERVICE);
        context.sendBroadcast(intent);
    }

    public void sendServiceRunningRequest(){
        /**
         * Checks if the service is currently running.
         * Service might run although there is no device connected.
         * In this case it will stop running soon.
         */
        Intent statusRequest = new Intent(SnachExtras.INTENT_ACTION_SERVICE_REQUEST);
        context.sendBroadcast(statusRequest);
    }

    public void registerReceiver() {
        IntentFilter connectionFilter = new IntentFilter();
        connectionFilter.addAction(SnachExtras.INTENT_ACTION_CONNECTION);
        connectionFilter.addAction(SnachExtras.INTENT_ACTION_SERVICE_REPLY);
        context.registerReceiver(OnConnectedReceiver, connectionFilter);
    }

    public void unregisterReceiver() {
        context.unregisterReceiver(OnConnectedReceiver);
    }

    public interface OnServiceConnectionListener {
        /*
        Tells the activity if the service is running/result of Broadcast...
         */
        public void isServiceRunning(boolean isRunning);
    }

    private BroadcastReceiver OnConnectedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(SnachExtras.INTENT_ACTION_CONNECTION)) {
                /**
                 * Called when the connection status changes.
                 */
                sendServiceRunningRequest();
            }
            if(action.equals(SnachExtras.INTENT_ACTION_SERVICE_REPLY)){
                /**
                 * Always called after "sendServiceRunningRequest()".
                 */
                mServiceListener.isServiceRunning(intent.getBooleanExtra(SnachExtras.INTENT_EXTRA_SERVICE_ALIVE, false));
            }
        }
    };

}
