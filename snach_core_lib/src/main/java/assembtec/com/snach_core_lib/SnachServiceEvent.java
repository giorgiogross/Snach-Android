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

import android.content.ServiceConnection;

/**
 * Created by Giorgio on 18.05.15.
 */
public interface SnachServiceEvent {
    void onServiceDisconnected();
    void onSensorDataReceived(int xAccel, int yAccel, int zAccel, int xGyro, int yGyro, int zGyro);
    void onSnachConnectionResult(boolean isConnected);
    void onSnachButtonPressed(int button_id);
}
