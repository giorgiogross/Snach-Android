package assembtec.com.snach;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import assembtec.com.snach_core_lib.GesturePoint;
import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 18.04.2015.
 */
public class GestureProcessingHandler implements DeltaAccelGyroRecognizer.DeltaListener{
    /**
     * Handles all the saved gesture data input.
     */

    // Recording gestures:
    private Context context;
    private boolean isRecording = false;
    private boolean isRecognizing = false;
    private String recordingGestureApp;
    private String recordingGestureMode;
    private String recordingGestureName;
    private String recordingGestureAction;

    private OnGestureAddedListener gestureAdderListener;

    // Processing gestures:
    private boolean isGestureStarted = false;
//    private int recordingTmeCounter = 0;
//    private boolean isRecognizingGesture = false;
    private DeltaAccelGyroRecognizer deltaAccelGyroRecognizer;
    private int gestureDurationCounter = 0;
    private ArrayList<ArrayList<ArrayList<GesturePoint>>> gestureCharacteristicsData;
    private ArrayList<GesturePoint> recordedCharacteristicsData;
    private ArrayList<ArrayList<GesturePoint>> cachedGestures;

    private int gestureAttemptsCounter = 0;

    public GestureProcessingHandler(ArrayList<ArrayList<ArrayList<GesturePoint>>> gestureData, Context context, BLEManager mainLoopThread){
        this.gestureCharacteristicsData = gestureData;
        this.context = context;
        gestureAdderListener = (OnGestureAddedListener) mainLoopThread;
        recordedCharacteristicsData = new ArrayList<GesturePoint>();
        deltaAccelGyroRecognizer = new DeltaAccelGyroRecognizer(this);
    }

    public void setNewInputData(GesturePoint gp_rec) {
        reactOnNextPoint(gp_rec);
    }

    private void reactOnNextPoint(GesturePoint nextPoint) {
        /**
         *
         */

        deltaAccelGyroRecognizer.setNextPoint(nextPoint);
    }

    private void endGestureRecognizer() {
        /**
         * Ends the gesture recognition for this gesture.
         */
        isGestureStarted = false;
//        isRecognizingGesture = false;
        gestureDurationCounter = 0;

        if(isRecording){
            gestureAttemptsCounter++;
            if(gestureAttemptsCounter <= Globals.GESTURE_RECORDING_ATTEMPTS+1){
                cacheGesture(recordedCharacteristicsData);
            } else {
                Log.i("RECOGNIZER", "saving...");
                saveNewGesturesCharacteristics(cachedGestures);
                resetGestureCache();
                isRecording = false;
            }
        } else {
            checkSavedGestures(recordedCharacteristicsData);
//            recordedCharacteristicsData.clear();
        }

//        recordedCharacteristicsData.clear();
//        recordedCharacteristicsData = new ArrayList<>();
    }

    private void resetGestureCache() {
        cachedGestures.clear();
        cachedGestures = null;

        gestureAttemptsCounter = 0;

        recordedCharacteristicsData.clear();
        recordedCharacteristicsData = new ArrayList<GesturePoint>();
        // ....
    }

    private void cacheGesture(ArrayList<GesturePoint> recordedCharacteristicsData) {
        if(cachedGestures == null){
            cachedGestures = new ArrayList<ArrayList<GesturePoint>>();
        }

        Log.i("GESTURE_CHECKER", "recordedCharacteristicsData.size().. "+recordedCharacteristicsData.size());
        if(recordedCharacteristicsData.size() >= Globals.MINIMUM_GESTURE_CHARACTERISTICS) {
            cachedGestures.add(recordedCharacteristicsData);
            gestureAdderListener.OnGestureAttempt(gestureAttemptsCounter);
        } else {
            gestureAttemptsCounter--;
            // TODO do error and notify user -> +repeat attempt!!
            // TODO do also on last attempt...
        }

    }

    private void saveNewGesturesCharacteristics(ArrayList<ArrayList<GesturePoint>> cachedGestures) {
        Log.i("GESTURE_CHECKER", "cachedGestures.size().. "+cachedGestures.size());
        if(cachedGestures.size() == Globals.GESTURE_RECORDING_ATTEMPTS+1) {
            SharedPreferences sharedGesturesSpecs = context.getSharedPreferences(Globals.MAJOR_KEY_GLOBALGESTURES_SPECS, Context.MODE_MULTI_PROCESS );
            int newID = sharedGesturesSpecs.getInt(Globals.KEY_GESTURES_AMOUNT, 0) + 1;

            sharedGesturesSpecs.edit().putInt(Globals.KEY_GESTURES_AMOUNT, newID).apply();

            SharedPreferences sharedGesture = context.getSharedPreferences(newID + Globals.MAJOR_KEY_GLOBALGESTURE, Context.MODE_MULTI_PROCESS );
            SharedPreferences.Editor editor = sharedGesture.edit();

            editor.putInt(Globals.KEY_GESTURE_ID, newID);
            editor.putBoolean(Globals.KEY_GESTURE_ENABLED, true);
            editor.putString(Globals.KEY_GESTURE_APP, recordingGestureApp);
            editor.putString(Globals.KEY_GESTURE_MODE, recordingGestureMode);
            editor.putString(Globals.KEY_GESTURE_NAME, recordingGestureName);
            editor.putString(Globals.KEY_GESTURE_ACTION, recordingGestureAction);

            int minAmount = 5;
            int maxAmount = 2;

            /*for(int d = 0; d < cachedGestures.size(); d++){
                for(int t = 0; t < cachedGestures.get(d).size(); t++){
                    Log.i("proving", "DxA :" + cachedGestures.get(d).get(t).getD_xA()+ " at attempt "+d+" at characterisics "+t);
                }
            }*/


            for(int cgp = 1; cgp <= cachedGestures.size(); cgp++){
                int recGestSize = cachedGestures.get(cgp-1).size();
                Log.i("GESTURE_CHECKER", "cached gesture attempt size: "+recGestSize);
                if(recGestSize >= Globals.MINIMUM_GESTURE_CHARACTERISTICS) {
                    editor.putInt(Globals.KEY_GESTURE_CARACTERISTICS_AMOUNT+cgp, recGestSize);

                    if(recGestSize>maxAmount){
                        maxAmount = recGestSize;
                    }
                    if(recGestSize<minAmount){
                        minAmount = recGestSize;
                    }

                    for (int c = 1; c <= recGestSize; c++) {
                        GesturePoint gp = cachedGestures.get(cgp-1).get(c-1);
                        editor.putInt(c + Globals.KEY_GESTURE_XA_DATA+cgp, gp.getxA());
                        editor.putInt(c + Globals.KEY_GESTURE_YA_DATA+cgp, gp.getyA());
                        editor.putInt(c + Globals.KEY_GESTURE_ZA_DATA+cgp, gp.getzA());
                        editor.putInt(c + Globals.KEY_GESTURE_dXA_DATA+cgp, gp.getD_xA());
                        Log.i("saving", "DxA :" + gp.getD_xA()+ " at attempt "+cgp+" at characterisics "+c);
                        editor.putInt(c + Globals.KEY_GESTURE_dYA_DATA+cgp, gp.getD_yA());
                        editor.putInt(c + Globals.KEY_GESTURE_dZA_DATA+cgp, gp.getD_zA());
                        editor.putInt(c + Globals.KEY_GESTURE_XG_DATA+cgp, gp.getxG());
                        editor.putInt(c + Globals.KEY_GESTURE_YG_DATA+cgp, gp.getyG());
                        editor.putInt(c + Globals.KEY_GESTURE_ZG_DATA+cgp, gp.getzG());
                        editor.putInt(c + Globals.KEY_GESTURE_dXG_DATA+cgp, gp.getD_xG());
                        editor.putInt(c + Globals.KEY_GESTURE_dYG_DATA+cgp, gp.getD_yG());
                        editor.putInt(c + Globals.KEY_GESTURE_dZG_DATA+cgp, gp.getD_zG());

                        editor.putInt(c + Globals.KEY_GESTURE_DELTA_TIME_DATA + cgp, gp.getdT());
                    }

                } else {
                    editor = null;
                    makeGestureRecordingError();
                    return;
                }
            }

            editor.putInt(Globals.KEY_GESTURE_CARACTERISTICS_AMOUNT_MIN, minAmount);
            editor.putInt(Globals.KEY_GESTURE_CARACTERISTICS_AMOUNT_MAX, maxAmount);

            editor.apply();

//            if(gestureAttemptsCounter >= Globals.GESTURE_RECORDING_ATTEMPTS){
                gestureAdderListener.OnGestureAdded();
//            } else {
//                gestureAdderListener.OnGestureAttempt(gestureAttemptsCounter);
//            }
        } else {
            // TODO make error toast etc..
            makeGestureRecordingError();
        }
    }

    private void makeGestureRecordingError() {
        Log.i("GESTURE_CHECKER", "error saving new gesture ");
        Intent recordIntent = new Intent();
        recordIntent.putExtra(SnachExtras.INTENT_EXTRA_RECORDING_ATTEMPT, -3);
        recordIntent.setAction(SnachExtras.INTENT_ACTION_GESTURE_RECORDING);
        recordIntent.putExtra(SnachExtras.INTENT_EXTRA_IS_RECORDING_COMPLETE, false);
        context.sendBroadcast(recordIntent);
    }

    private void checkSavedGestures(ArrayList<GesturePoint> recordedData) {
//        boolean isSame = false;
        Log.i("GESTURE_CHECKER", "recordeddata size: " + recordedData.size());

        for(int start = 0; start <= recordedData.size()-Globals.MINIMUM_GESTURE_CHARACTERISTICS; start++){
            if(isExistingGestureWithStart(recordedData.get(start))){
                ArrayList<ComparisonItem> matchingIndexi = compareToSavedPoints(recordedData.subList(start, recordedData.size() - 1));
                if(matchingIndexi.size() > 1){
                    Log.i("GESTURE_CHECKER", "matching gestures:  "+matchingIndexi.size());
                    // TODO if two gestures possible (e.g. one with 2/2 fitting points and one with 2/3 fitting points) and both have non-equal points-amounts
                    // TODO then listen for 300ms more for input and decide afterwards on base of percentages of correlation..

                    boolean allEqual = false;
                    boolean gSizeSame = false;

                    ArrayList<Double> percentages = new ArrayList<Double>();
                    for(int i = 0; i < matchingIndexi.size(); i++){
                        if(i < matchingIndexi.size()-1) {
                            allEqual = matchingIndexi.get(i).getGestureIndex() == matchingIndexi.get(i + 1).getGestureIndex();
                            gSizeSame = matchingIndexi.get(i).getMaxCharIndex() == matchingIndexi.get(i+1).getMaxCharIndex();
                        }
                        percentages.add((double)matchingIndexi.get(i).getCharIndex()/(double)matchingIndexi.get(i).getMaxCharIndex());
                    }
                    if(allEqual || gSizeSame){
                        Log.i("GESTURE_CHECKER", "matching gestures are all equal or of equal size");
                        doGestureAction(gestureCharacteristicsData.get(matchingIndexi.get(0).getGestureIndex()).get(0).get(0));

                        start = recordedData.size();
                    } else {
                        // find biggest percentage:
                        int percentindex = 0;
                        double percent = 0;
                        for(int z = 0; z < percentages.size(); z++){
                            if(percentages.get(z) > percent){
                                percent = percentages.get(z);
                                percentindex = z;
                            }
                        }
                        doGestureAction(gestureCharacteristicsData.get(matchingIndexi.get(percentindex).getGestureIndex()).get(0).get(0));
                        start = recordedData.size();
                        Log.i("GESTURE_CHECKER", "doing gesture with greatest probability...");
                    }

                } else if(matchingIndexi.size() == 1){
                    Log.i("GESTURE_CHECKER", "Found corresponding gesture");
                    doGestureAction(gestureCharacteristicsData.get(matchingIndexi.get(0).getGestureIndex()).get(0).get(0));
                    start = recordedData.size();
                    recordedCharacteristicsData.clear();
                } else {
                    // NO MATCHES
                    Log.i("GESTURE_CHECKER", "No matches found");
                }
            }
        }
    }

    private ArrayList<ComparisonItem> compareToSavedPoints(List<GesturePoint> gesturePoints) {

        ArrayList<ComparisonItem> matches = new ArrayList<ComparisonItem>();

        for (int gd = 0; gd < gestureCharacteristicsData.size(); gd++) {
            ComparisonItem compItem = new ComparisonItem();
            for(int attempt = 0; attempt < gestureCharacteristicsData.get(gd).size(); attempt++){

                boolean isSame = false;

                    for(int charac = 0; charac < gestureCharacteristicsData.get(gd).get(attempt).size(); charac ++) {
                        try {
                            isSame = compareCharacteristics(gd, charac, gesturePoints.get(charac));
                            manageComparisonItem(compItem, matches, gd, attempt, charac);
                        } catch (IndexOutOfBoundsException ie) {
                            ie.printStackTrace();
                            if (isSame && charac > Globals.MINIMUM_GESTURE_CHARACTERISTICS - 1) {
                                Log.i("GESTURE_CHECKER", "First few points were same");
                                manageComparisonItem(compItem, matches, gd, attempt, charac);
                                isSame = true;
                            } else {
                                isSame = false;
                            }
                            break;
                        }

                        if (!isSame) {
                            break;
                        }
                    }
                if(isSame && !matches.contains(compItem)){
                    matches.add(compItem);
                }
            }
        }

        return matches;
    }

    private void manageComparisonItem(ComparisonItem compItem, ArrayList<ComparisonItem> matches, int gd, int attempt, int charac) {
        if(matches.contains(compItem)){
            ComparisonItem ci = matches.get(matches.indexOf(compItem));
            if(!(ci.getCharIndex() == ci.getMaxCharIndex()) && ci.getCharIndex() < compItem.getCharIndex()){ // or compare percentage of correlation
                updateComparisonItemValues(compItem, gd, charac, attempt);
            }
        } else {
            updateComparisonItemValues(compItem, gd, charac, attempt);
        }
    }

    private void updateComparisonItemValues(ComparisonItem compItem, int gd, int charac, int attempt) {
        compItem.setGestureIndex(gd);
        compItem.setCharIndex(charac);
        compItem.setMaxCharIndex(gestureCharacteristicsData.get(gd).get(attempt).size());
    }

    private boolean compareCharacteristics(int gd, int charac, GesturePoint recordedPoint) {
        boolean matchGDX = false;
        boolean matchGDY = false;
        boolean matchADX = false;
        boolean matchADY = false;
        boolean matchADZ = false;

        for(int attemptIntern = 0; attemptIntern < gestureCharacteristicsData.get(gd).size(); attemptIntern++) {
            GesturePoint savedPoint = gestureCharacteristicsData.get(gd).get(attemptIntern).get(charac);
            if (recordedPoint.getD_xG() > Globals.MIN_DELTA_GYRO && !(recordedPoint.getD_xG() > savedPoint.getD_xG() - Globals.VARIANCE_GYRO &&
                    recordedPoint.getD_xG() < savedPoint.getD_xG() + Globals.VARIANCE_GYRO)) {
                matchGDX = true;
            } else if (recordedPoint.getD_xG() < Globals.MIN_DELTA_GYRO){
                matchGDX = true;
            }
            if (recordedPoint.getD_yG() > Globals.MIN_DELTA_GYRO && !(recordedPoint.getD_yG() > savedPoint.getD_yG() - Globals.VARIANCE_GYRO &&
                    recordedPoint.getD_yG() < savedPoint.getD_yG() + Globals.VARIANCE_GYRO)) {
                matchGDY = true;
            } else if (recordedPoint.getD_yG() < Globals.MIN_DELTA_GYRO) {
                matchGDY = true;
            }

            if (recordedPoint.getD_xA() > Globals.MIN_DELTA_ACCELERATION) {
                if ((recordedPoint.getD_xA() > savedPoint.getD_xA() - Globals.VARIANCE_ACCELERATION &&
                        recordedPoint.getD_xA() < savedPoint.getD_xA() + Globals.VARIANCE_ACCELERATION)) {
                    matchADX = true;
                }
            } else {
                matchADX = true;
            }
            if (recordedPoint.getD_yA() > Globals.MIN_DELTA_ACCELERATION) {
                if ((recordedPoint.getD_yA() > savedPoint.getD_yA() - Globals.VARIANCE_ACCELERATION &&
                        recordedPoint.getD_yA() < savedPoint.getD_yA() + Globals.VARIANCE_ACCELERATION)) {
                    matchADY = true;
                }
            } else {
                matchADY = true;
            }
            if (recordedPoint.getD_zA() > Globals.MIN_DELTA_ACCELERATION) {
                if ((recordedPoint.getD_zA() > savedPoint.getD_zA() - Globals.VARIANCE_ACCELERATION &&
                        recordedPoint.getD_zA() < savedPoint.getD_zA() + Globals.VARIANCE_ACCELERATION)) {
                    matchADZ = true;
                }
            } else {
                matchADZ = true;
            }

            if(matchADX && matchADY && matchADZ && matchGDX && matchGDY){
                Log.i("Gesture_checker", "found a good point");
            }
        }

        return (matchADX && matchADY && matchADZ && matchGDX && matchGDY);
    }

    private boolean checkSizeMatch(ArrayList<ArrayList<GesturePoint>> arrayLists, int size) {
        /**
         * Checks if there is a gesture alteration saved with the same length as the recorded one
         */
        for(int i = 0; i < arrayLists.size(); i++){
            if(arrayLists.get(i).size() == size){
                return true;
            }
        }

        return false;
    }

    private void doGestureAction(GesturePoint firstGesturePoint) {
        /**
         * Sends an intent with the gestureAction as IntentAction.
         * All SnachGestures are recognized in GestureReceiver-Class,
         * 3rd-Party-Apps can provide their own gestureAction (combined
         * with their package name) and listen for it. The extra GESTURE_APP
         * can be used by these 3rd-Party-Apps to distinguish gestures.
         */

        // check gestureMode and gestureApp....
        String gestureAction = firstGesturePoint.getAction();
        Intent recordIntent = new Intent();
        recordIntent.setAction(gestureAction);
        recordIntent.putExtra(SnachExtras.INTENT_EXTRA_GESTURE_APP, firstGesturePoint.getApp());
        recordIntent.putExtra(SnachExtras.INTENT_EXTRA_GESTURE_MODE, firstGesturePoint.getMode());
        context.sendBroadcast(recordIntent);

        /*// for testing purpose:

        if(gestureAction.equals(SnachExtras.GESTURE_ACTION_HOMESCREEN)) {
            Intent recordIntent = new Intent();
            recordIntent.setAction(SnachExtras.GESTURE_ACTION_HOMESCREEN);
            context.sendBroadcast(recordIntent);
        }
        else if (gestureAction.equals(SnachExtras.GESTURE_ACTION_PLAYMUSIC)) {
            Intent recordIntent = new Intent();
            recordIntent.setAction(SnachExtras.GESTURE_ACTION_PLAYMUSIC);
            context.sendBroadcast(recordIntent);
        }*/
    }

    private boolean isExistingGestureWithStart(GesturePoint recorededGP) {
        /**
         * Checks whether there is a gesture saved who's starting point matches the current GesturePoint.
         */
        for(int i = 0; i < gestureCharacteristicsData.size(); i++){
            for(int c = 0; c < gestureCharacteristicsData.get(i).size(); c++) {
                GesturePoint savedGP = gestureCharacteristicsData.get(i).get(c).get(0);
                if (     /*recorededGP.getxA() > savedGP.getxA()-Globals.VARIANCE_ACCELERATION &&
                    recorededGP.getxA() < savedGP.getxA()+Globals.VARIANCE_ACCELERATION &&
                    recorededGP.getyA() > savedGP.getyA()-Globals.VARIANCE_ACCELERATION &&
                    recorededGP.getyA() < savedGP.getyA()+Globals.VARIANCE_ACCELERATION &&
                    recorededGP.getzA() > savedGP.getzA()-Globals.VARIANCE_ACCELERATION &&
                    recorededGP.getzA() < savedGP.getzA()+Globals.VARIANCE_ACCELERATION &&*/

                        recorededGP.getxG() > savedGP.getxG() - Globals.VARIANCE_GYRO &&
                                recorededGP.getxG() < savedGP.getxG() + Globals.VARIANCE_GYRO &&
                                recorededGP.getyG() > savedGP.getyG() - Globals.VARIANCE_GYRO &&
                                recorededGP.getyG() < savedGP.getyG() + Globals.VARIANCE_GYRO
                    /*recorededGP.getzG() > savedGP.getzG()-Globals.VARIANCE_GYRO &&
                    recorededGP.getzG() < savedGP.getzG()+Globals.VARIANCE_GYRO*/
                        ) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void OnDeltaAccelGyroRecognized(GesturePoint recognizedGP) {
        Log.i("GESTURE_DELATA", "detextec dxa " +recognizedGP.getD_xA());
        recognizedGP.setTimeMillis(System.currentTimeMillis());

        if(isRecording) {
            recordedCharacteristicsData.add(recognizedGP);
        } else if(isExistingGestureWithPoint(recognizedGP)){

            removeOldGesturePoints(recordedCharacteristicsData);
            recordedCharacteristicsData.add(recognizedGP);
            if(recordedCharacteristicsData.size() >= Globals.MINIMUM_GESTURE_CHARACTERISTICS) {
                checkSavedGestures(recordedCharacteristicsData);
            }

            Log.i("GESTURE_DELATA", "iscaracteristcs! caracteristics size:  "+recordedCharacteristicsData.size());
        } else {

            Log.i("GESTURE_DELATA", "No Match in gestures and not recording...");
        }
    }

    private boolean isExistingGestureWithPoint(GesturePoint point){
        boolean isExisting = false;
        for (int gd = 0; gd < gestureCharacteristicsData.size(); gd++) {
            for(int attempt = 0; attempt < gestureCharacteristicsData.get(gd).size(); attempt++){
                for(int charac = 0; charac < gestureCharacteristicsData.get(gd).get(attempt).size(); charac ++){
                    try {
                        isExisting = compareCharacteristics(gd, charac, point);
                    } catch (IndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
                    if(isExisting){
                        Log.i("GESTURE_DELATA", "is gest with point");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isExistingGestureWithStartPoint(GesturePoint point){
        boolean isExisting = false;
        for (int gd = 0; gd < gestureCharacteristicsData.size(); gd++) {
            for(int attempt = 0; attempt < gestureCharacteristicsData.get(gd).size(); attempt++) {
                isExisting = compareCharacteristics(gd, 0, point);
                if (isExisting) {
                    Log.i("GESTURE_DELATA", "is gest with start");
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkForNonCharactEnd(ArrayList<GesturePoint> recordedData) {
        boolean isFalseEnd = true;
        Log.i("GESTURE_ENDCHECKER", "check for end: recordeddata size: " + recordedData.size());

        for (int gd = 0; gd < gestureCharacteristicsData.size(); gd++) {
            for(int attempt = 0; attempt < gestureCharacteristicsData.get(gd).size(); attempt++){
                for(int charac = 0; charac < gestureCharacteristicsData.get(gd).get(attempt).size(); charac ++){
                    try {
                        isFalseEnd = !compareCharacteristics(gd, charac, recordedData.get(charac));
                    } catch (IndexOutOfBoundsException ie){
                        ie.printStackTrace();
                        isFalseEnd = true;
                    }

                }
            }
        }

        return isFalseEnd;
    }

    private void removeOldGesturePoints(ArrayList<GesturePoint> recordedCharacteristicsData) {
        long ct = System.currentTimeMillis();
        for(int g = 0; g < recordedCharacteristicsData.size(); g++){
            if(ct - recordedCharacteristicsData.get(g).getTimeMillis() > Globals.ONE_SECOND) {
                recordedCharacteristicsData.remove(g);
                g--;
            }
        }
    }

    @Override
    public void OnGestureStarted(boolean isStarted) {
        Log.i("GESTURE_CHECKER", "gesture STARTED!");
//        isGestureStarted = true;
//        deltaAccelGyroRecognizer.setRecordStarted(true);
    }

    public void prepareGestureRecording(String extraApp, String extraMode, String gestureName, String gestureAction) {
        Log.i("GESTURE_PROC._HANDLER", "preparing recording");
        recordedCharacteristicsData.clear();

        this.isRecording = true;
        this.isRecognizing = false;
        this.isGestureStarted = false;

        // Finish recording after 1 sec:
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                isRecording = false;

                gestureAttemptsCounter++;
                if(gestureAttemptsCounter <= Globals.GESTURE_RECORDING_ATTEMPTS){
                    Log.i("RECOGNIZER", "recording..."+gestureAttemptsCounter);
                    cacheGesture(recordedCharacteristicsData);
                    recordedCharacteristicsData = new ArrayList<GesturePoint>();
                } else {
                    Log.i("RECOGNIZER", "saving...");

                    cacheGesture(recordedCharacteristicsData);
                    recordedCharacteristicsData = new ArrayList<GesturePoint>();

                    saveNewGesturesCharacteristics(cachedGestures);
                    resetGestureCache();
                }

            }
        }, 1000);

        this.recordingGestureApp = extraApp;
        this.recordingGestureMode = extraMode;
        this.recordingGestureName = gestureName;
        this.recordingGestureAction = gestureAction;
    }

    public void refreshGestureDataList(ArrayList<ArrayList<ArrayList<GesturePoint>>> gestureCharacteristicsData) {
        this.gestureCharacteristicsData = gestureCharacteristicsData;
    }

    public interface OnGestureAddedListener {
        public void OnGestureAdded();
        public void OnGestureAttempt(int attempt);
    }
}
