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
