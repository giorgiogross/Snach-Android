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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 18.04.2015.
 */
public class GestureAdderFragment extends Fragment {
    private RelativeLayout rl_base;
    private AdderListener adderListener;

    private RelativeLayout rl_record;
    private TextView tv_startRecordingGesture;
    private TextView tv_description;
    private TextView tv_attemptsCounter;

    private ImageButton ib_continueWithRecording;

    private RelativeLayout rl_recording_specs;
    private RelativeLayout rl_gestureActionSpecification;
    private ListView lv_gestureAction;
    private EditText et_gestureName;
    private ImageButton ib_backToActionList;
    private ImageButton ib_cancel;
    private TextView tv_gestureActionDescription;
    private Spinner s_gestureMode;

    private ActionsListAdapter actionsAdapter;

    private String selectedGestureAction = "";
    private String gestureMode = SnachExtras.GESTURE_MODE_ALWAYS;
    private String gestureApp = SnachExtras.GESTURE_GLOBAL;
    private boolean preparedToRecord = false;
    private String preparedGestureName = "";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rl_base = (RelativeLayout) inflater.inflate(R.layout.fragment_add_gesture, container, false);

        rl_record = (RelativeLayout) rl_base.findViewById(R.id.rl_record);
        tv_description = (TextView) rl_base.findViewById(R.id.tv_gestureRecordingDescription);
        tv_attemptsCounter = (TextView) rl_base.findViewById(R.id.tv_attemptsCounter);

        ib_cancel = (ImageButton) rl_base.findViewById(R.id.ib_cancel);
        ib_cancel.setOnClickListener(ButtonClickListener);
        ib_continueWithRecording = (ImageButton) rl_base.findViewById(R.id.ib_continueWithRecording);
        ib_continueWithRecording.setOnClickListener(ButtonClickListener);

        rl_recording_specs = (RelativeLayout) rl_base.findViewById(R.id.rl_recording_specs);
        et_gestureName = (EditText) rl_base.findViewById(R.id.et_gestureName);

        rl_gestureActionSpecification = (RelativeLayout) rl_base.findViewById(R.id.rl_gestureActionSpecification);
        ib_backToActionList = (ImageButton) rl_base.findViewById(R.id.ib_backToActionList);
        ib_backToActionList.setOnClickListener(ButtonClickListener);
        tv_gestureActionDescription = (TextView) rl_base.findViewById(R.id.tv_gestureActionDescription);

        s_gestureMode = (Spinner) rl_base.findViewById(R.id.s_gestureMode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.gesture_modes, android.R.layout.simple_spinner_item);
        s_gestureMode.setAdapter(adapter);
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

        tv_startRecordingGesture = (TextView) rl_base.findViewById(R.id.b_startRecordingGesture);
        tv_startRecordingGesture.setOnClickListener(ButtonClickListener);

        lv_gestureAction = (ListView) rl_base.findViewById(R.id.lv_gestureAction);
        lv_gestureAction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showFABcontinueWithRecording();
                showGestureActionSummary(position);
                selectedGestureAction = actionsAdapter.getAction(position);//actionsList.get(position).getAction();
            }
        });
        actionsAdapter = new ActionsListAdapter(lv_gestureAction, getActivity());

        // for directly recording:
        if(preparedToRecord){
            this.et_gestureName.setText(preparedGestureName);
            showRecordingScreen();
        }

        return rl_base;
    }


    private View.OnClickListener ButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ib_backToActionList:
                    hideGestureActionSummary();
                    break;
                case R.id.ib_continueWithRecording:
                    showRecordingScreen();
                    break;
                case R.id.b_startRecordingGesture:
                    startGestureRecording(gestureApp, gestureMode, et_gestureName.getText().toString());
                    break;
                case R.id.ib_cancel:
                    cancelRecording();
                    preparedToRecord = false;
                    getFragmentManager().popBackStack();
//                    adderListener.OnCancel();
                    break;
            }
        }
    };

    private void cancelRecording() {
        hideRecordingScreen();
    }

    private void showGestureActionSummary(int position){
        lv_gestureAction.setVisibility(View.INVISIBLE);
        rl_gestureActionSpecification.setVisibility(View.VISIBLE);
        tv_gestureActionDescription.setText(getResources().getString(R.string.gesture_action_description)+" "+actionsAdapter.getActionName(position));
    }

    private void hideGestureActionSummary(){
        lv_gestureAction.setVisibility(View.VISIBLE);
        rl_gestureActionSpecification.setVisibility(View.INVISIBLE);
        tv_gestureActionDescription.setText("");
        hideFABcontinueWithRecording();
    }

    private void showRecordingScreen() {
        hideFABcontinueWithRecording();
        rl_record.setVisibility(View.VISIBLE);
        rl_recording_specs.setVisibility(View.INVISIBLE);
    }

    private void hideRecordingScreen() {
        showFABcontinueWithRecording();
        rl_record.setVisibility(View.INVISIBLE);
        rl_recording_specs.setVisibility(View.VISIBLE);
    }

    private void showFABcontinueWithRecording(){
        ib_continueWithRecording.setVisibility(View.VISIBLE);
    }

    private void hideFABcontinueWithRecording(){
        ib_continueWithRecording.setVisibility(View.INVISIBLE);
    }

    public void startGestureRecording(String gestureApp, String gestureMode, String gestureName) {
        /**
         * Starts recording the gesture and delivers all necessary data for gesture identification
         * such as gestureAction, gestureMode and gestureApp (the app to be opened, optionally).
         *
         * Will also be called if a 3rd-Patty-App requests a gesture record.
         * In this case the 3rd-Party-App will provide Action, Mode, Name and App.
         * 'Action' is the IntentAction, 'App' will help the 3rd-Party-App to distinguish
         * its gestures, 'Name' will be shown in the gestures overview and 'Mode' is managed
         * by the service. Though, 'Mode' should be specified as 'ALWAYS', since the app can
         * register a broadcast receiver in its onCreate() and unregister in onDestroy().
         */

        tv_description.setText(getResources().getString(R.string.do_gesture));
        tv_startRecordingGesture.setVisibility(View.GONE);

        Intent recordIntent = new Intent();
        recordIntent.setAction(SnachExtras.INTENT_ACTION_GESTURE_RECORDING);
        recordIntent.putExtra(SnachExtras.INTENT_EXTRA_GESTURE_START_RECORDING, true);
        recordIntent.putExtra(SnachExtras.INTENT_EXTRA_GESTURE_ACTION, selectedGestureAction);
        recordIntent.putExtra(SnachExtras.INTENT_EXTRA_GESTURE_NAME, gestureName);
        recordIntent.putExtra(SnachExtras.INTENT_EXTRA_GESTURE_APP, gestureApp);
        recordIntent.putExtra(SnachExtras.INTENT_EXTRA_GESTURE_MODE, gestureMode);
        getActivity().sendBroadcast(recordIntent);
    }

    private void completeGestureRecording(){
        tv_description.setText(getResources().getString(R.string.set_gesture_startpoint));
        tv_startRecordingGesture.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        adderListener = (AdderListener) activity;
    }

    public void setGestureAttempt(int gestureAttempt) {
        tv_attemptsCounter.setText(""+gestureAttempt);
        completeGestureRecording();
    }

    public void prepareExternalRecording(String gestureAction, String appExtra, String gestureMode, String gestureName) {
        selectedGestureAction=gestureAction;

        try{
            showRecordingScreen();
            this.et_gestureName.setText(gestureName);
        } catch (NullPointerException ne){
            ne.printStackTrace();
            preparedToRecord = true;
            preparedGestureName = gestureName;
        }

        this.gestureApp = appExtra;
        this.gestureMode = gestureMode;

    }



    public interface AdderListener {
    }
}
