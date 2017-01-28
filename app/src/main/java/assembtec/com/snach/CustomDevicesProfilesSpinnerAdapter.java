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

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Giorgio on 11.04.2015.
 */
public class CustomDevicesProfilesSpinnerAdapter extends ArrayAdapter<DeviceProfileItem> {
    private ArrayList<DeviceProfileItem> objects;
    private LayoutInflater inflater;

    public CustomDevicesProfilesSpinnerAdapter(Context context, int resource, ArrayList<DeviceProfileItem> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.objects = objects;
        this.inflater = inflater;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View cv = getCustomView(position, convertView, parent);
        cv.setBackgroundColor(getContext().getResources().getColor(R.color.purewhite));
        return cv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.spinner_style, parent, false);

        TextView tv_main = (TextView) convertView.findViewById(R.id.tv_spinnerItem);
        tv_main.setText(objects.get(position).getName());
        tv_main.setGravity(Gravity.LEFT);

        return convertView;
    }

}
