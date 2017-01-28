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

import java.util.ArrayList;

/**
 * Created by Giorgio on 18.05.15.
 */
public class ListAppContentItem implements Parcelable {
    protected int SCREEN_MODE = 1;
    protected byte BACK_THEME = 0;
    protected byte FONT_THEME = 0;
    protected int COLOR_BACK = 0;
    protected int COLOR_MAIN = 0;
    protected int COLOR_HIGHLIGHT = 0;
    protected int COLOR_TITLE = 0;
    protected int COLOR_CONTENT = 0;

    protected byte ITEM_THEME = 0;

    protected byte TOP_BUTTON_ICON = 0;
    protected byte BOTTOM_BUTTON_ICON = 0;
    protected byte LEFT_BUTTON_ICON = 0;
    protected byte RIGHT_BUTTON_ICON = 0;

    protected byte TOP_BUTTON_ICON_ENDSTATE = 0;
    protected byte BOTTOM_BUTTON_ICON_ENDSTATE = 0;

    protected byte TOP_BUTTON_STYLE_ENDSTATE = 0;
    protected byte BOTTOM_BUTTON_STYLE_ENDSTATE = 0;

    protected byte RIGHT_BUTTON_STYLE = 0;
    protected byte LEFT_BUTTON_STYLE = 0;

    protected int TOP_BUTTON_BACK;
    protected int RIGHT_BUTTON_BACK;
    protected int BOTTOM_BUTTON_BACK;
    protected int LEFT_BUTTON_BACK;
    protected int TOP_BUTTON_COLOR;
    protected int RIGHT_BUTTON_COLOR;
    protected int BOTTOM_BUTTON_COLOR;
    protected int LEFT_BUTTON_COLOR;

    protected ArrayList ITEMS_TITLE;
    protected ArrayList ITEMS_SUBTITLE;
    protected ArrayList ITEMS_CONTENT;

    protected String listTitle_top = null;
    protected String listTitle_bottom = null;

    protected String defaultText = "No data";

    /**
     * ButtonIcons:
     * 0 = No icon, content ignores Button
     * 1 = No icon, content is aligned not to cover Button
     * >1 = IconID specified and read by SnachSystem.
     *
     * Max. 255 icons...!
     */

    public static final Parcelable.Creator<ListAppContentItem> CREATOR = new
            Parcelable.Creator<ListAppContentItem>() {
                public ListAppContentItem createFromParcel(Parcel in) {
                    return new ListAppContentItem(in);
                }

                public ListAppContentItem[] newArray(int size) {
                    return new ListAppContentItem[size];
                }
            };

    public ListAppContentItem(){

    }

    private ListAppContentItem(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(SCREEN_MODE);
        dest.writeInt(COLOR_BACK);
        dest.writeInt(COLOR_MAIN);
        dest.writeInt(COLOR_HIGHLIGHT);
        dest.writeInt(COLOR_TITLE);
        dest.writeInt(COLOR_CONTENT);

        dest.writeByte(BACK_THEME);
        dest.writeByte(FONT_THEME);
        dest.writeByte(ITEM_THEME);

        dest.writeList(ITEMS_TITLE);
        dest.writeList(ITEMS_SUBTITLE);
        dest.writeList(ITEMS_CONTENT);

        dest.writeInt(TOP_BUTTON_ICON);
        dest.writeInt(RIGHT_BUTTON_ICON);
        dest.writeInt(BOTTOM_BUTTON_ICON);
        dest.writeInt(LEFT_BUTTON_ICON);
        dest.writeInt(TOP_BUTTON_ICON_ENDSTATE);
        dest.writeInt(BOTTOM_BUTTON_ICON_ENDSTATE);

        dest.writeByte(TOP_BUTTON_STYLE_ENDSTATE);
        dest.writeByte(RIGHT_BUTTON_STYLE);
        dest.writeByte(BOTTOM_BUTTON_STYLE_ENDSTATE);
        dest.writeByte(LEFT_BUTTON_STYLE);
    }

    public void readFromParcel(Parcel in) {
        this.SCREEN_MODE = in.readInt();
        this.COLOR_BACK = in.readInt();
        this.COLOR_MAIN = in.readInt();
        this.COLOR_HIGHLIGHT = in.readInt();
        this.COLOR_TITLE = in.readInt();
        this.COLOR_CONTENT = in.readInt();

        this.BACK_THEME = in.readByte();
        this.FONT_THEME = in.readByte();
        this.ITEM_THEME = in.readByte();

        this.ITEMS_TITLE = in.readArrayList(String.class.getClassLoader());
        this.ITEMS_SUBTITLE = in.readArrayList(String.class.getClassLoader());
        this.ITEMS_CONTENT = in.readArrayList(String.class.getClassLoader());

        this.listTitle_top = in.readString();
        this.listTitle_bottom = in.readString();

        this.TOP_BUTTON_ICON = in.readByte();
        this.RIGHT_BUTTON_ICON = in.readByte();
        this.BOTTOM_BUTTON_ICON = in.readByte();
        this.LEFT_BUTTON_ICON = in.readByte();
        this.TOP_BUTTON_ICON_ENDSTATE = in.readByte();
        this.BOTTOM_BUTTON_ICON_ENDSTATE = in.readByte();

        this.TOP_BUTTON_STYLE_ENDSTATE = in.readByte();
        this.LEFT_BUTTON_STYLE = in.readByte();
        this.BOTTOM_BUTTON_STYLE_ENDSTATE = in.readByte();
        this.LEFT_BUTTON_STYLE = in.readByte();
    }

    public int getSCREEN_MODE() {
        return SCREEN_MODE;
    }

    public void setSCREEN_MODE(int SCREEN_MODE) {
        this.SCREEN_MODE = SCREEN_MODE;
    }

    public ArrayList getITEMS_TITLE() {
        return ITEMS_TITLE;
    }

    public void setITEMS_TITLE(ArrayList ITEMS_TITLE) {
        this.ITEMS_TITLE = ITEMS_TITLE;
    }

    public ArrayList getITEMS_SUBTITLE() {
        return ITEMS_SUBTITLE;
    }

    public void setITEMS_SUBTITLE(ArrayList ITEMS_SUBTITLE) {
        this.ITEMS_SUBTITLE = ITEMS_SUBTITLE;
    }

    public ArrayList getITEMS_CONTENT() {
        return ITEMS_CONTENT;
    }

    public void setITEMS_CONTENT(ArrayList ITEMS_CONTENT) {
        this.ITEMS_CONTENT = ITEMS_CONTENT;
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

    public int getCOLOR_BACK() {
        return COLOR_BACK;
    }

    public void setCOLOR_BACK(int COLOR_BACK) {
        this.COLOR_BACK = COLOR_BACK;
    }

    public int getCOLOR_MAIN() {
        return COLOR_MAIN;
    }

    public void setCOLOR_MAIN(int COLOR_MAIN) {
        this.COLOR_MAIN = COLOR_MAIN;
    }

    public int getCOLOR_HIGHLIGHT() {
        return COLOR_HIGHLIGHT;
    }

    public void setCOLOR_HIGHLIGHT(int COLOR_HIGHLIGHT) {
        this.COLOR_HIGHLIGHT = COLOR_HIGHLIGHT;
    }

    public int getCOLOR_TITLE() {
        return COLOR_TITLE;
    }

    public void setCOLOR_TITLE(int COLOR_TITLE) {
        this.COLOR_TITLE = COLOR_TITLE;
    }

    public int getCOLOR_CONTENT() {
        return COLOR_CONTENT;
    }

    public void setCOLOR_CONTENT(int COLOR_CONTENT) {
        this.COLOR_CONTENT = COLOR_CONTENT;
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

    public byte getTOP_BUTTON_STYLE_ENDSTATE() {
        return TOP_BUTTON_STYLE_ENDSTATE;
    }

    public void setTOP_BUTTON_STYLE_ENDSTATE(byte TOP_BUTTON_STYLE_ENDSTATE) {
        this.TOP_BUTTON_STYLE_ENDSTATE = TOP_BUTTON_STYLE_ENDSTATE;
    }

    public byte getRIGHT_BUTTON_STYLE() {
        return RIGHT_BUTTON_STYLE;
    }

    public void setRIGHT_BUTTON_STYLE(byte RIGHT_BUTTON_STYLE) {
        this.RIGHT_BUTTON_STYLE = RIGHT_BUTTON_STYLE;
    }

    public byte getBOTTOM_BUTTON_STYLE_ENDSTATE() {
        return BOTTOM_BUTTON_STYLE_ENDSTATE;
    }

    public void setBOTTOM_BUTTON_STYLE_ENDSTATE(byte BOTTOM_BUTTON_STYLE_ENDSTATE) {
        this.BOTTOM_BUTTON_STYLE_ENDSTATE = BOTTOM_BUTTON_STYLE_ENDSTATE;
    }

    public byte getLEFT_BUTTON_STYLE() {
        return LEFT_BUTTON_STYLE;
    }

    public void setLEFT_BUTTON_STYLE(byte LEFT_BUTTON_STYLE) {
        this.LEFT_BUTTON_STYLE = LEFT_BUTTON_STYLE;
    }

    public byte getBACK_THEME() {
        return BACK_THEME;
    }

    public void setBACK_THEME(byte BACK_THEME) {
        this.BACK_THEME = BACK_THEME;
    }

    public byte getFONT_THEME() {
        return FONT_THEME;
    }

    public void setFONT_THEME(byte FONT_THEME) {
        this.FONT_THEME = FONT_THEME;
    }

    public byte getITEM_THEME() {
        return ITEM_THEME;
    }

    public void setITEM_THEME(byte ITEM_THEME) {
        this.ITEM_THEME = ITEM_THEME;
    }

    public int getLEFT_BUTTON_COLOR() {
        return LEFT_BUTTON_COLOR;
    }

    public void setLEFT_BUTTON_COLOR(int LEFT_BUTTON_COLOR) {
        this.LEFT_BUTTON_COLOR = LEFT_BUTTON_COLOR;
    }

    public int getTOP_BUTTON_BACK() {
        return TOP_BUTTON_BACK;
    }

    public void setTOP_BUTTON_BACK(int TOP_BUTTON_BACK) {
        this.TOP_BUTTON_BACK = TOP_BUTTON_BACK;
    }

    public int getRIGHT_BUTTON_BACK() {
        return RIGHT_BUTTON_BACK;
    }

    public void setRIGHT_BUTTON_BACK(int RIGHT_BUTTON_BACK) {
        this.RIGHT_BUTTON_BACK = RIGHT_BUTTON_BACK;
    }

    public int getBOTTOM_BUTTON_BACK() {
        return BOTTOM_BUTTON_BACK;
    }

    public void setBOTTOM_BUTTON_BACK(int BOTTOM_BUTTON_BACK) {
        this.BOTTOM_BUTTON_BACK = BOTTOM_BUTTON_BACK;
    }

    public int getLEFT_BUTTON_BACK() {
        return LEFT_BUTTON_BACK;
    }

    public void setLEFT_BUTTON_BACK(int LEFT_BUTTON_BACK) {
        this.LEFT_BUTTON_BACK = LEFT_BUTTON_BACK;
    }

    public int getTOP_BUTTON_COLOR() {
        return TOP_BUTTON_COLOR;
    }

    public void setTOP_BUTTON_COLOR(int TOP_BUTTON_COLOR) {
        this.TOP_BUTTON_COLOR = TOP_BUTTON_COLOR;
    }

    public int getRIGHT_BUTTON_COLOR() {
        return RIGHT_BUTTON_COLOR;
    }

    public void setRIGHT_BUTTON_COLOR(int RIGHT_BUTTON_COLOR) {
        this.RIGHT_BUTTON_COLOR = RIGHT_BUTTON_COLOR;
    }

    public int getBOTTOM_BUTTON_COLOR() {
        return BOTTOM_BUTTON_COLOR;
    }

    public void setBOTTOM_BUTTON_COLOR(int BOTTOM_BUTTON_COLOR) {
        this.BOTTOM_BUTTON_COLOR = BOTTOM_BUTTON_COLOR;
    }

    public byte getTOP_BUTTON_ICON_ENDSTATE() {
        return TOP_BUTTON_ICON_ENDSTATE;
    }

    public void setTOP_BUTTON_ICON_ENDSTATE(byte TOP_BUTTON_ICON_ENDSTATE) {
        this.TOP_BUTTON_ICON_ENDSTATE = TOP_BUTTON_ICON_ENDSTATE;
    }

    public byte getBOTTOM_BUTTON_ICON_ENDSTATE() {
        return BOTTOM_BUTTON_ICON_ENDSTATE;
    }

    public void setBOTTOM_BUTTON_ICON_ENDSTATE(byte BOTTOM_BUTTON_ICON_ENDSTATE) {
        this.BOTTOM_BUTTON_ICON_ENDSTATE = BOTTOM_BUTTON_ICON_ENDSTATE;
    }

    public String getListTitle_top() {
        return listTitle_top;
    }

    public void setListTitle_top(String listTitle_top) {
        this.listTitle_top = listTitle_top;
    }

    public String getListTitle_bottom() {
        return listTitle_bottom;
    }

    public void setListTitle_bottom(String listTitle_bottom) {
        this.listTitle_bottom = listTitle_bottom;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }
}
