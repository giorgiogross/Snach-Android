package assembtec.com.snach;

import android.service.notification.StatusBarNotification;

/**
 * Created by Giorgio on 04.06.15.
 */
public interface OnNotificationEvent {
    public void OnNotificationPosted(StatusBarNotification[] statusBarNotification);
    public void OnNotificationRemoved(int ID);
}
