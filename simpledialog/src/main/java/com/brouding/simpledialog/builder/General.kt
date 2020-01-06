package com.brouding.simpledialog.builder

import android.content.Context
import android.util.TypedValue
import androidx.annotation.*
import androidx.annotation.IntRange
import com.brouding.simpledialog.BtnActionCallback
import com.brouding.simpledialog.DialogActionCallback

open class General(open val context: Context) {
    var btnActionCallback: BtnActionCallback? = null
    var dialogActionCallback: DialogActionCallback? = null
    var backgroundResourceId: Int? = null
    var isCancelable = false
    var isCancelableOnTouchOutside = false
    var textTitle: String? = null
    var titleTextColor: Int? = null
    var isTitleBold = true
    var isConfirmTextBold = true
    var isCancelTextBold = true
    var textContent: String? = null
    var contentPaddingLeft: Int? = null
    var contentTextColor: Int? = null
    var confirmTextSize: Int? = null
    var confirmTextColor: Int? = null
    var cancelTextSize: Int? = null
    var cancelTextColor: Int? = null
    var confirmText = "Confirm"
    var cancelText: String? = null
    var confirmTextColorString: String? = null
    var cancelTextColorString: String? = null
    private var COLOR_PATTERN = "[#][\\w]{6}|[#][\\w]{8}"
    var tag: Any? = null
    internal var type = Type.GENERAL

    enum class Type {
        GENERAL, LOADING, CUSTOM
    }

    fun setBackgroundResource(@DrawableRes resId: Int): General {
        backgroundResourceId = resId
        return this
    }

    fun setTitle(message: String): General {
        return setTitle(message, true)
    }

    fun setTitle(
        message: String,
        isBold: Boolean
    ): General {
        textTitle = message
        isTitleBold = isBold
        return this
    }

    fun setTitleTextColor(@ColorRes color: Int?): General {
        titleTextColor = color
        return this
    }

    fun setContent(message: String): General {
        return setContent(message, null)
    }

    fun setContent(
        message: String,
        paddingLeftDp: Int?
    ): General {
        check(message.isNotEmpty()) { "setContent - message cannot be empty !" }
        textContent = message
        contentPaddingLeft = getPXWithDP(paddingLeftDp)
        return this
    }

    fun setContentTextColor(@ColorRes color: Int?): General {
        contentTextColor = color
        return this
    }

    fun setBtnConfirmText(message: String): General {
        setBtnConfirmText(message, true)
        return this
    }

    fun setBtnConfirmText(
        message: String,
        isBold: Boolean
    ): General {
        confirmText = message
        isConfirmTextBold = isBold
        return this
    }

    fun setBtnConfirmTextSizeDp(@IntRange textSizeInDp: Int): General {
        confirmTextSize = textSizeInDp
        return this
    }

    fun setBtnConfirmTextColor(@ColorRes color: Int?): General {
        confirmTextColor = color
        return this
    }

    fun setBtnConfirmTextColor(color: String): General {
        check( color.matches(COLOR_PATTERN.toRegex()) ) {
            "setBtnConfirmTextColor - You can only use HTML color format" +
                    " or referred value from res/values/colors  -ex) \"#0074ef\" or R.color.exampleColor"
        }
        confirmTextColorString = color
        return this
    }

    open fun onBtnAction(callbackBtn: BtnActionCallback? = null): General {
        btnActionCallback = callbackBtn
        return this
    }

    fun onDialogAction(callbackDialog: DialogActionCallback? = null): General {
        dialogActionCallback = callbackDialog
        return this
    }

    fun setBtnCancelText(message: String): General {
        setBtnCancelText(message, true)
        return this
    }

    fun setBtnCancelText(
        message: String,
        isBold: Boolean
    ): General {
        cancelText = message
        isCancelTextBold = isBold
        return this
    }

    fun setBtnCancelTextSizeDp(@IntRange textSizeInDp: Int): General {
        cancelTextSize = textSizeInDp
        return this
    }

    fun setBtnCancelTextColor(@ColorRes color: Int?): General {
        cancelTextColor = color
        return this
    }

    fun setBtnCancelTextColor(color: String): General {
        check( color.matches(COLOR_PATTERN.toRegex()) ) {
            "setBtnCancelTextColor - You can only use HTML color format" +
                    " or referred value from res/values/colors  -ex) \"#0074ef\" or @color/exampleColor"
        }
        cancelTextColorString = color
        return this
    }

    fun setCancelable(isCancelable: Boolean): General {
        this.isCancelable = isCancelable
        isCancelableOnTouchOutside = isCancelable
        return this
    }

    fun setCancelableOnTouchOutside(isCancelableOnTouchOutside: Boolean): General {
        this.isCancelableOnTouchOutside = isCancelableOnTouchOutside
        return this
    }

    fun setTag(tag: Any?): General {
        this.tag = tag
        return this
    }

    open fun checkBeforeShow() {}

    fun getPXWithDP(dp: Int?): Int? {
        return if (dp == null) {
            null
        } else TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}