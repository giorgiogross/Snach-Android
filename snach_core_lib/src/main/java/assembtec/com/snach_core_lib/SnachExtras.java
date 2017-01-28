package assembtec.com.snach_core_lib;

/**
 * Created by Giorgio on 15.04.2015.
 */
public class SnachExtras {

    public final static String INTENT_ACTION_CONNECTION = "assembtec.com.snach_core_lib.ACTION_CONNECTION";
    public final static String INTENT_ACTION_SERVICE_REQUEST = "assembtec.com.snach.ACTION_SERVICE_REQUEST";
    public final static String INTENT_ACTION_SERVICE_REPLY = "assembtec.com.snach.ACTION_SERVICE_REPLY";
    public final static String INTENT_ACTION_GESTURE_RECORDING = "assembtec.com.snach.ACTION_GESTURE_RECORDING";
    public final static String INTENT_ACTION_GESTURE_REGISTRATION = "assembtec.com.snach.ACTION_GESTURE_REGISTRATION";
    public final static String INTENT_ACTION_SUPPORTED_APPS_QUERY = "assembtec.com.snach.ACTION_SUPPORTED_APPS_QUERY";
    public final static String INTENT_ACTION_SUPPORTED_APPS_REPLY = "assembtec.com.snach.ACTION_SUPPORTED_APPS_REPLY";
    public final static String INTENT_ACTION_SNACH_SCREEN_SETUP = "assembtec.com.snach.ACTION_SNACH_SCREEN_SETUP";

    public final static String INTENT_EXTRA_APP_PACKAGE = "assembtec.com.snach.EXTRA_APP_PACKAGE"; // Package to launch app in OnItemClick of lists
    public final static String INTENT_EXTRA_APP_NAME = "assembtec.com.snach.EXTRA_APP_NAME"; // App Name to display on Snach and in lists
    public final static String INTENT_EXTRA_APP_DESCRIPTION = "assembtec.com.snach.EXTRA_APP_DESCRIPTION"; // App Action Description displayed in lists
    public final static String INTENT_EXTRA_APP_INTENT_EXTRA = "assembtec.com.snach.EXTRA_APP_INTENT_EXTRA"; // The intent extra passed if the app is selected from "supported apps"
    public final static String INTENT_EXTRA_APP_BC_ACTION = "assembtec.com.snach.EXTRA_APP_BC_ACTION"; // BroadcastAction when SnachScreen is selected
    public final static String INTENT_EXTRA_APP_BC_EXTRA = "assembtec.com.snach.EXTRA_APP_BC_EXTRA"; // BroadcastExtra when SnachScreen is selected

    public final static String INTENT_EXTRA_CONNECTED = "assembtec.com.snach_core_lib.isConnected";
    public final static String INTENT_EXTRA_SERVICE_ALIVE = "assembtec.com.snach_core_lib.isAlive";


    public final static String INTENT_EXTRA_IS_RECORDING_COMPLETE = "assembtec.com.snach_core_lib.INTENT_EXTRA_IS_RECORDING_COMPLETE";
    public final static String INTENT_EXTRA_RECORDING_ATTEMPT = "assembtec.com.snach_core_lib.numberOfAttempt";
    public final static String INTENT_EXTRA_GESTURE_START_RECORDING = "assembtec.com.snach_core_lib.INTENT_EXTRA_GESTURE_START_RECORDING";
    public final static String INTENT_EXTRA_GESTURE_APP = "assembtec.com.snach_core_lib.INTENT_EXTRA_GESTURE_APP";
    public final static String INTENT_EXTRA_GESTURE_ACTION = "assembtec.com.snach_core_lib.INTENT_EXTRA_GESTURE_ACTION";
    public final static String INTENT_EXTRA_GESTURE_NAME = "assembtec.com.snach_core_lib.INTENT_EXTRA_GESTURE_NAME";
    public final static String GESTURE_GLOBAL = "assembtec.com.snach_core_lib.GESTURE_GLOBAL";
    public final static String RECORDING_REQUEST_CALLING_APP = "assembtec.com.snach_core_lib.RECORDING_REQUEST_CALLING_APP";
    public final static String RECORDING_REQUEST_MODE = "assembtec.com.snach_core_lib.RECORDING_REQUEST_MODE";
    public final static String RECORDING_REQUEST_APP = "assembtec.com.snach_core_lib.RECORDING_REQUEST_APP";
    public final static String RECORDING_REQUEST_ACTION = "assembtec.com.snach_core_lib.RECORDING_REQUEST_ACTION";
    public final static String RECORDING_REQUEST_NAME = "assembtec.com.snach_core_lib.RECORDING_REQUEST_NAME";
    public final static String IS_RECORDING_REQUEST = "assembtec.com.snach_core_lib.IS_RECORDING_REQUEST";

    public final static String RECORDING_REQUEST = "assembtec.com.snach_core_lib.RECORDING_REQUEST";
    public final static String GESTURE_ACTION_EXTERNAL = "assembtec.com.snach_core_lib.GESTURE_ACTION_EXTERNAL";
    public final static String GESTURE_ACTION_SWYPELEFT = "assembtec.com.snach_core_lib.GESTURE_ACTION_SWYPELEFT";
    public final static String GESTURE_ACTION_SWYPERIGHT= "assembtec.com.snach_core_lib.GESTURE_ACTION_SWYPERIGHT";
    public final static String GESTURE_ACTION_DISMISS= "assembtec.com.snach_core_lib.GESTURE_ACTION_DISMISS";
    public final static String GESTURE_ACTION_CONFIRM= "assembtec.com.snach_core_lib.GESTURE_ACTION_CONFIRM";
    public final static String GESTURE_ACTION_HOMESCREEN = "assembtec.com.snach_core_lib.GESTURE_ACTION_HOMESCREEN";
    public final static String GESTURE_ACTION_PLAYMUSIC = "assembtec.com.snach_core_lib.GESTURE_ACTION_PLAYMUSIC";
    public final static String GESTURE_ACTION_NEXTSONG = "assembtec.com.snach_core_lib.GESTURE_ACTION_NEXTSONG";
    public final static String GESTURE_ACTION_PREVIOUSSONG = "assembtec.com.snach_core_lib.GESTURE_ACTION_PREVIOUSSONG";
    public final static String GESTURE_ACTION_OPENAPP = "assembtec.com.snach_core_lib.GESTURE_ACTION_OPENAPP";

    // AppExtras:
    public final static String APP_SPECIAL_EXTRA = "assembtec.com.snach_app_extra";
    public final static String APP_SNACH_EVENT = "assembtec.com.APP_SNACH_EVENT";
    public final static String APP_SCREEN_REQUEST = "assembtec.com.APP_SCREEN_REQUEST";
//    public final static String APP_POPUP_ACTION_PRESSED = "assembtec.com.snach_app_extra";

    /**
     * Attached as an extra if a snach button is pressed.
     * For ListAppScreens TOP and BOTTOM Buttons will not be broadcasted as they
     * are used for scrolling.
     *
     * The Action of the BroadcastIntent is specified by the App itself when it registered
     * for the SnachScreen.
     */
//    public final static String APP_BUTTON_PRESSED = "assembtec.com.SNACH_BUTTON_IS_PRESSED";
    public final static String APP_BUTTON_TOP = "assembtec.com.SNACH_BUTTON_TOP_PRESSED";
    public final static int APP_BUTTON_TOP_ID = 1;
    public final static String APP_BUTTON_LEFT = "assembtec.com.SNACH_BUTTON_LEFT_PRESSED";
    public final static int APP_BUTTON_LEFT_ID = 4;
    public final static String APP_BUTTON_RIGHT = "assembtec.com.SNACH_BUTTON_RIGHT_PRESSED";
    public final static int APP_BUTTON_RIGHT_ID = 2;
    public final static String APP_BUTTON_BOTTOM = "assembtec.com.SNACH_BUTTON_BOTTOM_PRESSED";
    public final static int APP_BUTTON_BOTTOM_ID = 3;
    // The selected item index (for ListAppScreens)
    public final static String APP_LIST_ITEM_SELECTED = "assembtec.com.APP_LIST_ITEM_SELECTED";

    /**
     * Sets the gesture mode:
     * GESTURE_MODE_SCREEN_ON:
     *      Global gestures are only processed if the screen is turned on.
     *      App-specific gestures are only processed if the App is in foreground.
     * GESTURE_MODE_ALWAYS:
     *      Global gestures are always processed.
     *      App-specific gestures are always processed.
     * GESTURE_MODE_SCREEN_OFF:
     *      Global gestures are only processed if the screen is off.
     *      App-specific gestures are only processed if the screen is off.
     * GESTURE_MODE_FOREGROUND_ALWAYS:
     *      For App-specific gestures only.
     *      App-specific gestures are only processed if the app is active,
     *      no matter of the screen state.
     * GESTURE_MODE_FOREGROUND_SCREEN_OFF:
     *      For App-specific gestures only.
     *      App-specific gestures are only processed if the app is active
     *      and the screen is off.
     * GESTURE_MODE_FOREGROUND_SCREEN_ON.
     *      For App-specific gestures only.
     *      App-specific gestures are only processed if the app is active
     *      and the screen is on.
     *
     */
    public final static String INTENT_EXTRA_GESTURE_MODE = "assembtec.com.snach_core_lib.gestureMode";
    public final static String GESTURE_MODE_SCREEN_ON = "assembtec.com.snach_core_lib.gesture_mode_screen_on";
    public final static String GESTURE_MODE_ALWAYS = "assembtec.com.snach_core_lib.gesture_mode_always";
    public final static String GESTURE_MODE_SCREEN_OFF = "assembtec.com.snach_core_lib.gesture_mode_screen_off";
    public final static String GESTURE_MODE_FOREGROUND_ALWAYS = "assembtec.com.snach_core_lib.gesture_mode_app_foreground_always";
    public final static String GESTURE_MODE_FOREGROUND_SCREEN_OFF = "assembtec.com.snach_core_lib.gesture_mode_app_foreground_screen_off";
    public final static String GESTURE_MODE_FOREGROUND_SCREEN_ON = "assembtec.com.snach_core_lib.gesture_mode_app_foreground_screen_on";

    /**
     * Layout values
     * Values which can be used to customize the appearance of certain layout elements
     */
    public final static byte COLOR_BLACK = 0;
    public final static byte COLOR_WHITE = 1;

    // Lists:
    public final static int SCREENMODE_LISTLAYOUT = 1;
    public final static int SCREENMODE_LISTLAYOUT_REMOVEANIM = 7;
    public final static int SCREENMODE_LISTLAYOUT_SCROLLANIM = 8;
    public final static int SCREENMODE_LISTLAYOUT_REMOVE_AND_SCROLLANIM = 9;

    // Action Screens:
    public final static byte SCREENMODE_ACTIONLAYOUT = 2;

    // Cover:
    public static final int THEME_COVER_STANDARD = 0;

    // PopUp:
    public static final int POP_UP_SCREEN = 4;

    // Connection Parameters:
    public static final String SERVICE_PACKAGE = "assembtec.com.snach";
    public static final String SERVICE_NAME = "assembtec.com.snach.SnachStreamService";

    // Button Icons:
    public static final byte BUTTON_ICON_SCROLL_UP = 2;
    public static final byte BUTTON_ICON_SCROLL_DOWN = 3;
    public static final byte BUTTON_ICON_NOTIFICATION = 4;
    public static final byte BUTTON_ICON_SCROLL_LEFT = 5;
    public static final byte BUTTON_ICON_SCROLL_RIGHT = 6;
    public static final byte BUTTON_ICON_PLAY = 7;
    public static final byte BUTTON_ICON_PAUSE = 8;
    public static final byte BUTTON_ICON_STOP = 9;
    public static final byte BUTTON_ICON_NEXT_SONG = 10;
    public static final byte BUTTON_ICON_PREVIOUS_SONG = 11;
    public static final byte BUTTON_ICON_REWIND_LEFT = 12;

    // Font Themes:
    public static final byte ACTION_FONT_THEME_0 = 0;
    public static final byte ACTION_FONT_THEME_1 = 1;
    public static final byte ACTION_FONT_THEME_2 = 2;
    public static final byte ACTION_FONT_THEME_3 = 3;
    public static final byte ACTION_FONT_THEME_4 = 4;

    public static final byte LIST_FONT_THEME_0 = 0;

    // Back Themes:
    public static final byte BACK_THEME_0 = 0;

    // Icon Themes:
    public static final byte ICON_THEME_0 = 0;

    // Main Icons:
    public static final byte MAIN_ICON_PLAY = 0;

}
