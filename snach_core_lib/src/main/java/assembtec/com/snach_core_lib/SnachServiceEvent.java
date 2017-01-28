package assembtec.com.snach_core_lib;

import android.content.ServiceConnection;

/**
 * Created by Giorgio on 18.05.15.
 */
public interface SnachServiceEvent {
    void onServiceDisconnected();
    void onSensorDataReceived(int xAccel, int yAccel, int zAccel, int xGyro, int yGyro, int zGyro);
    void onSnachConnectionResult(boolean isConnected);
    void onSnachButtonPressed(int button_id);
}
