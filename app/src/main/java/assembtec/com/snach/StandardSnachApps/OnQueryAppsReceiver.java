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
