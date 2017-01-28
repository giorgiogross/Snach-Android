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
 * Created by Giorgio on 18.05.15.
 */
public class ActionAppContentItem implements Parcelable{
    protected int SCREEN_MODE = 2;
    protected byte BACK_THEME = 0;
    protected byte ICON_THEME = 0;
    protected byte FONT_THEME = 0;
    protected byte COLOR_BACK = 0;
    protected byte COLOR_MAIN = 0;
    protected byte COLOR_HIGHLIGHT = 0;
    protected byte COLOR_TITLE = 0;
    protected byte COLOR_CONTENT = 0;

    protected byte MAIN_ICON = 0;
    protected byte MAIN_ICON_COLOR = 0;
    protected byte MAIN_ICON_BACK = 0;

    protected byte TOP_BUTTON_ICON = 0;
    protected byte BOTTOM_BUTTON_ICON = 0;
    protected byte LEFT_BUTTON_ICON = 0;
    protected byte RIGHT_BUTTON_ICON = 0;

    protected byte TOP_BUTTON_STYLE = 0;
    protected byte RIGHT_BUTTON_STYLE = 0;
    protected byte BOTTOM_BUTTON_STYLE = 0;
    protected byte LEFT_BUTTON_STYLE = 0;

    protected byte TOP_BUTTON_BACK = 0;
    protected byte RIGHT_BUTTON_BACK = 0;
    protected byte BOTTOM_BUTTON_BACK = 0;
    protected byte LEFT_BUTTON_BACK = 0;
    protected byte TOP_BUTTON_COLOR = 0;
    protected byte RIGHT_BUTTON_COLOR = 0;
    protected byte BOTTOM_BUTTON_COLOR = 0;
    protected byte LEFT_BUTTON_COLOR = 0;

    protected String SCREEN_TITLE = "";
    protected String SCREEN_CONTENT = "";

    public static final Parcelable.Creator<ActionAppContentItem> CREATOR = new
            Parcelable.Creator<ActionAppContentItem>() {
                public ActionAppContentItem createFromParcel(Parcel in) {
                    return new ActionAppContentItem(in);
                }

                public ActionAppContentItem[] newArray(int size) {
                    return new ActionAppContentItem[size];
                }
            };

    public ActionAppContentItem(){

    }

    private ActionAppContentItem(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(SCREEN_MODE);
        dest.writeString(SCREEN_TITLE);
        dest.writeString(SCREEN_CONTENT);
        dest.writeInt(TOP_BUTTON_ICON);
        dest.writeInt(RIGHT_BUTTON_ICON);
        dest.writeInt(BOTTOM_BUTTON_ICON);
        dest.writeInt(LEFT_BUTTON_ICON);

        dest.writeByte(TOP_BUTTON_STYLE);
        dest.writeByte(RIGHT_BUTTON_STYLE);
        dest.writeByte(BOTTOM_BUTTON_STYLE);
        dest.writeByte(LEFT_BUTTON_STYLE);
        dest.writeByte(TOP_BUTTON_BACK);
        dest.writeByte(RIGHT_BUTTON_BACK);
        dest.writeByte(BOTTOM_BUTTON_BACK);
        dest.writeByte(LEFT_BUTTON_BACK);
        dest.writeByte(TOP_BUTTON_COLOR);
        dest.writeByte(RIGHT_BUTTON_COLOR);
        dest.writeByte(BOTTOM_BUTTON_COLOR);
        dest.writeByte(LEFT_BUTTON_COLOR);
        dest.writeByte(BACK_THEME);
        dest.writeByte(FONT_THEME);
        dest.writeByte(ICON_THEME);
        dest.writeByte(COLOR_BACK);
        dest.writeByte(COLOR_MAIN);
        dest.writeByte(COLOR_HIGHLIGHT);
        dest.writeByte(COLOR_TITLE);
        dest.writeByte(MAIN_ICON);
        dest.writeByte(MAIN_ICON_COLOR);
        dest.writeByte(MAIN_ICON_BACK);
    }

    public void readFromParcel(Parcel in) {
        this.SCREEN_MODE = in.readInt();
        this.SCREEN_TITLE = in.readString();
        this.SCREEN_CONTENT = in.readString();
        this.TOP_BUTTON_ICON = in.readByte();
        this.RIGHT_BUTTON_ICON = in.readByte();
        this.BOTTOM_BUTTON_ICON = in.readByte();
        this.LEFT_BUTTON_ICON = in.readByte();
        this.TOP_BUTTON_STYLE = in.readByte();
        this.RIGHT_BUTTON_STYLE = in.readByte();
        this.BOTTOM_BUTTON_STYLE = in.readByte();
        this.LEFT_BUTTON_STYLE = in.readByte();
        this.TOP_BUTTON_BACK = in.readByte();
        this.RIGHT_BUTTON_BACK = in.readByte();
        this.BOTTOM_BUTTON_BACK = in.readByte();
        this.LEFT_BUTTON_BACK = in.readByte();
        this.TOP_BUTTON_COLOR = in.readByte();
        this.RIGHT_BUTTON_COLOR= in.readByte();
        this.BOTTOM_BUTTON_COLOR = in.readByte();
        this.LEFT_BUTTON_COLOR = in.readByte();
        this.BACK_THEME = in.readByte();
        this.FONT_THEME = in.readByte();
        this.ICON_THEME = in.readByte();
        this.COLOR_BACK = in.readByte();
        this.COLOR_MAIN = in.readByte();
        this.COLOR_HIGHLIGHT = in.readByte();
        this.COLOR_TITLE = in.readByte();
        this.MAIN_ICON = in.readByte();
        this.MAIN_ICON_COLOR = in.readByte();
        this.MAIN_ICON_BACK = in.readByte();

    }

    public int getSCREEN_MODE() {
        return SCREEN_MODE;
    }

    public void setSCREEN_MODE(int SCREEN_MODE) {
        this.SCREEN_MODE = SCREEN_MODE;
    }

    public byte getLEFT_BUTTON_ICON() {
        return LEFT_BUTTON_ICON;
    }

    public void setLEFT_BUTTON_ICON(byte LEFT_BUTTON_ICON) {
        this.LEFT_BUTTON_ICON = LEFT_BUTTON_ICON;
    }

    public byte getRIGHT_BUTTON_ICON() {
        return RIGHT_BUTTON_ICON;
    }

    public void setRIGHT_BUTTON_ICON(byte RIGHT_BUTTON_ICON) {
        this.RIGHT_BUTTON_ICON = RIGHT_BUTTON_ICON;
    }

    public byte getTOP_BUTTON_ICON() {
        return TOP_BUTTON_ICON;
    }

    public void setTOP_BUTTON_ICON(byte TOP_BUTTON_ICON) {
        this.TOP_BUTTON_ICON = TOP_BUTTON_ICON;
    }

    public byte getBOTTOM_BUTTON_ICON() {
        return BOTTOM_BUTTON_ICON;
    }

    public void setBOTTOM_BUTTON_ICON(byte BOTTOM_BUTTON_ICON) {
        this.BOTTOM_BUTTON_ICON = BOTTOM_BUTTON_ICON;
    }

    public String getSCREEN_TITLE() {
        return SCREEN_TITLE;
    }

    public void setSCREEN_TITLE(String SCREEN_TITLE) {
        this.SCREEN_TITLE = SCREEN_TITLE;
    }

    public String getSCREEN_CONTENT() {
        return SCREEN_CONTENT;
    }

    public void setSCREEN_CONTENT(String SCREEN_CONTENT) {
        this.SCREEN_CONTENT = SCREEN_CONTENT;
    }

    public int getCOLOR_MAIN() {
        return COLOR_MAIN;
    }

    public void setCOLOR_MAIN(byte COLOR_MAIN) {
        this.COLOR_MAIN = COLOR_MAIN;
    }

    public int getCOLOR_HIGHLIGHT() {
        return COLOR_HIGHLIGHT;
    }

    public void setCOLOR_HIGHLIGHT(byte COLOR_HIGHLIGHT) {
        this.COLOR_HIGHLIGHT = COLOR_HIGHLIGHT;
    }

    public int getCOLOR_TITLE() {
        return COLOR_TITLE;
    }

    public void setCOLOR_TITLE(byte COLOR_TITLE) {
        this.COLOR_TITLE = COLOR_TITLE;
    }

    public int getCOLOR_CONTENT() {
        return COLOR_CONTENT;
    }

    public void setCOLOR_CONTENT(byte COLOR_CONTENT) {
        this.COLOR_CONTENT = COLOR_CONTENT;
    }

    public byte getBACK_THEME() {
        return BACK_THEME;
    }

    public void setBACK_THEME(byte BACK_THEME) {
        this.BACK_THEME = BACK_THEME;
    }

    public byte getICON_THEME() {
        return ICON_THEME;
    }

    public void setICON_THEME(byte ICON_THEME) {
        this.ICON_THEME = ICON_THEME;
    }

    public byte getFONT_THEME() {
        return FONT_THEME;
    }

    public void setFONT_THEME(byte FONT_THEME) {
        this.FONT_THEME = FONT_THEME;
    }

    public byte getTOP_BUTTON_STYLE() {
        return TOP_BUTTON_STYLE;
    }

    public void setTOP_BUTTON_STYLE(byte TOP_BUTTON_STYLE) {
        this.TOP_BUTTON_STYLE = TOP_BUTTON_STYLE;
    }

    public byte getRIGHT_BUTTON_STYLE() {
        return RIGHT_BUTTON_STYLE;
    }

    public void setRIGHT_BUTTON_STYLE(byte RIGHT_BUTTON_STYLE) {
        this.RIGHT_BUTTON_STYLE = RIGHT_BUTTON_STYLE;
    }

    public byte getBOTTOM_BUTTON_STYLE() {
        return BOTTOM_BUTTON_STYLE;
    }

    public void setBOTTOM_BUTTON_STYLE(byte BOTTOM_BUTTON_STYLE) {
        this.BOTTOM_BUTTON_STYLE = BOTTOM_BUTTON_STYLE;
    }

    public byte getLEFT_BUTTON_STYLE() {
        return LEFT_BUTTON_STYLE;
    }

    public void setLEFT_BUTTON_STYLE(byte LEFT_BUTTON_STYLE) {
        this.LEFT_BUTTON_STYLE = LEFT_BUTTON_STYLE;
    }

    public int getCOLOR_BACK() {

        return COLOR_BACK;
    }

    public void setCOLOR_BACK(byte COLOR_BACK) {
        this.COLOR_BACK = COLOR_BACK;
    }

    public int getMAIN_ICON() {
        return MAIN_ICON;
    }

    public void setMAIN_ICON(byte MAIN_ICON) {
        this.MAIN_ICON = MAIN_ICON;
    }

    public int getMAIN_ICON_COLOR() {
        return MAIN_ICON_COLOR;
    }

    public void setMAIN_ICON_COLOR(byte MAIN_ICON_COLOR) {
        this.MAIN_ICON_COLOR = MAIN_ICON_COLOR;
    }

    public int getMAIN_ICON_BACK() {
        return MAIN_ICON_BACK;
    }

    public void setMAIN_ICON_BACK(byte MAIN_ICON_BACK) {
        this.MAIN_ICON_BACK = MAIN_ICON_BACK;
    }

    public int getTOP_BUTTON_BACK() {
        return TOP_BUTTON_BACK;
    }

    public void setTOP_BUTTON_BACK(byte TOP_BUTTON_BACK) {
        this.TOP_BUTTON_BACK = TOP_BUTTON_BACK;
    }

    public int getRIGHT_BUTTON_BACK() {
        return RIGHT_BUTTON_BACK;
    }

    public void setRIGHT_BUTTON_BACK(byte RIGHT_BUTTON_BACK) {
        this.RIGHT_BUTTON_BACK = RIGHT_BUTTON_BACK;
    }

    public int getBOTTOM_BUTTON_BACK() {
        return BOTTOM_BUTTON_BACK;
    }

    public void setBOTTOM_BUTTON_BACK(byte BOTTOM_BUTTON_BACK) {
        this.BOTTOM_BUTTON_BACK = BOTTOM_BUTTON_BACK;
    }

    public int getLEFT_BUTTON_BACK() {
        return LEFT_BUTTON_BACK;
    }

    public void setLEFT_BUTTON_BACK(byte LEFT_BUTTON_BACK) {
        this.LEFT_BUTTON_BACK = LEFT_BUTTON_BACK;
    }

    public int getTOP_BUTTON_COLOR() {
        return TOP_BUTTON_COLOR;
    }

    public void setTOP_BUTTON_COLOR(byte TOP_BUTTON_COLOR) {
        this.TOP_BUTTON_COLOR = TOP_BUTTON_COLOR;
    }

    public int getRIGHT_BUTTON_COLOR() {
        return RIGHT_BUTTON_COLOR;
    }

    public void setRIGHT_BUTTON_COLOR(byte RIGHT_BUTTON_COLOR) {
        this.RIGHT_BUTTON_COLOR = RIGHT_BUTTON_COLOR;
    }

    public int getBOTTOM_BUTTON_COLOR() {
        return BOTTOM_BUTTON_COLOR;
    }

    public void setBOTTOM_BUTTON_COLOR(byte BOTTOM_BUTTON_COLOR) {
        this.BOTTOM_BUTTON_COLOR = BOTTOM_BUTTON_COLOR;
    }

    public int getLEFT_BUTTON_COLOR() {
        return LEFT_BUTTON_COLOR;
    }

    public void setLEFT_BUTTON_COLOR(byte LEFT_BUTTON_COLOR) {
        this.LEFT_BUTTON_COLOR = LEFT_BUTTON_COLOR;
    }
}
