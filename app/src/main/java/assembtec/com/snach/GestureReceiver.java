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
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 15.04.2015.
 */
public class GestureReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(SnachExtras.GESTURE_ACTION_HOMESCREEN)){
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startMain);
        }
        else if(action.equals(SnachExtras.GESTURE_ACTION_PLAYMUSIC)){
            Intent playPauseMusic = new Intent();
            playPauseMusic.setAction(Intent.ACTION_MEDIA_BUTTON);
            playPauseMusic.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
            context.sendOrderedBroadcast(playPauseMusic, null);

            playPauseMusic = new Intent(Intent.ACTION_MEDIA_BUTTON);
            playPauseMusic.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));
            context.sendOrderedBroadcast(playPauseMusic, null);
        }
        else if(action.equals(SnachExtras.RECORDING_REQUEST)){
            /**
             * Called by 3rd-Party-Apps to start recording a gesture
             * through the Snach settings app.
             *
             * The calling app needs to provide:
             * SnachExtras.RECORDING_REQUEST_CALLING_APP    -> The package name of the app so that the app will be reopened after the recording ends
             * SnachExtras.RECORDING_REQUEST_MODE           -> Gesture Mode
             * SnachExtras.RECORDING_REQUEST_NAME           -> Gesture Name
             * SnachExtras.RECORDING_REQUEST_APP            -> App-Specific extra to distinguish gestures
             * SnachExtras.RECORDING_REQUEST_ACTION         -> The IntentAction which will be attached to an Intent when the gesture is recognized
             *                                                 May be either app specific (combined with package name!) or one of the actions provided
             *                                                 by the SnachExtras-Class.
             */


            /*String callingApp = intent.getStringExtra(SnachExtras.RECORDING_REQUEST_CALLING_APP);
            String gestureMode = intent.getStringExtra(SnachExtras.RECORDING_REQUEST_MODE);
            String gestureName = intent.getStringExtra(SnachExtras.RECORDING_REQUEST_NAME);
            String appExtra = intent.getStringExtra(SnachExtras.RECORDING_REQUEST_APP);*/

            Bundle modifiedExtras = intent.getExtras();
            modifiedExtras.putBoolean(SnachExtras.IS_RECORDING_REQUEST, true);

            Intent starterIntent = new Intent(context, GesturesActivity.class);
            starterIntent.putExtras(modifiedExtras);
            context.startActivity(starterIntent);
        }
    }
}
