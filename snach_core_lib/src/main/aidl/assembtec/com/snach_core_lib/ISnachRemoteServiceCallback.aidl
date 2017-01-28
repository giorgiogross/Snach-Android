// ISnachRemoteServiceCallback.aidl
package assembtec.com.snach_core_lib;

// Declare any non-default types here with import statements

interface ISnachRemoteServiceCallback {
     void OnSensorDataReceived(int xA, int yA, int zA, int xG, int yG, int zG);
     void StopClient();
     void OnButtonPressed(int BUTTON_ID);
}
