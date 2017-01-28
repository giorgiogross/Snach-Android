package assembtec.com.snach;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import assembtec.com.snach_core_lib.GesturePoint;
import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 28.04.2015.
 */
public class GestureSpecificFragment extends Fragment {
    private RelativeLayout rl_base;

    private TextView tv_gestureAction;
    private TextView tv_editGestureAction;
    private TextView tv_repeatRecord;
    private TextView tv_saveEditedGesture;
    private TextView tv_deleteGestue;
    private EditText et_editName;

    private Spinner s_gestureMode;

    private ImageButton ib_back;

    private ListView lv_actions;
    private ArrayList<GestureActionItem> actionsList;
    private ActionsListAdapter actionsAdapter;

    private EditedListener editedListener;

    private int selectedID = -1;

    private boolean isStandard = false;

    private String gestureMode = SnachExtras.GESTURE_MODE_ALWAYS;
    private String gestureApp = SnachExtras.GESTURE_GLOBAL;
    private String gestureName = "Gesture";
    private String gestureAction = "Action";
    private String gestureActionTitle = "Action";
    private SharedPreferences sharedGesturesSpecs;
    private SharedPreferences sharedGesture;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rl_base = (RelativeLayout) inflater.inflate(R.layout.fragment_specific_gesture, container, false);
        initSharedPreferences();

        initGestureSpecs();

        // Views
        tv_gestureAction = (TextView) rl_base.findViewById(R.id.tv_gestureAction);
        tv_editGestureAction = (TextView) rl_base.findViewById(R.id.tv_editGestureAction);
        tv_repeatRecord = (TextView) rl_base.findViewById(R.id.tv_repeatRecord);
        tv_saveEditedGesture = (TextView) rl_base.findViewById(R.id.tv_saveGestureEdits);
        tv_deleteGestue = (TextView) rl_base.findViewById(R.id.tv_deleteGesture);
        et_editName = (EditText) rl_base.findViewById(R.id.et_editName);

        lv_actions = (ListView) rl_base.findViewById(R.id.lv_gestureAction);
        ib_back = (ImageButton) rl_base.findViewById(R.id.ib_back);
        ib_back.setOnClickListener(ClickListener);

        et_editName.setText(sharedGesture.getString(Globals.KEY_GESTURE_NAME, getResources().getString(R.string.name_gesture)));
        tv_editGestureAction.setOnClickListener(ClickListener);
        tv_repeatRecord.setOnClickListener(ClickListener);
        tv_saveEditedGesture.setOnClickListener(ClickListener);
        tv_deleteGestue.setOnClickListener(ClickListener);

        // Spinner:
        s_gestureMode = (Spinner) rl_base.findViewById(R.id.s_gestureMode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.gesture_modes, android.R.layout.simple_spinner_item);
        s_gestureMode.setAdapter(adapter);
        if(gestureMode.equals(SnachExtras.GESTURE_MODE_ALWAYS)){
            s_gestureMode.setSelection(0);
        } else if(gestureMode.equals(SnachExtras.GESTURE_MODE_SCREEN_ON)){
            s_gestureMode.setSelection(1);
        }
        s_gestureMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        gestureMode = SnachExtras.GESTURE_MODE_ALWAYS;
                        break;
                    case 1:
                        gestureMode = SnachExtras.GESTURE_MODE_SCREEN_ON;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // ListView:
        actionsAdapter = new ActionsListAdapter(lv_actions, getActivity());

        gestureActionTitle = actionsAdapter.getActionName(gestureAction);
        tv_gestureAction.setText(gestureActionTitle);

        lv_actions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gestureAction = actionsAdapter.getAction(position);
                gestureActionTitle = actionsAdapter.getActionName(position);
                tv_gestureAction.setText(gestureActionTitle);
                hideActionsList();
            }
        });

        // If Standardgesture selected:
        if(isStandard){
            tv_editGestureAction.setVisibility(View.GONE);
            et_editName.setFocusable(false);
            et_editName.setEnabled(false);
            et_editName.setActivated(false);
        }

        return rl_base;
    }

    private void initGestureSpecs() {
        gestureMode = sharedGesture.getString(Globals.KEY_GESTURE_MODE, SnachExtras.GESTURE_MODE_SCREEN_ON);
        if(selectedID > Globals.AMOUNT_OF_STANDARDGESTURES) {
            gestureName = sharedGesture.getString(Globals.KEY_GESTURE_NAME, "Action");
            gestureAction = sharedGesture.getString(Globals.KEY_GESTURE_ACTION, "");
            gestureApp = sharedGesture.getString(Globals.KEY_GESTURE_APP, SnachExtras.GESTURE_GLOBAL);
        } else {
            isStandard = true;
            gestureApp = SnachExtras.GESTURE_GLOBAL;

            switch(selectedID){
                case 1:
                    gestureName = getResources().getString(R.string.gesture_standard_swypeleft);
                    gestureAction = SnachExtras.GESTURE_ACTION_SWYPELEFT;
                    break;
                case 2:
                    gestureName = getResources().getString(R.string.gesture_standard_swyperight);
                    gestureAction = SnachExtras.GESTURE_ACTION_SWYPERIGHT;
                    break;
                case 3:
                    gestureName = getResources().getString(R.string.gesture_standard_confirm);
                    gestureAction = SnachExtras.GESTURE_ACTION_CONFIRM;
                    break;
                case 4:
                    gestureName = getResources().getString(R.string.gesture_standard_dismiss);
                    gestureAction = SnachExtras.GESTURE_ACTION_DISMISS;
                    break;

            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        editedListener = (EditedListener) activity;
    }

    View.OnClickListener ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gestureName = et_editName.getText().toString();

            switch (v.getId()){
                case R.id.tv_saveGestureEdits:
                    saveAllChanges();
                    getFragmentManager().popBackStack();
//                    editedListener.returnToOverview();
                    break;
                case R.id.tv_deleteGesture:
                    deleteGesture(selectedID);
                    getFragmentManager().popBackStack();
//                    editedListener.returnToOverview();
                    break;
                case R.id.tv_editGestureAction:
                    showActionsList();
                    break;
                case R.id.tv_repeatRecord:
                    if(isStandard){
                        sharedGesture.edit().putInt(Globals.KEY_GESTURE_ID, selectedID).apply();
                    }
                    startRecorder();
                    break;
                case R.id.ib_back:
                    if(lv_actions.getVisibility() == View.VISIBLE){
                        hideActionsList();
                    } else {
                        getFragmentManager().popBackStack();
//                        editedListener.returnToOverview();
                    }
            }
        }
    };

    private void hideActionsList() {
        ib_back.setImageResource(R.drawable.ic_action_back);
        lv_actions.setVisibility(View.GONE);
    }

    private void startRecorder() {
        /*Intent recordIntent = new Intent();
        recordIntent.setAction(SnachExtras.RECORDING_REQUEST);
        recordIntent.putExtra(SnachExtras.RECORDING_REQUEST_CALLING_APP, getActivity().getPackageName());
        recordIntent.putExtra(SnachExtras.RECORDING_REQUEST_ACTION, gestureAction);
        recordIntent.putExtra(SnachExtras.RECORDING_REQUEST_NAME, gestureName);
        recordIntent.putExtra(SnachExtras.RECORDING_REQUEST_APP, gestureApp);
        recordIntent.putExtra(SnachExtras.RECORDING_REQUEST_MODE, gestureMode);
        getActivity().sendBroadcast(recordIntent);*/

        editedListener.repeatRecording(gestureName, gestureMode, gestureAction, gestureApp);
    }

    private void showActionsList() {
        ib_back.setImageResource(R.drawable.ic_action_cancel);
        lv_actions.setVisibility(View.VISIBLE);
    }

    private void deleteGesture(int selectedID) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(selectedID + Globals.MAJOR_KEY_GLOBALGESTURE, Context.MODE_MULTI_PROCESS).edit();

        editor.remove(Globals.KEY_GESTURE_DURATION);
        editor.remove(Globals.KEY_GESTURE_ENABLED);
        editor.remove(Globals.KEY_GESTURE_ACTION);
        editor.remove(Globals.KEY_GESTURE_APP);
        editor.remove(Globals.KEY_GESTURE_MODE);
        editor.remove(Globals.KEY_GESTURE_NAME);
        editor.remove(Globals.KEY_GESTURE_ID);

        for (int a = 1; a <= Globals.GESTURE_RECORDING_ATTEMPTS + 1; a++) {
            int characteristicsAmount = sharedGesture.getInt(Globals.KEY_GESTURE_CARACTERISTICS_AMOUNT + a, 0);
            for (int i = 1; i <= characteristicsAmount; i++) {
                editor.remove(i + Globals.KEY_GESTURE_XA_DATA + a);
                editor.remove(i + Globals.KEY_GESTURE_YA_DATA + a);
                editor.remove(i + Globals.KEY_GESTURE_ZA_DATA + a);

                editor.remove(i + Globals.KEY_GESTURE_dXA_DATA + a);
                editor.remove(i + Globals.KEY_GESTURE_dYA_DATA + a);
                editor.remove(i + Globals.KEY_GESTURE_dZA_DATA + a);

                editor.remove(i + Globals.KEY_GESTURE_XG_DATA + a);
                editor.remove(i + Globals.KEY_GESTURE_YG_DATA + a);
                editor.remove(i + Globals.KEY_GESTURE_ZG_DATA + a);

                editor.remove(i + Globals.KEY_GESTURE_dXG_DATA + a);
                editor.remove(i + Globals.KEY_GESTURE_dYG_DATA + a);
                editor.remove(i + Globals.KEY_GESTURE_dZG_DATA + a);
                editor.remove(i + Globals.KEY_GESTURE_DELTA_TIME_DATA + a);
            }
        }

        editor.apply();
    }

    private void saveAllChanges() {
        SharedPreferences.Editor editor = sharedGesture.edit();
        editor.putString(Globals.KEY_GESTURE_ACTION, gestureAction);
        editor.putBoolean(Globals.KEY_GESTURE_ENABLED, true);
        editor.putString(Globals.KEY_GESTURE_APP, gestureApp);
        editor.putString(Globals.KEY_GESTURE_MODE, gestureMode);
        editor.putString(Globals.KEY_GESTURE_NAME, gestureName);

        editor.apply();
    }

    private void initSharedPreferences() {
        sharedGesturesSpecs = getActivity().getSharedPreferences(Globals.MAJOR_KEY_GLOBALGESTURES_SPECS, Context.MODE_MULTI_PROCESS );
        sharedGesture = getActivity().getSharedPreferences(selectedID + Globals.MAJOR_KEY_GLOBALGESTURE, Context.MODE_MULTI_PROCESS);
    }

    public void setGestureID(int id) {
        selectedID = id;
    }

    public interface EditedListener {
        public void repeatRecording(String name, String mode, String action, String app);
    }
}
