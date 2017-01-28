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

/**
 Copyright (C) 2015, 2015 Giorgio Groß - All rights reserved.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT.
 BY NO MEANS SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY DAMAGES,
 CLAIM OR OTHER LIABILITY, WHETHER IN AN ACTION OF TORT, CONTRACT
 OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DrawerItemClickListener implements ListView.OnItemClickListener {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private OnDrawerItemSelectedListener selectionListener;

    public DrawerItemClickListener(DrawerHandler mdHandler, ListView mDrawerList, DrawerLayout mDrawerLayout, Activity activity){
        this.mDrawerList = mDrawerList;
        this.mDrawerLayout = mDrawerLayout;
        selectionListener = (OnDrawerItemSelectedListener) mdHandler;
    }

    @Override
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        selectItem(position);
    }

    private void selectItem(int position) {
        if(mDrawerList.getAdapter().getItemViewType(position) == 1){
            selectionListener.onStartDevicesActivity();
        } else if(mDrawerList.getAdapter().getItemViewType(position) == 2){
            selectionListener.onStartBluetoothActivity();
        } else if(mDrawerList.getAdapter().getItemViewType(position) == 3){
            selectionListener.onStartGesturesActivity();
        } else if(mDrawerList.getAdapter().getItemViewType(position) == 4){
            selectionListener.onStartAppsActivity();
        }
    }

    public interface OnDrawerItemSelectedListener {
        public void onDrawerItemSelected(int position);
        public void onStartBluetoothActivity();
        public void onStartGesturesActivity();
        public void onStartAppsActivity();
        public void onStartDevicesActivity();
    }


}

