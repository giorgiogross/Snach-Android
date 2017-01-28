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
 package assembtec.com.snach_core_lib;

/**
 * Created by Giorgio on 16.04.2015.
 */
public class GesturePoint {
    private int xG;
    private int yG;
    private int zG;
    private int d_xG;
    private int d_yG;
    private int d_zG;
    private int xA;
    private int yA;
    private int zA;
    private int d_xA;
    private int d_yA;
    private int d_zA;
    private int ID;
    private int dT;
    private int duration;
    private boolean isCharacteristics;

    private String action;
    private String app;
    private String mode;

    private long timeMillis;

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public boolean isCharacteristics() {
        return isCharacteristics;
    }

    public void setCharacteristics(boolean isCharacteristics) {
        this.isCharacteristics = isCharacteristics;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getdT() {
        return dT;
    }

    public void setdT(int dT) {
        this.dT = dT;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getD_xG() {
        return d_xG;
    }

    public void setD_xG(int d_xG) {
        this.d_xG = d_xG;
    }

    public int getD_yG() {
        return d_yG;
    }

    public void setD_yG(int d_yG) {
        this.d_yG = d_yG;
    }

    public int getD_zG() {
        return d_zG;
    }

    public void setD_zG(int d_zG) {
        this.d_zG = d_zG;
    }

    public int getD_xA() {
        return d_xA;
    }

    public void setD_xA(int d_xA) {
        this.d_xA = d_xA;
    }

    public int getD_yA() {
        return d_yA;
    }

    public void setD_yA(int d_yA) {
        this.d_yA = d_yA;
    }

    public int getD_zA() {
        return d_zA;
    }

    public void setD_zA(int d_zA) {
        this.d_zA = d_zA;
    }

    public int getxG() {
        return xG;
    }

    public void setxG(int xG) {
        this.xG = xG;
    }

    public int getyG() {
        return yG;
    }

    public void setyG(int yG) {
        this.yG = yG;
    }

    public int getzG() {
        return zG;
    }

    public void setzG(int zG) {
        this.zG = zG;
    }

    public int getxA() {
        return xA;
    }

    public void setxA(int xA) {
        this.xA = xA;
    }

    public int getyA() {
        return yA;
    }

    public void setyA(int yA) {
        this.yA = yA;
    }

    public int getzA() {
        return zA;
    }

    public void setzA(int zA) {
        this.zA = zA;
    }
}
