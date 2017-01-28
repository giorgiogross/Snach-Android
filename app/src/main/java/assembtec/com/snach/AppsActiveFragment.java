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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Giorgio on 16.05.15.
 */
public class AppsActiveFragment extends Fragment {
    private RelativeLayout rl_base;
    private ListView lv_activeApps;
    private AppsAdapter listAdapter;

    private DevicesHandler devicesHandler;
    private ArrayList<SnachAppItem> appList;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl_base = (RelativeLayout) inflater.inflate(R.layout.fragment_apps_active, container, false);
        initActiveAppsList();

        return rl_base;
    }

    private void initActiveAppsList() {
        lv_activeApps = (ListView) rl_base.findViewById(R.id.lv_activeApps);

        devicesHandler = new DevicesHandler(getActivity().getApplicationContext());
        appList = devicesHandler.getActiveApps();

        listAdapter = new AppsAdapter(appList);
        lv_activeApps.setAdapter(listAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void updateActiveAppsList() {
        initActiveAppsList();
    }

    private class ViewHolder {
        protected TextView tv_appName;
        protected ImageButton ib_removeApp;

    }

    private class AppsAdapter extends BaseAdapter {
        ArrayList<SnachAppItem> appList = new ArrayList<>();

        public AppsAdapter(ArrayList<SnachAppItem> appList){
            this.appList = appList;
        }

        @Override
        public int getCount() {
            return appList.size();
        }

        @Override
        public Object getItem(int position) {
            return appList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();

                convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem_active_app, parent, false);
                holder.tv_appName = (TextView) convertView.findViewById(R.id.tv_appName);
                holder.ib_removeApp = (ImageButton) convertView.findViewById(R.id.ib_removeApp);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.tv_appName.setText(appList.get(position).getAppName());
            holder.ib_removeApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    devicesHandler.removeApp(devicesHandler.getCurrentDeviceProfileID(), appList.get(position).getID());
                }
            });

            return convertView;
        }
    }
}
