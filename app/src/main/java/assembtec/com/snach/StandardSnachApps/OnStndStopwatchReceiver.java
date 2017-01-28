package assembtec.com.snach.StandardSnachApps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 12.06.15.
 */
public class OnStndStopwatchReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("STND_STOPWATCH", "received screen event");
        if(intent.getStringExtra(SnachExtras.APP_SNACH_EVENT).equals(SnachExtras.APP_SCREEN_REQUEST)) {
            Intent service = new Intent(context, StandardAppStopwatch.class);
            context.startService(service);
        } else if(intent.getStringExtra(SnachExtras.APP_SNACH_EVENT).equals(SnachExtras.APP_BUTTON_TOP)){
            // can be used for easily navigating though multiple screens..
        } else if(intent.getStringExtra(SnachExtras.APP_SNACH_EVENT).equals(SnachExtras.APP_BUTTON_RIGHT)){

        } else if(intent.getStringExtra(SnachExtras.APP_SNACH_EVENT).equals(SnachExtras.APP_BUTTON_BOTTOM)){

        } else if(intent.getStringExtra(SnachExtras.APP_SNACH_EVENT).equals(SnachExtras.APP_BUTTON_LEFT)){

        }
    }
}
