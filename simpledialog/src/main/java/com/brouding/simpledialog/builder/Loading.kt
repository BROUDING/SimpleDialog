package com.brouding.simpledialog.builder

import android.content.Context
import androidx.annotation.IntRange
import androidx.annotation.RawRes
import com.brouding.simpledialog.BtnActionCallback
import com.brouding.simpledialog.extra.Type

class Loading(override val context: Context): General(context) {
    var btnCancelShowTime = 2500
    var customProgressGIF: Int? = null

    init {
        type = Type.LOADING
    }

    fun applyGeneral(func: General.() -> Unit): Loading {
        this.func()
        return this
    }

    override fun onBtnAction(callbackBtn: BtnActionCallback?): Loading {
        return super.onBtnAction(callbackBtn) as Loading
    }

    fun setBtnCancelShowTime(@IntRange btnCancelShowTime: Int): Loading {
        this.btnCancelShowTime = btnCancelShowTime
        return this
    }

    fun setProgressGIF(@RawRes customProgressGIF: Int?): Loading {
        this.customProgressGIF = customProgressGIF
        return this
    }

    override fun checkBeforeShow() {
        checkNotNull(cancelText) { "LoadingBuilder - cancelText needs to be set" }
    }
}