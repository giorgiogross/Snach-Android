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
