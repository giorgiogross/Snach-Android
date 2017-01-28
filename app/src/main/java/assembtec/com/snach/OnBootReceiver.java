package assembtec.com.snach;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Giorgio on 14.04.2015.
 */
public class OnBootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        new ServiceManager(context).startService(new DevicesHandler(context).getCurrentDeviceAddress());
    }
}
