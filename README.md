# Simple Dialog [![Platform](https://img.shields.io/badge/Platform-Android-green.svg) ]()[![Build Status](https://travis-ci.org/BROUDING/SimpleDialog.svg?branch=master)](https://travis-ci.org/BROUDING/SimpleDialog) [![Download](https://api.bintray.com/packages/brouding/maven/android-simple-dialog/images/download.svg) ](https://bintray.com/brouding/maven/android-simple-dialog/_latestVersion)[![GitHub license](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/brouding/simpledialog/blob/master/LICENSE.txt)

Simple and easy Android Dialog by BROUDING

![Sample Video](https://github.com/BROUDING/SimpleDialog/blob/master/sample/sample_video.gif?raw=true)

---
# Sample .apk
You can download the latest sample APK from this repo here: https://github.com/brouding/SimpleDialog/blob/master/sample/SimpleDialogSample.apk

---
# Gradle Dependency
### Repository
The Gradle dependency is available via [jCenter](https://bintray.com/brouding/maven/android-simple-dialog).
jCenter is the default Maven repository used by Android Studio.

The minimum API level supported by this library is API 14, Android 4.0 (ICE_CREAM_SANDWICH)


### Import to your project
add below code in `build.gradle (Module: app)`
```gradle
dependencies {
	// ... other dependencies here
    compile 'com.brouding:android-simple-dialog:0.3.0.1'
}
```
---
# How to use ( Find more in [`Wiki`](https://github.com/BROUDING/SimpleDialog/wiki) )
### SimpleDialog - Basic
[<b>Click <u>here</u> for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#basic-dialog)
```java
new SimpleDialog.Builder(thisActivity)
          .setTitle("Hello !")
          .setContent("This is basic SimpleDialog :)", 3)

          // Customizing (You can find more in Wiki)

          //.setBtnConfirmText("Check!")
          //.setBtnConfirmTextColor("#de413e")
          //.setBtnCancelText("Cancel")
          //.setBtnCancelTextColor("#de413e")
          //.setCancelable(true)          // Default value is false
          //.onConfirm(new SimpleDialog.BtnCallback() {
          //    @Override
          //    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
          //        // Do something
          //    }
          //})
          //.setBtnCancelText("Cancel", false)
          //.onCancel(new SimpleDialog.BtnCallback() {
          //    @Override
          //    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
          //        // Do something
          //    }
          //})
          .show();    // Must be called at the end
```


### SimpleDialog - Progress
[<b>Click <u>here</u> for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#progress-dialog)
###### [Want to make custom GIF?](https://loading.io/)
(Don't forget to save it in `res/raw`)
```java
new SimpleDialog.Builder(thisActivity)
          .setContent("This is progress SimpleDialog :)", 7)
          // .showProgress must be set true if you want ProgressDialog
          .showProgress(true)     // Default GIF is in the library (R.raw.simple_dialog_progress_default)
          //.setProgressGIF(R.raw.simple_dialog_progress_default)
          .setBtnCancelText("Cancel")
          .setBtnCancelTextColor("#2861b0")

          // Customizing (You can find more in Wiki)

          //.setBtnCancelText("Cancel", false)
          //.setBtnCancelTextColor(R.color.colorPrimary)
          //.setBtnCancelShowTime(2000)
          //.onCancel(new SimpleDialog.BtnCallback() {
          //    @Override
          //    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
          //        // thisActivity.finish();
          //    }
          //})
          //.showProgress(true)
          .show();    // Must be called at the end
```


### SimpleDialog - Guide
[<b>Click <u>here</u> for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#guide-dialog)
```java
new SimpleDialog.Builder(thisActivity)
          .setTitle("Hello !", true)
          .setContent("This is guide SimpleDialog :)\n\n- You can pinch the view !")
          .setGuideImage(R.drawable.image_guide_pinch)
          .setGuideImageSizeDp(150, 150)
          .setPermanentCheck(Pref.PREFERENCE_NAME, Pref.KEY_FIRST_WELCOME)
          .onConfirm(new SimpleDialog.BtnCallbackWithPermanentCheck() {
              @Override
              public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which, boolean isPermanentChecked) {
                  if( isPermanentChecked )
                      setBtnGuideReset(true);
              }
          })
          .setBtnConfirmText("Check!")
          .setBtnConfirmTextColor("#e6b115")
          // I thought cancel button is not necessary, it's unavailable unless there're requests


          // Customizing (You can find more in Wiki)

          //.setTitle("Hello !", true)
          //.setBtnPermanentCheckText("다시 보지 않기", true)
          //.setGuideImagePaddingDp(10)
          //.setGuideImageSizeDp(100, 100)
          .showIfPermanentValueIsFalse();  // Must be called at the end (if permanentCheck is necessary)
```


### SimpleDialog - CustomView
[<b>Click <u>here</u> for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#custom-dialog)
```java
new SimpleDialog.Builder(thisActivity)
          .setTitle("This is Title :)")
          // If the customView is long enough, SimpleDialog will put your layout in the ScrollView automatically
          .setCustomView(R.layout.brouding_simple_dialog_test_layout_custom_long)
          .setBtnConfirmText("Check!")
          .setBtnConfirmTextSizeDp(16)
          .setBtnConfirmTextColor("#1fd1ab")
          .setBtnCancelText("Cancel", false)
          .setBtnCancelTextColor("#555555")
          .show();    // Must be called at the end
```
---
# # Tip
If you use same or similar Dialog often and many different places in the app, which you should, it's easy to use making `Const` String for each dialog
and write Dialog builder in `Application` file like below :

##### In `App.java`
```java
public class App extends Application {
	private static App instance;

	public static App getInstance() {
        return instance;
    }

	public SimpleDialog showDialogWithMessage(final Activity thisActivity, int sign, String text, SimpleDialog.BtnCallback clickListener) {
	    switch (sign) {
		        case Const.GENERAL_WAIT:
                mDialog = new SimpleDialog.Builder(thisActivity)
                        .setContent(text==null ? "Loading..." : text)
                        .showProgress(true)
                        .setBtnCancelText("Cancel")
                        .onCancel(clickListener)
                        .setCancelable(false)
                        .show();
                break;

				case Const.NO_NETWORK:
          			new SimpleDialog.Builder(thisActivity)
                        .setContent("Network connection is unstable")
                        .setBtnConfirmText("Check")
                        .onConfirm(new SimpleDialog.BtnCallback() {
                            @Override
                            public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                                thisActivity.finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
                break;
		}

		return mDialog;
	}
```

##### In `BaseActivity.java` or `BaseFragment.java`
```java
public class BaseActivity extends AppCompatActivity {
	public SimpleDialog showDialogWithMessage(int sign) {
        return App.getInstance().showDialogWithMessage(this, sign, null, null);
    }

	...

	public SimpleDialog showDialogWithMessage(int sign, String text, SimpleDialog.BtnCallback cancelClickListener) {
        return App.getInstance().showDialogWithMessage(this, sign, text, cancelClickListener);
    }
}
```

##### In `MainActivity.java`
```java
public class MainActivity extends BaseActivity {
	showDialogWithMessage(Const.GENERAL_WAIT);
}
```
---
License
-------

    Copyright 2017 SimpleDialog authors.

	- Jeongwon Lee (ssyjk2@gmail.com)

    All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
