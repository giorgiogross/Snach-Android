package assembtec.com.snach;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 16.05.15.
 */
public class AppsSupportedFragment extends Fragment {
    // UI:
    private RelativeLayout rl_base;
    private ProgressBar pb_querySupportedApps;
    private ListView lv_supportedApps;
    private TextView tv_noAppsFound;
    private AppsAdapter listAdapter;

    ArrayList<SnachAppItem> appList;

    // App query:
    private Handler queryHandler;
    private Runnable queryRunnable;
    private boolean isQueryingApps = false;

    // Device:
//    private DevicesHandler devicesHandler;
    private OnAppAddedListener onAppEventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onAppEventListener = (OnAppAddedListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl_base = (RelativeLayout) inflater.inflate(R.layout.fragment_apps_supported, container, false);
        pb_querySupportedApps = (ProgressBar) rl_base.findViewById(R.id.pb_querySupportedApps);
        tv_noAppsFound = (TextView) rl_base.findViewById(R.id.tv_noAppsFound);

        initAppsList();
        return rl_base;
    }

    private void initAppsList() {
        lv_supportedApps = (ListView) rl_base.findViewById(R.id.lv_supportedApps);
//        devicesHandler = new DevicesHandler(getActivity().getApplicationContext());
        lv_supportedApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*devicesHandler.addNewApp(appList.get(position).getAppName(),
                        appList.get(position).getAppPackage(),
                        appList.get(position).getAppBCAction(),
                        appList.get(position).getAppBCExtra());*/
                onAppEventListener.addActiveApp(appList.get(position));
                onAppEventListener.updateActiveAppList();
                /*Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage(appList.get(position).getAppPackage());
                launchIntent.putExtra(SnachExtras.APP_SPECIAL_EXTRA, appList.get(position).getIntentExtra());
                startActivity(launchIntent);*/
            }
        });

        appList = new ArrayList<>();

        IntentFilter filter = new IntentFilter();
        filter.addAction(SnachExtras.INTENT_ACTION_SUPPORTED_APPS_REPLY);
        getActivity().registerReceiver(SupportedAppsReceiver, filter);

        pb_querySupportedApps.setVisibility(View.VISIBLE);
        lv_supportedApps.setVisibility(View.INVISIBLE);

        listAdapter = new AppsAdapter(appList);
        lv_supportedApps.setAdapter(listAdapter);

        Intent supportedAppsRequest = new Intent();
        supportedAppsRequest.setAction(SnachExtras.INTENT_ACTION_SUPPORTED_APPS_QUERY);
        getActivity().sendBroadcast(supportedAppsRequest);

        queryHandler = new Handler();
        queryRunnable = new Runnable() {
            @Override
            public void run() {
                isQueryingApps = false;
                getActivity().unregisterReceiver(SupportedAppsReceiver);
                pb_querySupportedApps.setVisibility(View.INVISIBLE);

                if(appList.size() == 0){
                    tv_noAppsFound.setVisibility(View.VISIBLE);
                    lv_supportedApps.setVisibility(View.INVISIBLE);
                }
            }
        };
        queryHandler.postDelayed(queryRunnable, Globals.QUERY_SUPPRTED_APPS_TIME);
        isQueryingApps = true;
    }

    private BroadcastReceiver SupportedAppsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Todo: check if combination of apppackage and appname already were added
            SnachAppItem sap = new SnachAppItem();
            sap.setAppPackage(intent.getStringExtra(SnachExtras.INTENT_EXTRA_APP_PACKAGE));
            sap.setAppName(intent.getStringExtra(SnachExtras.INTENT_EXTRA_APP_NAME));
            sap.setAppDescription(intent.getStringExtra(SnachExtras.INTENT_EXTRA_APP_DESCRIPTION));
            sap.setIntentExtra((intent.getStringExtra(SnachExtras.INTENT_EXTRA_APP_INTENT_EXTRA)));
            sap.setAppBCAction(intent.getStringExtra(SnachExtras.INTENT_EXTRA_APP_BC_ACTION));
            sap.setAppBCExtra(intent.getStringExtra(SnachExtras.INTENT_EXTRA_APP_BC_EXTRA));

            appList.add(sap);
            listAdapter.updateList(appList);
            listAdapter.notifyDataSetChanged();
            lv_supportedApps.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        if(isQueryingApps) {
            queryHandler.removeCallbacks(queryRunnable);
            getActivity().unregisterReceiver(SupportedAppsReceiver);
        }
        super.onDestroy();
    }

    private class ViewHolder {
        protected TextView tv_appName;
        protected TextView tv_appDescription;
    }

    private class AppsAdapter extends BaseAdapter{
        ArrayList<SnachAppItem> appList = new ArrayList<>();
        public AppsAdapter(ArrayList<SnachAppItem> appList){
            this.appList = appList;
        }

        public void updateList(ArrayList<SnachAppItem> appList){
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();

                convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem_supported_app, parent, false);
                holder.tv_appName = (TextView) convertView.findViewById(R.id.tv_appName);
                holder.tv_appDescription = (TextView) convertView.findViewById(R.id.tv_appDescription);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.tv_appName.setText(appList.get(position).getAppName());
            holder.tv_appDescription.setText(appList.get(position).getAppDescription());

            return convertView;
        }
    }


    public interface OnAppAddedListener {
        public void updateActiveAppList();

        void addActiveApp(SnachAppItem snachAppItem);
    }
}
