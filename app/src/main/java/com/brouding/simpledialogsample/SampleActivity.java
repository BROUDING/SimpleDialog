package com.brouding.simpledialogsample;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brouding.blockbutton.BlockButton;
import com.brouding.simpledialog.SimpleDialog;
import com.brouding.simpledialogsample.Pref.Pref;
import com.transitionseverywhere.TransitionManager;

public class SampleActivity extends AppCompatActivity implements View.OnClickListener {
    private Activity thisActivity;
    private SharedPreferences mPreference;

    private ViewGroup layoutMain;
    private BlockButton btnResetGuidePref;
    private TextView textChangeEnabled;

    private SimpleDialog mCustomDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);

        thisActivity = this;
        mPreference  = getSharedPreferences(Pref.PREFERENCE_NAME, MODE_PRIVATE);

        layoutMain = (ViewGroup) findViewById(R.id.layout_main);

        BlockButton btnShowBasicDialog = (BlockButton) findViewById(R.id.btn_show_basic_dialog);
        btnShowBasicDialog.setOnClickListener(this);

        BlockButton btnShowProgressDialog = (BlockButton) findViewById(R.id.btn_show_progress_dialog);
        btnShowProgressDialog.setOnClickListener(this);

        BlockButton btnShowGuideDialog = (BlockButton) layoutMain.findViewById(R.id.btn_show_guide_dialog);
        btnShowGuideDialog.setOnClickListener(this);

        BlockButton btnShowCustomDialog = (BlockButton) findViewById(R.id.btn_show_custom_dialog);
        btnShowCustomDialog.setOnClickListener(this);

        BlockButton btnShowLongCustomDialog = (BlockButton) findViewById(R.id.btn_show_long_custom_dialog);
        btnShowLongCustomDialog.setOnClickListener(this);

        textChangeEnabled = (TextView) layoutMain.findViewById(R.id.text_btn_change_enabled);

        btnResetGuidePref = (BlockButton) layoutMain.findViewById(R.id.btn_reset_guide);
        btnResetGuidePref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetGuidePref();

                setBtnGuideReset(false);
            }
        });

        setBtnGuideReset(false);
    }

    @Override
    protected void onPause() {
        super.onPause();

        resetGuidePref();
    }

    private void setBtnGuideReset(boolean enabled) {
        TransitionManager.beginDelayedTransition(layoutMain);

        btnResetGuidePref.setEnabled(enabled);
        textChangeEnabled.setText("setEnabled(" +enabled +")");
    }

    private void resetGuidePref() {
        SharedPreferences.Editor edit = mPreference.edit();
        edit.putBoolean(Pref.KEY_FIRST_WELCOME, Pref.FIRST_WELCOME_DEFAULT);
        edit.apply();
    }

    public void goToGithub(View v) {
        String url ="https://github.com/BROUDING/";
        Intent intent;

        switch ( v.getId() ) {
            case R.id.btn_goto_block_button:
                url += "BlockButton";
                break;

            case R.id.btn_goto_simple_dialog:
                url += "SimpleDialog";
                break;
        }

        intent = new Intent( Intent.ACTION_VIEW, Uri.parse(url) );
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch ( view.getId() ) {
            case R.id.btn_show_basic_dialog:
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
                break;

            case R.id.btn_show_progress_dialog:
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
                break;

            case R.id.btn_show_guide_dialog:
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
                break;

            case R.id.btn_show_custom_dialog:
                mCustomDialog = new SimpleDialog.Builder(thisActivity)
                        // If the customView is long enough, SimpleDialog will put your layout in the ScrollView automatically
                        .setCustomView(R.layout.brouding_simple_dialog_test_layout_custom)
                        .setBtnConfirmText("Check!")
                        .setBtnCancelText("Cancel", false)
                        .setBtnCancelTextColor("#777777")


                        // Customizing (You can find more in Wiki)

                        // .setBtnConfirmTextColor("#de413e")
                        // .setTitle("This is Title :)")
                        // .setBtnConfirmTextSizeDp(15)
                        //.onConfirm(new SimpleDialog.BtnCallback() {
                        //    @Override
                        //    public void onClick(@NonNull SimpleDialog dialog, @NonNull SimpleDialog.BtnAction which) {
                        //        TextView mView = (TextView)mCustomDialog.getCustomView().findViewById(R.id.text_blockbutton);
                        //        mView.setText("????");
                        //    }
                        //})
                        .show();
                break;

            case R.id.btn_show_long_custom_dialog:
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
                break;

        }
    }
}
