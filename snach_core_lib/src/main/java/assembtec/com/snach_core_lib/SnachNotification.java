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
 * Created by Giorgio on 05.06.15.
 */
public class SnachNotification implements Parcelable{
    protected int ID;
    protected String title;
    protected String content;
    protected String AppBCAction;
    protected long postTime;

    public static final Parcelable.Creator<SnachNotification> CREATOR = new
            Parcelable.Creator<SnachNotification>() {
                public SnachNotification createFromParcel(Parcel in) {
                    return new SnachNotification(in);
                }

                public SnachNotification[] newArray(int size) {
                    return new SnachNotification[size];
                }
            };

    public SnachNotification(){

    }

    private SnachNotification(Parcel in){
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ID);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(AppBCAction);
        dest.writeLong(postTime);
    }

    public void readFromParcel(Parcel in) {
        ID = in.readInt();
        title = in.readString();
        content = in.readString();
        AppBCAction = in.readString();
        postTime = in.readLong();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public String getAppBCAction() {
        return AppBCAction;
    }

    public void setAppBCAction(String appBCAction) {
        AppBCAction = appBCAction;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }
}
