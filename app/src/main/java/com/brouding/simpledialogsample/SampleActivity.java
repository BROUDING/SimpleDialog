package com.brouding.simpledialogsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.brouding.simpledialog.SimpleDialog;

public class SampleActivity extends AppCompatActivity {
    private String mPreferenceName = "SimpleDialog_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        new SimpleDialog.Builder(this)
                .title("Header !")
                .content("Hello World !\nThis is Simple Dialog :)")
                .cancelable(false)
//                .showProgress(true)
//                .preferenceName(mPreferenceName)
//                .permanentCheckKey("Sample01_check")
//                .guideImage(R.drawable.image_guide_pinch)
                .btnCancelText("Cancel")
                .btnConfirmText("Confirm")
                .show();
    }
}
