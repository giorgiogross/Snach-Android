package assembtec.com.snach;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * Created by Giorgio on 17.05.15.
 */
public class DeviceProfilesAddFragment extends Fragment {
    private RelativeLayout rl_base;
    private EditText et_addProfileName;
    private ImageButton ib_addProfileConfirm;

    private OnDevicesChanges onDevicesChanges;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rl_base = (RelativeLayout) inflater.inflate(R.layout.fragment_add_device_profile, container, false);
        et_addProfileName = (EditText) rl_base.findViewById(R.id.et_addProfileName);
        ib_addProfileConfirm = (ImageButton) rl_base.findViewById(R.id.ib_addProfileConfirm);
        ib_addProfileConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceProfileItem devItem = new DeviceProfileItem();
                devItem.setName(et_addProfileName.getText().toString());
                onDevicesChanges.addNewDeviceProfile(devItem);
            }
        });

        return rl_base;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onDevicesChanges = (OnDevicesChanges) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
