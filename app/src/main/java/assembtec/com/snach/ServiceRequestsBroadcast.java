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
