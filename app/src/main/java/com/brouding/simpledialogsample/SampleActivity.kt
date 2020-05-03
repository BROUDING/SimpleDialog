package com.brouding.simpledialogsample

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.TransitionManager
import com.brouding.simpledialog.SimpleDialog
import com.brouding.simpledialog.builder.General
import com.brouding.simpledialog.builder.Custom
import com.brouding.simpledialog.builder.Loading
import com.brouding.simpledialog.builder.Selection
import com.brouding.simpledialog.extra.BtnAction
import com.brouding.simpledialog.extra.DialogAction
import com.brouding.simpledialogsample.extra.Pref
import kotlinx.android.synthetic.main.activity_sample.*

class SampleActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var layoutMain: ViewGroup
    private var mCustomDialog: SimpleDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        layoutMain = findViewById<View>(R.id.layout_main) as ViewGroup

        btnShowBasicDialog.setOnClickListener(this)
        btnShowProgressDialog.setOnClickListener(this)
        btnShowGuideDialog.setOnClickListener(this)
        btnShowListDialog.setOnClickListener(this)
        btnShowCustomDialog.setOnClickListener(this)
        btnShowLongCustomDialog.setOnClickListener(this)

        btnResetGuidePref.setOnClickListener {
            resetGuidePref()
            setBtnGuideReset(false)
        }
        setBtnGuideReset(false)
    }

    override fun onPause() {
        super.onPause()
        resetGuidePref()
    }

    private fun setBtnGuideReset(enabled: Boolean) {
        TransitionManager.beginDelayedTransition(layoutMain)
        btnResetGuidePref.isEnabled = enabled
        textChangeEnabled.text = "setEnabled($enabled)"
    }

    private fun resetGuidePref() {
        val mPreference = getSharedPreferences(Pref.PREFERENCE_NAME, Context.MODE_PRIVATE)
        val edit = mPreference.edit()
        edit.putBoolean(Pref.KEY_FIRST_WELCOME, Pref.FIRST_WELCOME_DEFAULT)
        edit.apply()
    }

    fun goToGithub(v: View) {
        var url = "https://github.com/BROUDING/"
        val intent: Intent
        when (v.id) {
            R.id.btnGotoBlockButton  -> url += "BlockButton"
            R.id.btnGotoSimpleDialog -> url += "SimpleDialog"
        }
        intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnShowBasicDialog -> SimpleDialog( General(this).apply {
                setBackgroundResource(R.drawable.brouding_simple_dialog_bg_round_test)
                setTitle("Hello !")
                setTitleTextColor(R.color.colorWhite)
                setContent("This is basic SimpleDialog :)", 3) // Customizing (You can find more in Wiki)
                setContentTextColor(R.color.colorWhite)
                setCancelable(true)
                setBtnCancelText("Cancel")
                setBtnConfirmTextColor(R.color.colorWhite)
                setBtnCancelTextColor(R.color.colorWhite)
                onBtnAction {
                    when(it) {
                            BtnAction.CONFIRM -> Log.e("@@# which one? = ", "CONFIRM")
                            BtnAction.CANCEL  -> Log.e("@@# which one? = ", "CANCEL")
                            else -> {}
                        }
                }
                onDialogAction {
                    when(it) {
                        DialogAction.SHOW    -> Log.e("@@# what happened? = ", "SHOWN")
                        DialogAction.DISMISS -> Log.e("@@# what happened? = ", "DISMISSED")
                        DialogAction.CANCEL  -> Log.e("@@# what happened? = ", "CANCELED")
                    }
                }
            }
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
                ).show() // Must be called at the end
            R.id.btnShowProgressDialog -> SimpleDialog( Loading(this)
                .applyGeneral {
                    setContent("This is progress SimpleDialog :)", 7)
                    setBtnCancelText("Cancel")
                    setBtnCancelTextColor("#2861b0") // Customizing (You can find more in Wiki)
                    onBtnAction {
                        Log.e("@@# which one? = ", "" +it.name)
                    }
                }
                .setBtnCancelShowTime(2000)
                // Default GIF is in the library (R.raw.simple_dialog_progress_default)
                //.setProgressGIF(R.raw.simple_dialog_progress_default)
                //.setBtnCancelText("Cancel", false)
                //.setBtnCancelTextColor(R.color.colorPrimary)
                //.setBtnCancelShowTime(2000)
                ).show() // Must be called at the end
            R.id.btnShowListDialog ->
                SimpleDialog( Selection(this)
                    .applyGeneral {
                        setTitle("HELLO !!!")
                        setCancelable(true)
                    }
                    .setContentList( listOf("AAAA", "BBB", "CC") )
                    .onSelect {
                        Log.e("@@# option = ", "" +it)
                    }
                ).show()

            R.id.btnShowGuideDialog ->
                SimpleDialog( Custom(this)
                    .applyGeneral {
                        setTitle("Hello !", true)
                        setContent("This is guide SimpleDialog :)\n\n- You can pinch the view !")
                        setBtnConfirmText("Check!")
                        setBtnConfirmTextColor("#e6b115") // I thought cancel button is not necessary, it's unavailable unless there're requests
                    }
                    .onBtnAction {
                        Log.e("@@# which one? = ", "" +it.name)
                        if (it == BtnAction.CONFIRM_WITH_PERMANENT_CHECK)
                            setBtnGuideReset(true)
                    }
                    .setGuideImage(R.drawable.image_guide_pinch)
                    .setGuideImageSizeDp(150, 150)
                    .setPermanentCheck(Pref.PREFERENCE_NAME, Pref.KEY_FIRST_WELCOME)
                    //.setBtnPermanentCheckText("다시 보지 않기", true)
                    // Customizing (You can find more in Wiki)
                    //.setTitle("Hello !", true)
                    //.setGuideImagePaddingDp(10)
                ).showIfNotChecked() // Must be called at the end (if permanentCheck is necessary)
            R.id.btnShowCustomDialog ->
                mCustomDialog =
                    SimpleDialog( Custom(this) // If the customView is long enough, SimpleDialog will put your layout in the ScrollView automatically
                        .setCustomView(R.layout.brouding_simple_dialog_test_layout_custom)
                        .applyGeneral {
                            setBtnConfirmText("Check!")
                            setBtnCancelText("Cancel", false)
                            setBtnCancelTextColor("#777777") // Customizing (You can find more in Wiki)
                        }
                        // .setBtnConfirmTextColor("#de413e")
                        // .setTitle("This is Title :)")
                        // .setBtnConfirmTextSizeDp(15)
                    ).showAndReturn()

            R.id.btnShowLongCustomDialog ->
                SimpleDialog( Custom(this)
                    .applyGeneral {
                        setTitle("This is Title :)") // If the customView is long enough, SimpleDialog will put your layout in the ScrollView automatically
                        setBtnConfirmText("Check!")
                        setBtnConfirmTextSizeDp(16)
                        setBtnConfirmTextColor("#1fd1ab")
                        setBtnCancelText("Cancel", false)
                        setBtnCancelTextColor("#555555")
                    }
//                    .setPermanentCheck(Pref.PREFERENCE_NAME, Pref.KEY_FIRST_WELCOME)
//                    .setBtnPermanentCheckText("Don't show again")
                    .setCustomView(R.layout.brouding_simple_dialog_test_layout_custom_long)
            ).show()
        }
    }
}