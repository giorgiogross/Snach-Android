package assembtec.com.snach;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 16.05.15.
 */
public class OnAppReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * Called when an app wants to be added to the Snach screens
         */
        if(SnachExtras.INTENT_ACTION_SNACH_SCREEN_SETUP.equals(intent.getAction())){
            intent.putExtra(Globals.ADD_NEW_APP, true);

            Intent launcherIntent = new Intent(context, AppsActivity.class);
            launcherIntent.putExtras(intent.getExtras());
            context.startActivity(launcherIntent);

        }
    }
}
