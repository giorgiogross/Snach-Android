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

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

/**
 * Created by Giorgio on 15.04.2015.
 */
public class OnBluetoothReceiver extends BroadcastReceiver {
    private Context context = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
            if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) == BluetoothAdapter.STATE_ON) {
                /*this.context = context;
                Handler starterHandler =  new Handler();
                starterHandler.postAtTime(starterTask, 1000);*/

                new ServiceManager(context).startService(new DevicesHandler(context).getCurrentDeviceAddress());
            } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR) == BluetoothAdapter.STATE_OFF) {
                new ServiceManager(context).stopService();
            }

        }
    }

    /*private Runnable starterTask = new Runnable() {
        public void run() {
            new ServiceManager(context).startService();
        }
    };*/
}
