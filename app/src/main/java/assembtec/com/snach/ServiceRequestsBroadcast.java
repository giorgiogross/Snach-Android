package assembtec.com.snach;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 16.04.2015.
 */
public class ServiceRequestsBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(action.equals(SnachExtras.INTENT_ACTION_SERVICE_REQUEST)){
            checkServiceState(context);
        }

    }

    private void checkServiceState(Context context) {
        /**
         * Checks and broadcasts if the SnachStreamService is still running
         */
        Intent statusReply = new Intent(SnachExtras.INTENT_ACTION_SERVICE_REPLY);
        statusReply.putExtra(SnachExtras.INTENT_EXTRA_SERVICE_ALIVE, SnachStreamService.isInstatiated());
        context.sendBroadcast(statusReply);
    }
}
