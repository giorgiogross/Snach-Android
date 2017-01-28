package assembtec.com.snach.StandardSnachApps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import assembtec.com.snach.R;
import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 11.06.15.
 */
public class OnQueryAppsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        sendAppsQueryReply(context);
    }

    private void sendAppsQueryReply(Context context) {
        Intent mReply = new Intent();
        mReply.setAction(SnachExtras.INTENT_ACTION_SUPPORTED_APPS_REPLY);
        mReply.putExtra(SnachExtras.INTENT_EXTRA_APP_NAME, context.getResources().getString(R.string.stnd_stopwatch_apptitle));
        mReply.putExtra(SnachExtras.INTENT_EXTRA_APP_PACKAGE, context.getPackageName());
        mReply.putExtra(SnachExtras.INTENT_EXTRA_APP_DESCRIPTION, context.getResources().getString(R.string.stnd_stopwatch_description));
        /*
            DEPRECATED, used when the app is selected:
            Usually the app itself should be started and show the user which features will be added to his Snach.
            This is useful to let the user select functions he needs and even to let him customize the layout,
            but its a safety lack.
            This feature can be easily implemented later, so for now if an app has two Snach Apps it has to send
            two of these BroadcastIntents back as a reply.
         */
        mReply.putExtra(SnachExtras.INTENT_EXTRA_APP_INTENT_EXTRA, "myCustomIntentExtra");
        // Intents used on screen reauests and interactions:
        mReply.putExtra(SnachExtras.INTENT_EXTRA_APP_BC_ACTION, "com.assembtec.snach.STND_STOPWATCH");
        mReply.putExtra(SnachExtras.INTENT_EXTRA_APP_BC_EXTRA, "mCustomBroadcastExtra");
        context.sendBroadcast(mReply);
    }
}
