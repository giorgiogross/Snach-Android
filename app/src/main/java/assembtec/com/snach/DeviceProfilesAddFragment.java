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
