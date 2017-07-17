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
    compile 'com.brouding:android-simple-dialog:0.2.5.1'
}
```

# How to use (Wiki will be updated !)
### SimpleDialog - Basic
```java
new SimpleDialog.Builder(thisActivity)
                .setContent("This is basic SimpleDialog :)")
                .setBtnConfirmText("Check!")
                .setBtnConfirmTextColor("#de413e")

                // Customizing (You can find more in Wiki)

                //.setTitle("Hello !", true)	// setTitle(String message, boolean isBold)
                //.setCancelable(true)          // Default value is false
								//.setBtnConfirmTextSizeDp(15)
								//.setBtnConfirmTextColor("#de413e")
                //.onConfirm(new SimpleDialog.BtnCallback() {
                //    @Override
                //    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                //        // Do something
                //    }
                //})
                //.setBtnCancelText("Cancel", false)	// setBtnCancelText(String message, boolean isBold)
                //.onCancel(new SimpleDialog.BtnCallback() {
                //    @Override
                //    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                //        // Do something
                //    }
                //})
                .show();  // Must be called at the end
```

### SimpleDialog - Progress
```java
new SimpleDialog.Builder(thisActivity)
		// I thought ProgressDialog doesn't need setTitle, it's unavailable unless there're requests
                .setContent("This is progress SimpleDialog :)")
                .setProgressGIF(R.raw.simple_dialog_progress_default)	// If you use this, setProgress(true) is not necessary
                .setBtnCancelText("Cancel")
                .setBtnCancelTextColor("#2861b0")

                // Customizing (You can find more in Wiki)

                //.setBtnCancelText("Cancel", false)	// setBtnCancelText(String message, boolean isBold)
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
```java
new SimpleDialog.Builder(thisActivity)
                .setTitle("Hello !")      // Default text will be set Bold, Not necessary
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

                //.setTitle("Hello !", false)
                //.setBtnPermanentCheckText("Don't show again", true)
                //.setGuideImagePaddingDp(10)
                //.setGuideImageSizeDp(100, 100)
								//.onConfirm(new SimpleDialog.BtnCallback() {
                //    @Override
                //    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                //        // Do something
                //    }
                //})
		// If permanentCheck is unnecessary, you can use >> .show();
                .showIfPermanentValueIsFalse();  // Must be called at the end (if permanentCheck is necessary)
```

### SimpleDialog - CustomView
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

                // Customizing (You can find more in Wiki)

								//.setPermanentCheck(Pref.PREFERENCE_NAME, Pref.KEY_PERMANENT_GUIDE_MAIN)
                //.setBtnPermanentCheckText("Don't show again", true)
								//.onConfirm(new SimpleDialog.BtnCallback() {
                //    @Override
                //    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                //        // Do something
                //    }
                //})
								// If permanentCheck is unnecessary, you can use >> .show();
                .showIfPermanentValueIsFalse();  // Must be called at the end (if permanentCheck is necessary)
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
