package assembtec.com.snach_core_lib;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by Giorgio on 18.05.15.
 */
public class SnachRemoteHandler {
    private SnachServiceEvent snachServiceEvent;
    private Context context;

    private boolean isBound = false;
    private boolean deliverSensorData = false;
    private ISnachRemoteService mRemoteService;

    private ServiceConnection mRemoteServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteService = ISnachRemoteService.Stub.asInterface(service);

            Log.i("REMOTE_HANDLER", "connected service!");
            snachServiceEvent.onSnachConnectionResult(true);
            try {
                mRemoteService.registerCallback(mCallback);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("REMOTE_HANDLER", "DISconnected service!");
            mRemoteService = null;
            stop();
            isBound = false;
        }
    };

    public SnachRemoteHandler (Context context, Object caller) {
        // Calling class needs to implement SnachServiceEvent interface
        this.context = context;
        snachServiceEvent = (SnachServiceEvent) caller;

    }

    public void begin(){
        /**
         * Binds to the Snach service. Should be called before calling any other method of this class.
         */
        if(!isBound) {
            Intent intent = new Intent();
            intent.setClassName(SnachExtras.SERVICE_PACKAGE, SnachExtras.SERVICE_NAME);

            Log.i("BINDER", "intent: " + (intent) + "   remoteConnection: " + mRemoteServiceConnection + "   context: " + context);
            boolean b = context.bindService(intent, mRemoteServiceConnection, Context.BIND_AUTO_CREATE);
            Log.i("BINDER", "bounded successfully: " + b);
            isBound = true;
        }
    }

    public void stop(){
        /**
         * Unbinds from the service. Should be called when the client app does not need this class anymore.
         */

        Log.i("REMOTE_HANDLER", "stopping client");
        if(isBound) {
            if (mRemoteService != null) {
                try {
                    mRemoteService.unregisterCallback(mCallback);
                } catch (DeadObjectException de){
                    de.printStackTrace();
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }
            }

            context.unbindService(mRemoteServiceConnection);
            isBound = false;
        }
        snachServiceEvent.onServiceDisconnected();
    }

    public void registerForSensorData(){
        deliverSensorData = true;
    }

    public void unregisterFromSensorDate(){
        deliverSensorData = false;
    }

    public void sendAppContent(ListAppContentItem appContentItem){
        try {
            mRemoteService.setUpListAppContent(appContentItem);
        } catch (DeadObjectException de){
            de.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendAppContent(ActionAppContentItem appContentItem){
        try {
            mRemoteService.setUpActionAppContent(appContentItem);
        } catch (DeadObjectException de){
            de.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendPopUpContent(PopUpContentItem popUpContentItem){
        try {
            mRemoteService.setUpPopUpContent(popUpContentItem);
        } catch (DeadObjectException de){
            de.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    // todo add method and aidl files to send notifications and one to push screens

    private ISnachRemoteServiceCallback mCallback = new ISnachRemoteServiceCallback.Stub(){

        @Override
        public void OnSensorDataReceived(int xA, int yA, int zA, int xG, int yG, int zG) throws RemoteException {
            snachServiceEvent.onSensorDataReceived(xA,yA,zA,xG,yG,zG);
        }

        @Override
        public void StopClient(){
            stop();
        }

        @Override
        public void OnButtonPressed(int BUTTON_ID) throws RemoteException {
            snachServiceEvent.onSnachButtonPressed(BUTTON_ID);
        }
    };
}
