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
 * Created by Giorgio on 23.04.2015.
 */
public class ComparisonItem {
    private int gestureIndex;
    private int charIndex;
    private int maxCharIndex;

    public int getGestureIndex() {
        return gestureIndex;
    }

    public void setGestureIndex(int gestureIndex) {
        this.gestureIndex = gestureIndex;
    }

    public int getCharIndex() {
        return charIndex;
    }

    public void setCharIndex(int carIndex) {
        this.charIndex = carIndex;
    }

    public int getMaxCharIndex() {
        return maxCharIndex;
    }

    public void setMaxCharIndex(int maxCarIndex) {
        this.maxCharIndex = maxCarIndex;
    }
}
