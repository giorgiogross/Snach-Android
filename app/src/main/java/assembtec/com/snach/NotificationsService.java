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
