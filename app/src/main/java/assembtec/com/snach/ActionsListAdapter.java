package assembtec.com.snach;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import assembtec.com.snach_core_lib.SnachExtras;

/**
 * Created by Giorgio on 29.04.2015.
 */
public class ActionsListAdapter extends BaseAdapter {

    ArrayList<GestureActionItem> actionsList;
    Activity activity;
    ListView listView;

    public ActionsListAdapter (ListView listView, Activity activity){
        this.activity = activity;
        this.listView = listView;
        setUpActionList();
    }

    private void setUpActionList() {
        actionsList = new ArrayList<GestureActionItem>();

        // Standard Gestures:
        GestureActionItem standardGestureSL = new GestureActionItem();
        standardGestureSL.setName(activity.getResources().getString(R.string.gesture_standard_swypeleft));
        standardGestureSL.setAction(SnachExtras.GESTURE_ACTION_SWYPELEFT);
        actionsList.add(standardGestureSL);

        GestureActionItem standardGestureSR = new GestureActionItem();
        standardGestureSR.setName(activity.getResources().getString(R.string.gesture_standard_swyperight));
        standardGestureSR.setAction(SnachExtras.GESTURE_ACTION_SWYPERIGHT);
        actionsList.add(standardGestureSR);

        GestureActionItem standardGestureCONF = new GestureActionItem();
        standardGestureCONF.setName(activity.getResources().getString(R.string.gesture_standard_confirm));
        standardGestureCONF.setAction(SnachExtras.GESTURE_ACTION_CONFIRM);
        actionsList.add(standardGestureCONF);

        GestureActionItem standardGestureDISM = new GestureActionItem();
        standardGestureDISM.setName(activity.getResources().getString(R.string.gesture_standard_dismiss));
        standardGestureDISM.setAction(SnachExtras.GESTURE_ACTION_DISMISS);
        actionsList.add(standardGestureDISM);

        // System Gestures:
        GestureActionItem gai1 = new GestureActionItem();
        gai1.setName(activity.getResources().getString(R.string.gesture_action_homescreen));
        gai1.setAction(SnachExtras.GESTURE_ACTION_HOMESCREEN);
        actionsList.add(gai1);

        GestureActionItem gai2 = new GestureActionItem();
        gai2.setName(activity.getResources().getString(R.string.gesture_action_playmusic));
        gai2.setAction(SnachExtras.GESTURE_ACTION_PLAYMUSIC);
        actionsList.add(gai2);

        // TODO add all missing actions and group them
        listView.setAdapter(this);
    }

    @Override
    public int getCount() {
        return actionsList.size();
    }

    @Override
    public Object getItem(int position) {
        return actionsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if(convertView == null){
            convertView = activity.getLayoutInflater().inflate(R.layout.listitem_actions, parent, false);
            holder.tv_actionName = (TextView) convertView.findViewById(R.id.tv_actionName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_actionName.setText(actionsList.get(position).getName());

        return convertView;
    }

    public String getAction(int position) {
        return actionsList.get(position).getAction();
    }

    public String getActionName(String gestureAction) {
        String name = "Action";

        for(int i = 0; i< actionsList.size(); i++){
            if(gestureAction.equals(actionsList.get(i).getAction())){
                name = actionsList.get(i).getName();
                break;
            }
        }

        return name;
    }

    public String getActionName(int position) {
        return actionsList.get(position).getName();
    }

    private class ViewHolder {
        protected TextView tv_actionName;
    }
}
