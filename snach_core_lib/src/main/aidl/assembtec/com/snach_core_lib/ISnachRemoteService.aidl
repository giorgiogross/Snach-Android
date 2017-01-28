// ISnachRemoteService.aidl
package assembtec.com.snach_core_lib;
import assembtec.com.snach_core_lib.ActionAppContentItem;
import assembtec.com.snach_core_lib.ListAppContentItem;
import assembtec.com.snach_core_lib.PopUpContentItem;
import assembtec.com.snach_core_lib.ISnachRemoteServiceCallback;
import assembtec.com.snach_core_lib.SnachNotification;
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
