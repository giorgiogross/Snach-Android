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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Giorgio on 03.06.15.
 */
public class PopUpContentItem implements Parcelable {
    protected int level;
    protected String title;
    protected String content;
    protected String AppBCAction; // the Action of the intent which will be broad casted when the user taps the popup action button
    protected int POP_UP_LAYER = 0; // determines if an app pushed the pop up (0) or if it was pushed by the Snach Services (e.g. Telephony (1) etc..)
    protected int popup_theme;
    protected int icon_b_left;
    protected int icon_b_right;

    public static final Parcelable.Creator<PopUpContentItem> CREATOR = new
            Parcelable.Creator<PopUpContentItem>() {
                public PopUpContentItem createFromParcel(Parcel in) {
                    return new PopUpContentItem(in);
                }

                public PopUpContentItem[] newArray(int size) {
                    return new PopUpContentItem[size];
                }
            };

    public PopUpContentItem(){

    }

    private PopUpContentItem(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(level);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeInt(popup_theme);
        dest.writeInt(icon_b_left);
        dest.writeInt(icon_b_right);
    }

    public void readFromParcel(Parcel in) {
        level = in.readInt();
        title = in.readString();
        content = in.readString();
        popup_theme = in.readInt();
        icon_b_left = in.readInt();
        icon_b_right = in.readInt();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPopup_theme() {
        return popup_theme;
    }

    public void setPopup_theme(int popup_theme) {
        this.popup_theme = popup_theme;
    }

    public int getIcon_b_left() {
        return icon_b_left;
    }

    public void setIcon_b_left(int icon_b_left) {
        this.icon_b_left = icon_b_left;
    }

    public int getIcon_b_right() {
        return icon_b_right;
    }

    public void setIcon_b_right(int icon_b_right) {
        this.icon_b_right = icon_b_right;
    }

    public String getAppBCAction() {
        return AppBCAction;
    }

    public void setAppBCAction(String appBCAction) {
        AppBCAction = appBCAction;
    }

    public int getPOP_UP_LAYER() {
        return POP_UP_LAYER;
    }

    public void setPOP_UP_LAYER(int POP_UP_LAYER) {
        this.POP_UP_LAYER = POP_UP_LAYER;
    }
}
