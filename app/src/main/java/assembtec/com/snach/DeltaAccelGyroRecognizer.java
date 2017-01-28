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

import android.util.Log;

import assembtec.com.snach_core_lib.GesturePoint;

/**
 * Created by Giorgio on 18.04.2015.
 */
public class DeltaAccelGyroRecognizer {
    private DeltaListener deltaListener;
    private int xAccelLast;
    private int yAccelLast;
    private int zAccelLast;
    private int xGyroLast;
    private int yGyroLast;
    private int zGyroLast;
    private int timeLast;

    public DeltaAccelGyroRecognizer(GestureProcessingHandler mHandler){
        this.deltaListener = (DeltaListener) mHandler;
    }

    public void setFirstPoint(GesturePoint firstGP){
        xAccelLast = firstGP.getxA();
        yAccelLast = firstGP.getyA();
        zAccelLast = firstGP.getzA();
        xGyroLast = firstGP.getxG();
        yGyroLast = firstGP.getyG();
        zGyroLast = firstGP.getzG();
        timeLast = 0;
    }

    public void overrideLastPoint(GesturePoint nextGP){
        xAccelLast = nextGP.getxA();
        yAccelLast = nextGP.getyA();
        zAccelLast = nextGP.getzA();
        xGyroLast = nextGP.getxG();
        yGyroLast = nextGP.getyG();
        zGyroLast = nextGP.getzG();
        timeLast = nextGP.getdT();
    }

    public void setNextPoint(GesturePoint nextGP) {
        boolean isCharacteristics = isCharacteristics(nextGP);
        if (isCharacteristics) {
            Log.i("GESTURE_DELATA", "is characteristics!");
            nextGP.setCharacteristics(true);
//            deltaListener.OnGestureStarted(true);
            deltaListener.OnDeltaAccelGyroRecognized(nextGP);
            overrideLastPoint(nextGP);
        }
    }

    public boolean isPointDeltaCharacteristics(GesturePoint p){
        return (
                        Math.abs(p.getD_xA()) >= Globals.MIN_DELTA_ACCELERATION ||
                        Math.abs(p.getD_yA()) >= Globals.MIN_DELTA_ACCELERATION ||
                        Math.abs(p.getD_zA()) >= Globals.MIN_DELTA_ACCELERATION ||
                        Math.abs(p.getD_xG()) >= Globals.MIN_DELTA_GYRO ||
                        Math.abs(p.getD_yG()) >= Globals.MIN_DELTA_GYRO /*||
                        Math.abs(deltaZG) >= Globals.MIN_DELTA_GYRO*/
                );
    }

    private boolean isCharacteristics(GesturePoint nextGP) {
        /*int deltaXA = Math.abs(Math.abs(xAccelLast)-Math.abs(nextGP.getxA()));
        int deltaYA = Math.abs(Math.abs(yAccelLast)-Math.abs(nextGP.getyA()));
        int deltaZA = Math.abs(Math.abs(zAccelLast)-Math.abs(nextGP.getzA()));
        int deltaXG = Math.abs(Math.abs(xGyroLast)-Math.abs(nextGP.getxG()));
        int deltaYG = Math.abs(Math.abs(yGyroLast)-Math.abs(nextGP.getyG()));
        int deltaZG = Math.abs(Math.abs(zGyroLast)-Math.abs(nextGP.getzG()));*/

        int deltaXA = nextGP.getxA() - xAccelLast;
        int deltaYA = nextGP.getyA() - yAccelLast;
        int deltaZA = nextGP.getzA() - zAccelLast;
        int deltaXG = nextGP.getxG() - xGyroLast;
        int deltaYG = nextGP.getyG() - yGyroLast;
        int deltaZG = nextGP.getzG() - zGyroLast;

        Log.i("GESTURE_DELATA", "\ndelta xA: "+deltaXA+
                "\ndelta yA: "+deltaYA+
                "\ndelta zA: "+deltaZA+
                "\ndelta xG: "+deltaXG+
                "\ndelta yG: "+deltaYG+
                "\ndelta zG: "+deltaZG);

        nextGP.setD_xA(deltaXA);
        nextGP.setD_yA(deltaYA);
        nextGP.setD_zA(deltaZA);
        nextGP.setD_xG(deltaXG);
        nextGP.setD_yG(deltaYG);
        nextGP.setD_zG(deltaZG);

        return isPointDeltaCharacteristics(nextGP);

    }


    public interface DeltaListener {
        /**
         * Called every time a characteristics is recognized.
         * (When a delta in acceleration or gyro is recognized)
         */
        public void OnDeltaAccelGyroRecognized(GesturePoint recognizedGP);

        /** Called when the first characteristics is recognized.
         * (When the first delta in acceleration or gyro is recognized)
         *
         * Cancel listening 500ms after this call!
         */
        public void OnGestureStarted(boolean isStarted);

    }
}
