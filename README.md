# Simple Dialog [![Platform](https://img.shields.io/badge/Platform-Android-green.svg) ]()[![Build Status](https://travis-ci.org/BROUDING/SimpleDialog.svg?branch=master)](https://travis-ci.org/BROUDING/SimpleDialog) [![Download](https://api.bintray.com/packages/brouding/maven/android-simple-dialog/images/download.svg) ](https://bintray.com/brouding/maven/android-simple-dialog/_latestVersion)[![GitHub license](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/brouding/simpledialog/blob/master/LICENSE.txt)

Simple and easy Builder pattern Android Dialog by BROUDING

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
    compile 'com.brouding:android-simple-dialog:1.0.1'
}
```
---
# How to use ( Find more in [`Wiki`](https://github.com/BROUDING/SimpleDialog/wiki) )
### SimpleDialog - General
[<b>Click <u>here</u> for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#basic-dialog)
```java
SimpleDialog( General(thisContext)
	.setTitle("Hello !")
	.setContent("This is basic SimpleDialog :)", 3)

	// Customizing (You can find more in Wiki)
	//.setBtnConfirmText("Check!")
	//.setBtnConfirmTextColor("#de413e")
	//.setBtnCancelText("Cancel")
	//.setBtnCancelTextColor("#de413e")
	//.setCancelable(true)          // Default value is false
	).show()    // Must be called at the end
```


### SimpleDialog - Loading
[<b>Click <u>here</u> for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#loading-dialog)
###### [Want to make some custom GIF?](https://loading.io/)
(Don't forget to keep it in `res/raw`)
```java
// Default GIF is in the library (R.raw.simple_dialog_progress_default)
SimpleDialog( Loading(thisContext)
	.applyGeneral {
		setContent("This is progress SimpleDialog :)", 7)
		setBtnCancelText("Cancel")
		setBtnCancelTextColor("#2861b0")
		onBtnAction {
			Log.e("@@# which one? = ", "${it.name}")
		}
	}
// Customizing (You can find more in Wiki)
//.setProgressGIF(R.raw.simple_dialog_progress_default)
//.setBtnCancelShowTime(2000)
).show()    // Must be called at the end
```


### SimpleDialog - Selection
[<b>Click <u>here</u> for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#selection-dialog)
```java
SimpleDialog( Selection(this)
	.applyGeneral {
		setTitle("HELLO !!!")
		setCancelable(true)
	}
	.setContentList( listOf("AAAA", "BBB", "CC"), paddingLeftDp=7 )	// simple list of String, default paddingLeft is 14
	.onSelect { selection ->
		Log.e("@@# option = ", "$selection")
	}
).show()    // Must be called at the end
```


### SimpleDialog - Guide
[<b>Click <u>here</u> for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#guide-dialog)
```java
SimpleDialog( Custom(thisContext)
	.applyGeneral {
		setTitle("Hello !", true)
		setContent("This is guide SimpleDialog :)\n\n- You can pinch the view !")
		setBtnConfirmText("Check!")
		setBtnConfirmTextColor("#e6b115")
		onBtnAction {
			if (it == BtnAction.CONFIRM_WITH_PERMANENT_CHECK)
				setBtnGuideReset(true)
		}

		// I thought cancel button is not necessary, it's unavailable unless there're requests
	}
	.setGuideImage(R.drawable.image_guide_pinch)
	.setGuideImageSizeDp(150, 150)
	.setPermanentCheck(Pref.PREFERENCE_NAME, Pref.KEY_FIRST_WELCOME)
	// Customizing (You can find more in Wiki)

	//.setTitle("Hello !", true)
	//.setBtnPermanentCheckText("다시 보지 않기", true)
	//.setGuideImagePaddingDp(10)
	//.setGuideImageSizeDp(100, 100)
).showIfNotChecked()  // Must be called at the end (if permanentCheck is necessary)
```


### SimpleDialog - Custom
[<b>Click <u>here</u> for more options</b>](https://github.com/BROUDING/SimpleDialog/wiki/How-to-use#custom-dialog)
```java
SimpleDialog( Custom(thisContext)
	.applyGeneral {
		setTitle("This is Title :)")
		setBtnConfirmText("Check!")
		setBtnConfirmTextSizeDp(16)
		setBtnConfirmTextColor("#1fd1ab")
		setBtnCancelText("Cancel", false)
		setBtnCancelTextColor("#555555")
	}
	// If the customView is long enough, SimpleDialog will put your layout in the ScrollView automatically
	.setCustomView(R.layout.brouding_simple_dialog_test_layout_custom_long)
).show()    // Must be called at the end
```
---
# # Tip
If you use same or similar Dialog often and many different places in the app, which you should, it's easy to use making `Const` String for each dialog
and write Dialog builder in `Application` file like below or make a wrapper function using Extensions in Kotlin :

##### In `App.kt`
```java
class App extends Application {
	private var mDialog: SimpleDialog? = null
	    fun showDialogWithMessage(
	        thisActivity: Activity,
	        sign: Int,
	        text: String?,
	        clickListener: BtnActionCallback?
	    ): SimpleDialog? {
	        when (sign) {
	            Const.GENERAL_WAIT -> mDialog = SimpleDialog(
	                Loading(thisActivity)
	                    .applyGeneral {
	                        setContent(text ?: "Loading...")
	                        setBtnCancelText("Cancel")
	                    }
	            ).showAndReturn()

	            Const.NO_NETWORK -> SimpleDialog(
	                General(thisActivity)
	                    .setContent("Network connection is unstable")
	                    .setBtnConfirmText("Check")
	                    .onBtnAction(clickListener)
	            ).show()
	        }
	        return mDialog
	    }
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

    Copyright 2020 SimpleDialog authors.

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
