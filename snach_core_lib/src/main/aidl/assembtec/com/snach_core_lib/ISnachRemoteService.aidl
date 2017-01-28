// ISnachRemoteService.aidl
package assembtec.com.snach_core_lib;
import assembtec.com.snach_core_lib.ActionAppContentItem;
import assembtec.com.snach_core_lib.ListAppContentItem;
import assembtec.com.snach_core_lib.PopUpContentItem;
import assembtec.com.snach_core_lib.ISnachRemoteServiceCallback;
import assembtec.com.snach_core_lib.SnachNotification;

// Declare any non-default types here with import statements

interface ISnachRemoteService {

    void setUpActionAppContent(in ActionAppContentItem appContentItem);
    void setUpListAppContent(in ListAppContentItem appContentItem);
    void setUpPopUpContent(in PopUpContentItem popupContentItem);
    void pushNotification(in SnachNotification snachNotification);
    void removeNotification(in String bcAction);
    void registerCallback(in ISnachRemoteServiceCallback mCallback);
    void unregisterCallback(in ISnachRemoteServiceCallback mCallback);

}
