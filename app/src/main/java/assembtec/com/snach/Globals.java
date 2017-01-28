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

import java.util.UUID;

/**
 * Created by Giorgio on 11.04.2015.
 */
public class Globals {
    // BLE Services
    public static final UUID SNACH_SYSTEM_SERVICE_UUID = UUID.fromString("3f540000-1ee0-4245-a7ef-35885ccae141");
    // BLE Characteristics
    public static final UUID SNACH_SYSTEM_UART_TX_UUID = UUID.fromString("3f540003-1ee0-4245-a7ef-35885ccae141");
    public static final UUID SNACH_SYSTEM_UART_RX_UUID = UUID.fromString("3f540002-1ee0-4245-a7ef-35885ccae141");
    // BLE Descriptors
    public static final UUID SNACH_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    // Snach Buffer
    public static final int SNACH_MAX_BUFFER_SIZE = 100;
    public static final int SNACH_MAX_BYTE_TRANSFER = 20;
    public static final int SNACH_MAX_LISTBYTE_TRANSFER = 40;
    public static final int SNACH_MAX_POPUPBYTE_TRANSFER = 40;
    public static final int SNACH_MAX_ACTIONBYTE_TRANSFER = 40;
    public static final int SNACH_MAX_LISTTITLE_LENGTH = 16;
    /**
     * Allow maximum 10 pending buffer items:
     * To prevent spamming the Snach system maximum 10 pending buffer items
     * can be sent to the Snach. If an app calls an ICP method and the BLEManager
     * fails to add the BufferItem due to this restriction, an error will be reported
     * through the IPC callback.
     */
    public static final int MAX_PENDING_BUFFERITEMS = 10;

    // Separator bytes
    public static final int SNACH_CONTENT_ID_BYTE = 253;
    public static final int BYTE_ID_ATTACH_CONTENT_TO_ITEM = 252;
    public static final int SNACH_ITEM_SAPARATOR_BYTE = 0;
    /*public static final int SNACH_APP_TITLE_START_BYTE = 1;
    public static final int SNACH_APP_TITLE_END_BYTE = 4;*/
    public static final int SNACH_LISTITEM_TITLE_START_BYTE = 17;
    public static final int SNACH_ACTION_TITLE_START_BYTE = 17;
    public static final int SNACH_LISTITEM_TITLE_START_BYTE_FIRSTITEM = 1;
    public static final int SNACH_LISTITEM_TITLE_END_BYTE = 18;
    public static final int SNACH_ACTION_TITLE_END_BYTE = 18;
    public static final int SNACH_LISTITEM_CONTENT_START_BYTE = 2;
    public static final int SNACH_ACTION_CONTENT_START_BYTE = 2;
    public static final int SNACH_LISTITEM_CONTENT_END_BYTE = 3;
    public static final int SNACH_ACTION_CONTENT_END_BYTE = 3;
    public static final int SNACH_LISTITEM_CONTENT_END_BYTE_LASTITEM = 4;
    public static final int SNACH_LISTITEM_END_BYTE = 3;
    public static final int SNACH_EOF_BYTE = 254;
    public static final int BYTE_ID_SCROLL_UP = 0;
    public static final int BYTE_ID_SCROLL_DOWN = 1;
    public static final int BYTE_ID_LISTTITLE_TOP = 14;
    public static final int BYTE_ID_LISTTITLE_BOTTOM = 15;
    public static final int BYTE_ID_UPDATE_LISTTITLES = 251;

    // Screen-Identification bytes
    public static final int SNACH_WATCHFACE = 0;
    public static final int WATCHFACE_CLASSIC = 1;
    public static final int WATCHFACE_CUSTOM = 3;
    public static final int SNACH_DISMISS_POPUP = 5;
    public static final int SNACH_COVER_LAYOUT = 3;
    public static final int BYTE_ID_NOTIFICATION_INFO = 6;

    // Gesture Intent Actions
//    protected final static String ACTION_GESTURE_HOMESCREEN = "assembtec.com.snach.ACTION_HOMESCREEN";
    protected final static int MINIMUM_GESTURE_CHARACTERISTICS = 1;
    protected final static int AMOUNT_OF_STANDARDGESTURES = 4;

    // Gesture Processing Values:
    protected final static int MIDDLER_CONST = 128;
    protected final static int VARIANCE_TIME = 25;
    protected final static int VARIANCE_ACCELERATION = 1000;
    protected final static int MIN_DELTA_ACCELERATION = 6300;
    protected final static int VARIANCE_GYRO = 25;
    protected final static int MIN_DELTA_GYRO = 70;
    protected final static int MAX_GESTURE_POINTS = 5; // => +1 wegen '0'
    protected final static int BT_DATA_DELAY = 100;
    protected final static int GESTURE_RECORDING_ATTEMPTS = 4; // => +1 wegen '0'
    protected final static long ONE_SECOND = 1000;
    protected final static long MAX_REC_TIME_COUNT = 10;

    // Gesture Layout Values:
    protected final static int SUBHEADER_ITEM = 0;
    protected final static int GESTURE_ITEM = 1;

    // ID's:
    protected final static int FOREGROUND_SERVICE_ID = 85676515;
    protected final static int SERVICE_STARTED_NOTIFICATION_ID = 3459;

    // Defaults:
    protected final static String DEFAULT_BROADCAST_ACTION_SCREEN = "ACTION_SNACH_NEW_SCREEN";
    protected final static String DEFAULT_BROADCAST_EXTRA_SCREEN = "EXRTA_SNACH_NEW_SCREEN";
    protected final static String DEFAULT_APP_PACKAGE = "com.example.app";
    protected final static String DEFAULT_APP_NAME = "App";
    protected final static String DEFAULT_DEVICE_NAME = "Snach Device";
    protected final static String DEFAULT_DEVICE_ADDRESS = "";
    protected final static int DEFAULT_CONNECTION_ATTEMPT_TIMEOUT = 3000;
    protected final static int DEFAULT_CONNECTED_TIMEOUT = 10000;

    protected final static int DEFAULT_SNACH_SCREENS = 2;
    protected final static int DEFAULT_APPS_AMOUNT = 0;

    // Apps:
    protected final static int QUERY_SUPPRTED_APPS_TIME = 12000;
    protected final static String ADD_NEW_APP = "ADD_NEW_APP";

    /**SharedPreferences Keys*/
    //Gestures:
    protected final static String MAJOR_KEY_GLOBALGESTURES_SPECS = "globalGesturesSpecs";

    protected final static String KEY_GESTURES_AMOUNT = ".gesturesAmount";

    // protected final static String MAJOR_KEY_SWYPELEFT = ".standardGesture_swypeLeft"; /** combined with the Gesture_ID: ID+KEY */
    // protected final static String MAJOR_KEY_SWYPERIGHT = ".standardGesture_swypeRight"; /** combined with the Gesture_ID: ID+KEY */
    // protected final static String MAJOR_KEY_DISMISS = ".standardGesture_dismiss"; /** combined with the Gesture_ID: ID+KEY */
    // protected final static String MAJOR_KEY_CONFIRM = ".standardGesture_confirm"; /** combined with the Gesture_ID: ID+KEY */


    protected final static String MAJOR_KEY_GLOBALGESTURE = ".globalGesture"; /** combined with the Gesture_ID: ID+KEY */

    protected final static String KEY_GESTURE_ID = ".gesturesAmount";
    protected final static String KEY_GESTURE_DURATION = ".gestureDuration";
    protected final static String KEY_GESTURE_ENABLED = ".isGestureEnabled";
    protected final static String KEY_GESTURE_APP = ".gestureAppCall";
    protected final static String KEY_GESTURE_MODE = ".gestureMode";
    protected final static String KEY_GESTURE_ACTION = ".gestureAction";
    protected final static String KEY_GESTURE_NAME = ".gestureName";
    protected final static String KEY_GESTURE_CARACTERISTICS_AMOUNT = ".characteristicsAmount";
    protected final static String KEY_GESTURE_CARACTERISTICS_AMOUNT_MIN = ".minCharacteristicsAmount";
    protected final static String KEY_GESTURE_CARACTERISTICS_AMOUNT_MAX = ".maxCharacteristicsAmount";
    protected final static String KEY_GESTURE_XA_DATA = ".xAcceleration"; /** combined with number of characteristics and the attempt: "caract.ID"+KEY+ATTEMPT */
    protected final static String KEY_GESTURE_YA_DATA = ".yAcceleration"; /** combined with number of characteristics: "caract.ID"+KEY */
    protected final static String KEY_GESTURE_ZA_DATA = ".zAcceleration"; /** combined with number of characteristics: "caract.ID"+KEY */
    protected final static String KEY_GESTURE_dXA_DATA = ".d_xAcceleration"; /** combined with number of characteristics: "caract.ID"+KEY */
    protected final static String KEY_GESTURE_dYA_DATA = ".d_yAcceleration"; /** combined with number of characteristics: "caract.ID"+KEY */
    protected final static String KEY_GESTURE_dZA_DATA = ".d_zAcceleration"; /** combined with number of characteristics: "caract.ID"+KEY */
    protected final static String KEY_GESTURE_XG_DATA = ".xGyro"; /** combined with number of characteristics: "caract.ID"+KEY */
    protected final static String KEY_GESTURE_YG_DATA = ".yGyro"; /** combined with number of characteristics: "caract.ID"+KEY */
    protected final static String KEY_GESTURE_ZG_DATA = ".zGyro"; /** combined with number of characteristics: "caract.ID"+KEY */
    protected final static String KEY_GESTURE_dXG_DATA = ".d_xGyro"; /** combined with number of characteristics: "caract.ID"+KEY */
    protected final static String KEY_GESTURE_dYG_DATA = ".d_yGyro"; /** combined with number of characteristics: "caract.ID"+KEY */
    protected final static String KEY_GESTURE_dZG_DATA = ".d_zGyro"; /** combined with number of characteristics: "caract.ID"+KEY */
    protected final static String KEY_GESTURE_DELTA_TIME_DATA = ".dTime"; /** combined with number of characteristics: "caract.ID"+KEY */
    protected final static String KEY_GESTURE_IS_CHARACTERISTICS = ".isCharacteristics"; /** combined with number of characteristics: "caract.ID"+KEY */

    // Device-Profiles:
    protected final static String MAJOR_KEY_DEVICES_SPECS = "devicesSpecifications";

    protected final static String KEY_DEVICES_AMOUNT = ".devicesAmount";
    protected final static String KEY_CURRENT_DEVICE_ID = ".currentSelectedID";
    protected final static String KEY_CURRENT_DEVICE_NAME = ".name";
    protected final static String KEY_CURRENT_DEVICE_ADDRESS = ".adress";


    protected final static String MAJOR_KEY_DEVICE = ".preferences"; /** combined with the ID: ID+KEY */

    protected final static String KEY_APPS_AMOUNT = ".appsAmount";
    protected final static String KEY_MAX_SNACH_SCREENS = ".maxSnachScreens";
    protected final static String KEY_DEVICE_ID = ".ID";
    protected final static String KEY_DEVICE_NAME = ".name";
    protected final static String KEY_DEVICE_ADDRESS = ".address";
    protected final static String KEY_DEVICE_ISPAIRED = ".isPaired";


    protected final static String MAJOR_KEY_DEVICE_APP = ".apps"; /** combined with the IDs: ID_DEVICE+ID_APP+KEY */

    protected final static String KEY_APP_ID = ".appID"; /** combined with the ID: ID+KEY */
    protected final static String KEY_APP_SNACH_SCREEN_INDEX = ".appSnachScreenIndex"; /** combined with the ID: ID+KEY */
    protected final static String KEY_APP_NAME = ".appName"; /** combined with the ID: ID+KEY */
    protected final static String KEY_APP_PACKAGE = ".appPackage"; /** combined with the ID: ID+KEY */
    protected final static String KEY_APP_BROADCAST_ACTION = ".appBCAction"; /** combined with the ID: ID+KEY */
    protected final static String KEY_APP_BROADCAST_EXTRA = ".appBCExtra"; /** combined with the ID: ID+KEY */


    // protected final static String MAJOR_KEY_DEVICE_CLIENTGESTURES = ".clientGestures"; /** combined with the ID: ID+KEY */
    // protected final static String MAJOR_KEY_DEVICE_CLIENTPREF = ".clientPreferences"; /** combined with the ID: ID+KEY */
    // protected final static String MAJOR_KEY_DEVICE_CLIENTDATA = ".clientData"; /** combined with the ID: ID+KEY */
    //protected final static String MAJOR_KEY_DEVICE_CLIENTDESIGN = ".clientDesign"; /** combined with the ID: ID+KEY */


    protected  final static String INTENT_ACTION_STOP_SNACH_STRAM_SERVICE = "assembtec.com.INTENT_ACTION_STOP_SNACH_STRAM_SERVICE";

}
