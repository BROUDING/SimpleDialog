# Simple Dialog [![Platform](https://img.shields.io/badge/Platform-Android-green.svg) ]()[![Build Status](https://travis-ci.org/BROUDING/SimpleDialog.svg?branch=master)](https://travis-ci.org/BROUDING/SimpleDialog) [![Download](https://api.bintray.com/packages/brouding/maven/android-simple-dialog/images/download.svg) ](https://bintray.com/brouding/maven/android-simple-dialog/_latestVersion)[![GitHub license](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/brouding/simpledialog/blob/master/LICENSE.txt)

Simple and easy Android Dialog by BROUDING

![Sample Video](https://github.com/BROUDING/SimpleDialog/blob/master/sample/sample_video.gif?raw=true)

# Sample .apk
You can download the latest sample APK from this repo here: https://github.com/brouding/SimpleDialog/blob/master/sample/SimpleDialogSample.apk


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
    compile 'com.brouding:android-simple-dialog:0.3.0'
}
```

# How to use ( Find more in [`Wiki`](https://github.com/BROUDING/SimpleDialog/wiki) )
### SimpleDialog - Basic
[<b>Click here for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#basic-dialog)
```java
new SimpleDialog.Builder(thisActivity)new SimpleDialog.Builder(thisActivity)
                        .setContent("This is basic SimpleDialog :)", 3)
                        //.setBtnConfirmText("Check!")
                        .setBtnConfirmTextColor("#de413e")
                        //.setBtnCancelText("Cancel")
                        .setBtnCancelTextColor("#de413e")

                        // Customizing (You can find more in Wiki)

                        .setTitle("Hello !")
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
                        .show();  // Must be called at the end
```

### SimpleDialog - Progress
[<b>Click here for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#progress-dialog)
```java
new SimpleDialog.Builder(thisActivity)
                        .setContent("This is progress SimpleDialog :)", 7)
                        .setProgressGIF(R.raw.simple_dialog_progress_default)
                        //.setCustomView(R.layout.brouding_simple_dialog_test_layout_custom)
                        //.setBtnConfirmText("Check!")
                        //.setBtnConfirmTextSizeDp(15)
                        //.setBtnConfirmTextColor("#de413e")
                        //.setBtnCancelText("Cancel")
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
                        .show();  // Must be called at the end
```

### SimpleDialog - Guide
[<b>Click here for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#guide-dialog)
```java
new SimpleDialog.Builder(thisActivity)
                        .setTitle("Hello !", true)      // Not necessary
                        .setContent("This is guide SimpleDialog :)\n\n- You can pinch the view !")
                        .setGuideImage(R.drawable.image_guide_pinch)    // Not necessary
                        .setGuideImageSizeDp(150, 150)
                        .setPermanentCheck(Pref.PREFERENCE_NAME, Pref.KEY_FIRST_WELCOME)
                        .onConfirm(new SimpleDialog.BtnCallbackWithPermanentCheck() {
                            @Override
                            public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which, boolean isPermanentChecked) {
                                if( isPermanentChecked )
                                    setBtnGuideReset(true);
                            }
                        })
                        // I thought cancel button is not necessary, it's unavailable unless there're requests
                        .setBtnConfirmText("Check!")
                        .setBtnConfirmTextColor("#e6b115")


                        // Customizing (You can find more in Wiki)

                        //.setTitle("Hello !", true)
                        //.setBtnPermanentCheckText("다시 보지 않기", true)
                        //.setGuideImagePaddingDp(10)
                        //.setGuideImageSizeDp(100, 100)
                        .showIfPermanentValueIsFalse();  // Must be called at the end (if permanentCheck is necessary)
```

### SimpleDialog - CustomView
[<b>Click here for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#custom-dialog)
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
                        .show();
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
