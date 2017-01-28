package assembtec.com.snach;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 18.04.2015.
 */
public class GestureOverviewFragment extends Fragment {
    private RelativeLayout rl_base;
    private ListView lv_Gestures;
    private ArrayList<GestureItem> gestureData;
    private Button b_addGesture;

    private ItemSelectedListener itemSelectedListener;
    private SharedPreferences sharedGesturesSpecs;

    private boolean hasStandardGesture = true;
    private boolean hasSystemGesture = false;
    private boolean hasShortcut = false;
    private boolean hasAppGesture = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rl_base = (RelativeLayout) inflater.inflate(R.layout.fragment_gestures, container, false);
        lv_Gestures = (ListView) rl_base.findViewById(R.id.lv_gestures);

        initSharedPreferences();
        initGestureList();
        final GestureListAdapter listAdapter = new GestureListAdapter(gestureData);
        lv_Gestures.setAdapter(listAdapter);

        lv_Gestures.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listAdapter.getItemViewType(position) == Globals.GESTURE_ITEM){
                    // show settings screen
                    itemSelectedListener.OnGestureItemSelected(gestureData.get(position).getID());
                }
            }
        });

        b_addGesture = (Button) rl_base.findViewById(R.id.b_addGesture);
        b_addGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemSelectedListener.OnAddNewGesture();
            }
        });

        return rl_base;
    }

    private void initSharedPreferences() {
        sharedGesturesSpecs = getActivity().getSharedPreferences(Globals.MAJOR_KEY_GLOBALGESTURES_SPECS, Context.MODE_MULTI_PROCESS );
    }

    private void initGestureList() {
        gestureData = new ArrayList<GestureItem>();

        int totalGlobalGestures = sharedGesturesSpecs.getInt(Globals.KEY_GESTURES_AMOUNT, 4);
        for(int g = 1; g <= totalGlobalGestures; g++){
            if(g > Globals.AMOUNT_OF_STANDARDGESTURES) {
                SharedPreferences sharedGesture = getActivity().getSharedPreferences(g + Globals.MAJOR_KEY_GLOBALGESTURE, Context.MODE_MULTI_PROCESS);
                int gesture_ID = sharedGesture.getInt(Globals.KEY_GESTURE_ID, -1);
                boolean isEnabled = sharedGesture.getBoolean(Globals.KEY_GESTURE_ENABLED, false);
                Log.i("GestureFragment", "isEnabled " + isEnabled);
                String name = sharedGesture.getString(Globals.KEY_GESTURE_NAME, "Action");
                String action = sharedGesture.getString(Globals.KEY_GESTURE_ACTION, "");

                if (gesture_ID > 0) {
                    GestureItem gi = new GestureItem();
                    setItemPosition(action, gi);
                    gi.setEnabled(isEnabled);
                    gi.setName(name);
                    gi.setID(gesture_ID);
                    gestureData.add(gi);
                }

            } else {
                SharedPreferences sharedGesture = getActivity().getSharedPreferences(g + Globals.MAJOR_KEY_GLOBALGESTURE, Context.MODE_MULTI_PROCESS);
                int gesture_ID = g;
                boolean isEnabled = sharedGesture.getBoolean(Globals.KEY_GESTURE_ENABLED, false);
                String name = "";
                String action = "";

                switch(g){
                    case 1:
                        name = getResources().getString(R.string.gesture_standard_swypeleft);
                        action = SnachExtras.GESTURE_ACTION_SWYPELEFT;
                        break;
                    case 2:
                        name = getResources().getString(R.string.gesture_standard_swyperight);
                        action = SnachExtras.GESTURE_ACTION_SWYPERIGHT;
                        break;
                    case 3:
                        name = getResources().getString(R.string.gesture_standard_confirm);
                        action = SnachExtras.GESTURE_ACTION_CONFIRM;
                        break;
                    case 4:
                        name = getResources().getString(R.string.gesture_standard_dismiss);
                        action = SnachExtras.GESTURE_ACTION_DISMISS;
                        break;
                }

//                if (gesture_ID > 0) {
                    GestureItem gi = new GestureItem();
                    setItemPosition(action, gi);
                    gi.setEnabled(isEnabled);
                    gi.setName(name);
                    gi.setID(gesture_ID);
                    gestureData.add(gi);
//                }
            }
        }

        /*// Standard gestures:
        SharedPreferences sharedSwypeLeftGesture = getActivity().getSharedPreferences(Globals.MAJOR_KEY_SWYPELEFT, Context.MODE_MULTI_PROCESS );
        GestureItem swypeLeft = new GestureItem();
        swypeLeft.setEnabled(sharedSwypeLeftGesture.getBoolean(Globals.KEY_GESTURE_ENABLED, false));
        swypeLeft.setPosition(1.1);
        swypeLeft.setName(getResources().getString(R.string.gesture_standard_swypeleft));
        gestureData.add(swypeLeft);

        SharedPreferences sharedSwypeRightGesture = getActivity().getSharedPreferences(Globals.MAJOR_KEY_SWYPERIGHT, Context.MODE_MULTI_PROCESS );
        GestureItem swypeRight = new GestureItem();
        swypeRight.setEnabled(sharedSwypeRightGesture.getBoolean(Globals.KEY_GESTURE_ENABLED, false));
        swypeRight.setPosition(1.1);
        swypeRight.setName(getResources().getString(R.string.gesture_standard_swyperight));
        gestureData.add(swypeRight);

        SharedPreferences sharedDismissGesture = getActivity().getSharedPreferences(Globals.MAJOR_KEY_DISMISS, Context.MODE_MULTI_PROCESS );
        GestureItem dismiss = new GestureItem();
        dismiss.setEnabled(sharedDismissGesture.getBoolean(Globals.KEY_GESTURE_ENABLED, false));
        dismiss.setPosition(1.1);
        dismiss.setName(getResources().getString(R.string.gesture_standard_dismiss));
        gestureData.add(dismiss);

        SharedPreferences sharedConfirmGesture = getActivity().getSharedPreferences(Globals.MAJOR_KEY_CONFIRM, Context.MODE_MULTI_PROCESS );
        GestureItem confirm = new GestureItem();
        confirm.setEnabled(sharedConfirmGesture.getBoolean(Globals.KEY_GESTURE_ENABLED, false));
        confirm.setPosition(1.1);
        confirm.setName(getResources().getString(R.string.gesture_standard_confirm));
        gestureData.add(confirm);
*/

        // Sub headers:
        GestureItem gi_Standard = new GestureItem();
        gi_Standard.setPosition(1);
        GestureItem gi_System = new GestureItem();
        gi_System.setPosition(2);
        GestureItem gi_Shortcuts = new GestureItem();
        gi_Shortcuts.setPosition(3);
        GestureItem gi_Apps = new GestureItem();
        gi_Apps.setPosition(4);

        if(hasStandardGesture) {
            gestureData.add(gi_Standard);
        }
        if(hasSystemGesture) {
            gestureData.add(gi_System);
        }
        if(hasShortcut) {
            gestureData.add(gi_Shortcuts);
        }
        if(hasAppGesture) {
            gestureData.add(gi_Apps);
        }

        Collections.sort(gestureData, new PosComparator());
    }

    public class PosComparator implements Comparator<GestureItem> {
        @Override
        public int compare(GestureItem pi1, GestureItem pi2) {
            if(pi1.getPosition() < pi2.getPosition()){
                return -1;
            }
            if(pi1.getPosition() > pi2.getPosition()){
                return 1;
            }
            return 0;
        }
    }

    /*public void updateList(){
        gestureData.clear();
        initSharedPreferences();
        initGestureList();
        GestureListAdapter listAdapter = new GestureListAdapter(gestureData);
        lv_Gestures.setAdapter(listAdapter);

    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.itemSelectedListener = (ItemSelectedListener) activity;
    }

    public void setItemPosition(String action, GestureItem gi) {
        if(     action.equals(SnachExtras.GESTURE_ACTION_CONFIRM) ||
                action.equals(SnachExtras.GESTURE_ACTION_DISMISS) ||
                action.equals(SnachExtras.GESTURE_ACTION_SWYPELEFT) ||
                action.equals(SnachExtras.GESTURE_ACTION_SWYPERIGHT)){
            // Standard gesture
            gi.setPosition(1.1);
            hasStandardGesture = true;
        } else if(action.equals(SnachExtras.GESTURE_ACTION_HOMESCREEN) ||
                action.equals(SnachExtras.GESTURE_ACTION_PLAYMUSIC) ||
                action.equals(SnachExtras.GESTURE_ACTION_NEXTSONG) ||
                action.equals(SnachExtras.GESTURE_ACTION_PREVIOUSSONG)) {
            // System gesture
            gi.setPosition(2.1);
            hasSystemGesture = true;
        } else if(action.equals(SnachExtras.GESTURE_ACTION_OPENAPP)) {
            // Shortcut
            gi.setPosition(3.1);
            hasShortcut = true;
        } else {
            // App gesture
            gi.setPosition(4.1);
            hasAppGesture = true;
        }
    }

    private class ViewHolder {
        protected TextView tv_gestureTitle;
        protected Switch sw_enabled;

        protected TextView tv_subheader;
        protected ImageView iv_subheaderIcon;

    }

    private class GestureListAdapter extends BaseAdapter {
        ArrayList<GestureItem> gestureData;

        private GestureListAdapter (ArrayList<GestureItem> gestureData){
            this.gestureData = gestureData;
        }

        @Override
        public int getItemViewType(int position) {
            if(     gestureData.get(position).getPosition() == 1 ||
                    gestureData.get(position).getPosition() == 2 ||
                    gestureData.get(position).getPosition() == 3 ||
                    gestureData.get(position).getPosition() == 4){

                // Subheader
                return Globals.SUBHEADER_ITEM;

            } else {

                // Gesture:
                return Globals.GESTURE_ITEM;

            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return gestureData.size();
        }

        @Override
        public Object getItem(int position) {
            return gestureData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if(convertView == null){
                if(getItemViewType(position) == Globals.SUBHEADER_ITEM){
                    convertView = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.listitem_subheader, parent, false);
                    holder.tv_subheader = (TextView) convertView.findViewById(R.id.tv_subheader);
                    holder.iv_subheaderIcon = (ImageView) convertView.findViewById(R.id.iv_subheaderIcon);
                    convertView.setTag(holder);
                } else {
                    convertView = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.listitem_gesture, parent, false);
                    holder.tv_gestureTitle = (TextView) convertView.findViewById(R.id.tv_gestureName);
                    holder.sw_enabled = (Switch) convertView.findViewById(R.id.sw_enabled);
                    holder.sw_enabled.setFocusable(false);
                    convertView.setTag(holder);
                }
            }

            holder = (ViewHolder) convertView.getTag();
            if(getItemViewType(position) == Globals.SUBHEADER_ITEM){
                if(gestureData.get(position).getPosition() == 1) {
                    holder.tv_subheader.setText(getResources().getString(R.string.gesture_subheader_standard));
                    holder.iv_subheaderIcon.setImageResource(R.drawable.abc_btn_radio_to_on_mtrl_000);
                } else if(gestureData.get(position).getPosition() == 2){
                    holder.tv_subheader.setText(getResources().getString(R.string.gesture_subheader_system));
                    holder.iv_subheaderIcon.setImageResource(R.drawable.abc_btn_radio_to_on_mtrl_000);
                } else if (gestureData.get(position).getPosition() == 3){
                    holder.tv_subheader.setText(getResources().getString(R.string.gesture_subheader_shortcuts));
                    holder.iv_subheaderIcon.setImageResource(R.drawable.abc_btn_radio_to_on_mtrl_000);
                } else if(gestureData.get(position).getPosition() == 4){
                    holder.tv_subheader.setText(getResources().getString(R.string.gesture_subheader_appgestures));
                    holder.iv_subheaderIcon.setImageResource(R.drawable.abc_btn_radio_to_on_mtrl_000);
                }
            } else {
                holder.tv_gestureTitle.setText(gestureData.get(position).getName());
                holder.sw_enabled.setChecked(gestureData.get(position).isEnabled());

                holder.sw_enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        getActivity().getSharedPreferences(gestureData.get(position).getID()+Globals.MAJOR_KEY_GLOBALGESTURE, Context.MODE_MULTI_PROCESS).edit().putBoolean(Globals.KEY_GESTURE_ENABLED, isChecked).apply();
                    }
                });
            }

            return convertView;
        }
    }

    public interface ItemSelectedListener {
        public void OnGestureItemSelected(int ID);
        public void OnAddNewGesture();
    }
}
