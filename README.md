# Snach Android
DIY Smartwatch built with Arduino.

This is the Android code for the project demonstrated at https://www.youtube.com/watch?v=0DXkcHel6NA <br>
I am not working actively on this project anymore, but I wanted to share the code with you. I wrote all of this right before I started to study computer sciences, so I was gaining experience and developping my coding skills through the project.

# The Concept
Most of the concept is shown in the video. The watch does hardly any computiation on the arduino but streams all data (button events, sensor data, ...) to the connected Android smartphone. It communicates with the Android Companion App shown in the Snach-Android repository. The Snach should provide the hardware to enable gesture control of your phone and extension of your phone screen. The App should provide software support to reach these goals.  <br>
The vision for this project is that smart watches should not try to resemble but try to extend a smartphone. As such they show dedicated "to the point" information, provide access to a small set of (important) applications right from your wrist and add sensors to your phone.

# The Software
Imagine you have a widget for the Apps you use most. Now imagine these widgets are not on your home screen, but on your wrist watch. Each widget is formatted following a template which is provided by the Snach/Android App. You can select the widgets you want to have on your Snach with the Android App. The App then takes care of showing them on the smartwatch screen when you navigate to the widget. <br>
Navigation is realized like this: Imagine a double linked list with the watch face at the head followed by the widgets. When you tap the button to reach the next app, you will see the next widget in line, when you tap the button to reach the previous App you see the prevous widget in line. Tapping the "home" button brings you to the head of the list (the watch face).

3rd party Apps can use the Android API to configure and offer widgets. Navigating to an App triggers the App to start requesing the layout configuration and contents of the widget from the 3rd party App. If you press a button while the App is shown these button events are also transmitted to the 3rd party Android App. This way it can respond to button clicks and update the screen content as needed.

# Arduino
See my Snach-Arduino Repo for details about the smartwatch and to find its code.

# Licenses
Code and creative contents created by me are signed as such and licensed under the Apache-2.0. See LICENSE.txt for details. Code not created by me is signed as such and includes a copyright notice on the top.
