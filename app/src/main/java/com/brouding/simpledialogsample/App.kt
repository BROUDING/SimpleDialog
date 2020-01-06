package com.brouding.simpledialogsample

import android.app.Activity
import android.app.Application
import com.brouding.simpledialog.BtnActionCallback
import com.brouding.simpledialog.SimpleDialog
import com.brouding.simpledialog.builder.General
import com.brouding.simpledialog.builder.Loading

class App : Application() {
    private var mDialog: SimpleDialog? = null

    fun showDialogWithMessage(
        thisActivity: Activity,
        sign: Int,
        text: String?,
        clickListener: BtnActionCallback?
    ): SimpleDialog? {
//        when (sign) {
//            Const.GENERAL_WAIT -> mDialog = SimpleDialog(
//                Loading(thisActivity)
//                    .applyGeneral {
//                        setContent(text ?: "Loading...")
//                        setBtnCancelText("Cancel")
//                    }
//            ).showAndReturn()
//
//            Const.NO_NETWORK -> SimpleDialog(
//                General(thisActivity)
//                    .setContent("Network connection is unstable")
//                    .setBtnConfirmText("Check")
//                    .onBtnAction(clickListener)
//            ).show()
//        }
        return mDialog
    }
}