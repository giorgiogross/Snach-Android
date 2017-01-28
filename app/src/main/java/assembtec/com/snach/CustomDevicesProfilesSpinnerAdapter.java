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
