package assembtec.com.snach.StandardSnachApps;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import assembtec.com.snach_core_lib.ActionAppContentItem;
import assembtec.com.snach_core_lib.SnachExtras;
import assembtec.com.snach_core_lib.SnachRemoteHandler;
import assembtec.com.snach_core_lib.SnachServiceEvent;

/**
 * Created by Giorgio on 11.06.15.
 */
public class StandardAppStopwatch extends Service implements SnachServiceEvent{
    private SnachRemoteHandler mSnachRemote;
    private boolean isTimerRunning = false;
    private ActionAppContentItem AACI;

    private Handler delayHandler;
    private Runnable delayRunnable;
    private int currentTime = 0;

    public StandardAppStopwatch () {}

    @Override
    public IBinder onBind(Intent intent) {
        // Return your IBinder if you want to be able to bind to this service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("STND_STOPWATCH", "starting stopwatch service");
        setUp();
        return START_STICKY;
    }

    private void setUp() {
        mSnachRemote = new SnachRemoteHandler(getApplicationContext(), this);
        mSnachRemote.begin();
    }

    /**
     * Snach Service Callbacks:
     */
    @Override
    public void onServiceDisconnected() {
        this.stopSelf();
    }

    @Override
    public void onSensorDataReceived(int xAccel, int yAccel, int zAccel, int xGyro, int yGyro, int zGyro) {

    }

    @Override
    public void onSnachConnectionResult(boolean isConnected) {
        Log.i("STND_STOPWATCH", "Setting up aaci: " + isConnected);
        if(isConnected){
            initTimer();
            setUpDefaultAACI();
            sendAACI();
        }
    }

    private void initTimer() {
        delayHandler = new Handler();
        delayRunnable = new Runnable() {
            @Override
            public void run() {
                Log.i("STND_STOPWATCH", "elevating: "+currentTime);
                currentTime++;
                updateTimerAACIContent(currentTime);
                sendAACI();
                if(isTimerRunning){
                    delayHandler.postDelayed(this, 1000);
                }
            }
        };
    }

    @Override
    public void onSnachButtonPressed(int button_id) {
        if(button_id == SnachExtras.APP_BUTTON_BOTTOM_ID) {
            if(isTimerRunning){
                pauseTimer();
                AACI.setBOTTOM_BUTTON_ICON(SnachExtras.BUTTON_ICON_PLAY);
                sendAACI();
            } else {
                setUpTimerAACI();
                updateTimerAACIContent(currentTime);
                sendAACI();
                startTimer();
            }
        } else if(button_id == SnachExtras.APP_BUTTON_RIGHT_ID){
            currentTime = 0;
            if(isTimerRunning){
                setUpTimerAACI();
                updateTimerAACIContent(currentTime);
                sendAACI();
            } else {
                setUpDefaultAACI();
                sendAACI();
            }
        } else if(button_id == SnachExtras.APP_BUTTON_LEFT_ID){
            if(isTimerRunning){
                stopTimer();
                setUpDefaultAACI();
                sendAACI();
            }
        }
    }

    private void pauseTimer(){
        delayHandler.removeCallbacks(delayRunnable);
        isTimerRunning = false;
    }

    private void stopTimer() {
        delayHandler.removeCallbacks(delayRunnable);
        isTimerRunning = false;
        currentTime = 0;
    }

    private void sendAACI() {
        if(AACI != null) {
            mSnachRemote.sendAppContent(AACI);
        }
    }

    private void setUpDefaultAACI() {
        ActionAppContentItem aaci = new ActionAppContentItem();
        aaci.setSCREEN_TITLE("00:00");
        aaci.setSCREEN_CONTENT("Current time");
        aaci.setSCREEN_MODE(SnachExtras.SCREENMODE_ACTIONLAYOUT);
        aaci.setCOLOR_BACK(SnachExtras.COLOR_WHITE);
        aaci.setCOLOR_CONTENT(SnachExtras.COLOR_BLACK);
        aaci.setCOLOR_TITLE(SnachExtras.COLOR_BLACK);
        aaci.setCOLOR_HIGHLIGHT(SnachExtras.COLOR_BLACK);
        aaci.setCOLOR_MAIN(SnachExtras.COLOR_WHITE);
        aaci.setBOTTOM_BUTTON_ICON(SnachExtras.BUTTON_ICON_PLAY);
        aaci.setRIGHT_BUTTON_ICON(SnachExtras.BUTTON_ICON_REWIND_LEFT);
        aaci.setLEFT_BUTTON_ICON(SnachExtras.BUTTON_ICON_STOP);
        aaci.setBACK_THEME(SnachExtras.BACK_THEME_0);
        aaci.setFONT_THEME(SnachExtras.ACTION_FONT_THEME_2);
        aaci.setICON_THEME(SnachExtras.ICON_THEME_0);
        this.AACI = aaci;
    }

    private void startTimer() {
        isTimerRunning = true;
        delayHandler.postDelayed(delayRunnable, 1000);
    }

    private void updateTimerAACIContent(int currentTime) {
        int sec = currentTime % 60;
        int min = currentTime/60;
        String secS = String.valueOf(sec);
        String minS = String.valueOf(min);
        if(sec < 10){secS = "0"+secS;}
        if(min < 10){minS = "0"+minS;}
        AACI.setSCREEN_TITLE(minS+":"+secS);
        AACI.setSCREEN_CONTENT("Timer running..");
    }

    private void setUpTimerAACI() {
        ActionAppContentItem aaci = new ActionAppContentItem();
        aaci.setSCREEN_MODE(SnachExtras.SCREENMODE_ACTIONLAYOUT);
        aaci.setCOLOR_BACK(SnachExtras.COLOR_WHITE);
        aaci.setCOLOR_CONTENT(SnachExtras.COLOR_BLACK);
        aaci.setCOLOR_TITLE(SnachExtras.COLOR_BLACK);
        aaci.setCOLOR_HIGHLIGHT(SnachExtras.COLOR_BLACK);
        aaci.setCOLOR_MAIN(SnachExtras.COLOR_WHITE);
        aaci.setBOTTOM_BUTTON_ICON(SnachExtras.BUTTON_ICON_PLAY);
        aaci.setBACK_THEME(SnachExtras.BACK_THEME_0);
        aaci.setFONT_THEME(SnachExtras.ACTION_FONT_THEME_2);
        aaci.setICON_THEME(SnachExtras.ICON_THEME_0);
        aaci.setBOTTOM_BUTTON_ICON(SnachExtras.BUTTON_ICON_PAUSE);
        aaci.setRIGHT_BUTTON_ICON(SnachExtras.BUTTON_ICON_REWIND_LEFT);
        aaci.setLEFT_BUTTON_ICON(SnachExtras.BUTTON_ICON_STOP);

        this.AACI = aaci;
    }
}

