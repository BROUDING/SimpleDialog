package com.brouding.simpledialog.builder

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.annotation.LayoutRes
import androidx.annotation.UiThread
import com.brouding.simpledialog.BtnActionCallback
import com.brouding.simpledialog.SimpleDialog


class Custom(override val context: Context) : General(context) {
    var customView: View? = null
    private var mPreferences: SharedPreferences? = null
    private var preferenceName: String? = null
    private var permanentCheckKey: String? = null

    var permanentCheckText = "Don't show again"
    var permanentTextSize: Int? = 13
    var isPermanentTextBold = false

    var guideImageId: Int? = null
    var guideImagePadding: Int? = null
    var guideImageWidth: Int? = null
    var guideImageHeight: Int? = null

    init {
        type = Type.CUSTOM
    }

    fun applyGeneral(func: General.() -> Unit): Custom {
        this.func()
        return this
    }

    fun setGuideImage(@DrawableRes imageId: Int): Custom {
        setGuideImage(imageId, null)
        return this
    }

    fun setGuideImage(@DrawableRes imageId: Int, @IntRange paddingDP: Int?): Custom {
        guideImageId = imageId
        guideImagePadding = getPXWithDP(paddingDP)
        return this
    }

    fun setGuideImageSizeDp(@IntRange width: Int?, @IntRange height: Int?): Custom {
        guideImageWidth = getPXWithDP(width)
        guideImageHeight = getPXWithDP(height)
        return this
    }

    fun setCustomView(@LayoutRes layoutRes: Int): Custom {
        val mInflator = LayoutInflater.from(context)
        return setCustomView(mInflator.inflate(layoutRes, null))
    }

    fun setCustomView(view: View): Custom {
        if (view.parent != null && view.parent is ViewGroup) {
            (view.parent as ViewGroup).removeView(view)
        }
        customView = view
        return this
    }

    override fun onBtnAction(callbackBtn: BtnActionCallback?): Custom {
        return super.onBtnAction(callbackBtn) as Custom
    }

    fun setPermanentCheck(preferenceName: String, preferenceKey: String): Custom {
        this.preferenceName = preferenceName
        this.permanentCheckKey = preferenceKey
        return this
    }

    fun isPermanentSet(): Boolean = preferenceName != null && permanentCheckKey != null

    override fun checkBeforeShow() {}

    fun setBtnPermanentCheckText(message: String): Custom {
        setBtnPermanentCheckText(message, false)
        return this
    }

    fun setBtnPermanentCheckText(
        message: String,
        isBold: Boolean
    ): Custom {
        permanentCheckText = message
        isPermanentTextBold = isBold
        return this
    }

    fun setBtnPermanentCheckTextSizeDp(@IntRange textSizeDp: Int): Custom {
        permanentTextSize = textSizeDp
        return this
    }

    fun applyPermanentCheck() {
        mPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        val edit = mPreferences!!.edit()
        edit.putBoolean(permanentCheckKey, true)
        edit.apply()
    }

    @UiThread
    fun showIfPermanentValueIsFalse() {
        mPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        if (!mPreferences!!.getBoolean(permanentCheckKey, false)) {
            SimpleDialog(this).show()
        }
    }
}