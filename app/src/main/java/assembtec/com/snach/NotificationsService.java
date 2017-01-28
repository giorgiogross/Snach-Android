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

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * Created by Giorgio on 04.06.15.
 */
public class NotificationsService extends NotificationListenerService {
    static public OnNotificationEvent mNotificationEvent;

    public static void setOnNotificationEvent(Object obj){
        mNotificationEvent = (OnNotificationEvent) obj;
    }

    public NotificationsService(){
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        try {
            mNotificationEvent.OnNotificationPosted(getActiveNotifications());
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        try {
            mNotificationEvent.OnNotificationRemoved(sbn.getId());
        } catch (NullPointerException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
