package assembtec.com.snach;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 18.04.2015.
 */
public class GesturesActivity extends ActionBarActivity implements ServiceManager.OnServiceConnectionListener, GestureAdderFragment.AdderListener,GestureOverviewFragment.ItemSelectedListener, GestureSpecificFragment.EditedListener{

//    private TextView tv_gesturesTitle;

    // Gestures
    private ArrayList<GestureItem> gestureData;

    // Fragments
    private GestureOverviewFragment gestureFragment;
    private GestureAdderFragment adderFragment;
    private GestureSpecificFragment specsFragment;
    private android.support.v4.app.FragmentTransaction fragmentTransaction;
    private android.support.v4.app.FragmentManager fragmentManager;

    private String appCallingForRecord = "";
    private boolean isRecordingRequest = false;

    // Device Profiles
    private ServiceManager mServiceManager;
    ArrayList<DeviceProfileItem> profileItems;

    // Drawer:
    private DrawerHandler mdHandler;
    private Toolbar toolbar;
    private FrameLayout fl_content;

    private BroadcastReceiver OnGestureRecConpleted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!intent.getBooleanExtra(SnachExtras.INTENT_EXTRA_GESTURE_START_RECORDING, false)) {
                int attempts = intent.getIntExtra(SnachExtras.INTENT_EXTRA_RECORDING_ATTEMPT, -1);
                Log.i("RECOGNIZER", "receiving..." + attempts);
                if (attempts <= Globals.GESTURE_RECORDING_ATTEMPTS && attempts >= 0) {
                    showNextGesturePart(intent.getIntExtra(SnachExtras.INTENT_EXTRA_RECORDING_ATTEMPT, 1) + 1);
                } else if (intent.getBooleanExtra(SnachExtras.INTENT_EXTRA_IS_RECORDING_COMPLETE, false)) {
                    Log.i("RECOGNIZER", "receiving...");
                    /*setGesturesFragment();
                    updateGestureFragment();*/
                    initFragments();
                } else {
                    Log.i("RECOGNIZER", "errorrrrr...");
                    // TODO Show error
                }
            }
        }
    };

    /*private void updateGestureFragment() {
        if(gestureFragment != null){
            gestureFragment.updateList();
        }
    }*/

    private void showNextGesturePart(int i) {
        if(adderFragment != null){
            adderFragment.setGestureAttempt(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        RelativeLayout rl_content = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_gesture, fl_content);
        fl_content = (FrameLayout) findViewById(R.id.content_frame);
        fl_content.addView(rl_content);

        toolbar = (Toolbar) findViewById(R.id.tb_gesture);
        mdHandler = new DrawerHandler(this, toolbar, false, true, false, false);


//        tv_gesturesTitle = (TextView) findViewById(R.id.tv_gesturesTitle);

        mServiceManager = new ServiceManager(getApplicationContext(), this);
        initFragments();

        Intent intent = getIntent();
        this.isRecordingRequest = intent.getBooleanExtra(SnachExtras.IS_RECORDING_REQUEST, false);
        if(isRecordingRequest){
            setAdderFragment();

            String callingApp = intent.getStringExtra(SnachExtras.RECORDING_REQUEST_CALLING_APP);
            String gestureMode = intent.getStringExtra(SnachExtras.RECORDING_REQUEST_MODE);
            String gestureName = intent.getStringExtra(SnachExtras.RECORDING_REQUEST_NAME);
            String appExtra = intent.getStringExtra(SnachExtras.RECORDING_REQUEST_APP);
            String gestureAction = intent.getStringExtra(SnachExtras.RECORDING_REQUEST_ACTION);

            prepareRecordingRequest(gestureAction, appExtra, gestureMode, gestureName);
            this.appCallingForRecord = callingApp;
            // TODO reopen app after recording ends
        }
    }

    private void prepareRecordingRequest(String gestureAction, String appExtra, String gestureMode, String gestureName) {
        adderFragment.prepareExternalRecording(gestureAction, appExtra, gestureMode, gestureName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(OnGestureRecConpleted, new IntentFilter(SnachExtras.INTENT_ACTION_GESTURE_RECORDING));
    }

    private void initFragments() {
        gestureFragment = new GestureOverviewFragment();
        adderFragment = new GestureAdderFragment();
        specsFragment = new GestureSpecificFragment();

        fragmentManager = getSupportFragmentManager();

        setGesturesFragment();
    }

    private void setGesturesFragment() {
        // Create new fragment and transaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.rl_gesture_content, gestureFragment);

        // Commit the transaction
        transaction.commit();

//        tv_gesturesTitle.setText(getResources().getString(R.string.gesture_title));
    }

    private void setAdderFragment() {
        // Create new fragment and transaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.rl_gesture_content, adderFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

//        tv_gesturesTitle.setText(getResources().getString(R.string.add_gesture_title));
    }

    private void setSpecFragment() {
        // Create new fragment and transaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.rl_gesture_content, specsFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

//        tv_gesturesTitle.setText(getResources().getString(R.string.edit_gesture_title));
    }

    @Override
    public void isServiceRunning(boolean isRunning) {

    }

    @Override
    public void OnGestureItemSelected(int ID) {
        specsFragment.setGestureID(ID);
        setSpecFragment();
    }

    @Override
    public void OnAddNewGesture() {
        addNewGesture();
    }

    @Override
    public void repeatRecording(String name, String mode, String action, String app) {
        setAdderFragment();
        prepareRecordingRequest(action, app, mode, name);
    }

    private void addNewGesture() {
        setAdderFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(OnGestureRecConpleted);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

}
