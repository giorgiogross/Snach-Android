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

/**
 * Created by Giorgio on 16.05.15.
 */
public class SnachAppItem {
    private int ID, snachScreenIndex;
    private String appName;
    private String appDescription;
    private String appPackage;
    private String appBCAction;
    private String appBCExtra;
    private String intentExtra;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getSnachScreenIndex() {
        return snachScreenIndex;
    }

    public void setSnachScreenIndex(int snachScreenIndex) {
        this.snachScreenIndex = snachScreenIndex;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppBCAction() {
        return appBCAction;
    }

    public void setAppBCAction(String appBCAction) {
        this.appBCAction = appBCAction;
    }

    public String getAppBCExtra() {
        return appBCExtra;
    }

    public void setAppBCExtra(String appBCExtra) {
        this.appBCExtra = appBCExtra;
    }

    public String getIntentExtra() {
        return intentExtra;
    }

    public void setIntentExtra(String intentExtra) {
        this.intentExtra = intentExtra;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public String getAppDescription() {
        return appDescription;
    }
}
