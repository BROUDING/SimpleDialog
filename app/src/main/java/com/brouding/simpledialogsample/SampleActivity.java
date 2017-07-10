package com.brouding.simpledialogsample;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.brouding.simpledialog.SimpleDialog;

public class SampleActivity extends AppCompatActivity {
    private String mPreferenceName = "SimpleDialog_TEST";
    SimpleDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        mDialog = new SimpleDialog.Builder(this)
                .setTitle("Header !", false)
                .setContent("Hello World !\nThis is Simple Dialog :)")
                .setCancelable(true)
                .showProgress(true)
//                .setProgressGIF(R.raw.loading_default)
//                .setBtnCancelShowTime(500)
//                .setPreferenceName(mPreferenceName)
//                .setPermanentCheckKey("Sample01_check")
//                .setGuideImage(R.drawable.image_guide_pinch)
                .setBtnCancelText("Cancel", true)
                .setBtnCancelTextColor(R.color.colorPrimary)
                .onCancel(new SimpleDialog.BtnCallback() {
                    @Override
                    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                        Log.e("@@# onCancel ", "tag = " +dialog.getTag());
                    }
                })
                .setBtnConfirmText("CONFIRM", true)
                .setTag("SAMPLE 01")
                .show();
    }
}
