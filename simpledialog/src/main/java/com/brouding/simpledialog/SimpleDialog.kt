package com.brouding.simpledialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import android.os.Handler
import android.util.TypedValue
import android.view.*
import android.view.WindowManager.BadTokenException
import android.widget.*
import androidx.annotation.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import androidx.transition.TransitionManager
import com.brouding.simpledialog.builder.*
import com.brouding.simpledialog.extra.BtnAction
import com.brouding.simpledialog.extra.DialogAction
import com.brouding.simpledialog.extra.Type
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.brouding_simple_dialog_default.*
import kotlinx.android.synthetic.main.brouding_simple_dialog_default.txtTitle
import kotlinx.android.synthetic.main.brouding_simple_dialog_include_buttons.*
import kotlinx.android.synthetic.main.brouding_simple_dialog_selection.*

typealias BtnActionCallback = (BtnAction) -> Unit
typealias DialogActionCallback = (DialogAction) -> Unit
typealias SelectionCallback = (String) -> Unit

@SuppressLint("InflateParams")
class SimpleDialog
constructor(private val builder: General) : Dialog(builder.context, R.style.TransparentDialogTheme), View.OnClickListener {
    private lateinit var transitionsContainer: ViewGroup
    private var layoutCancel: LinearLayout? = null
    private var checkbox: CheckBox? = null

    init {
        val windowLayoutParams = WindowManager.LayoutParams()
        windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        windowLayoutParams.dimAmount = 0.3f
        if (window != null) window!!.attributes = windowLayoutParams else throw DialogException(
            "Bad window token, you cannot show a dialog " +
                    "before an Activity is created or after it's hidden."
        )

        setLayout(builder)
    }

    private fun setTitle(message: String?, textColor: Int? = null) {
        if (builder.isTitleBold)
            txtTitle.setTypeface(null, Typeface.BOLD)

        txtTitle.visibility = View.VISIBLE
        txtTitle.text = message

        textColor?.let {
            txtTitle.setTextColor( getColor(context, it) )
        }
    }

    private fun setContent(message: String?, paddingLeft: Int? = null, textColor: Int? = null) {
        paddingLeft?.let {
            val left   = txtContent.paddingLeft
            val top    = txtContent.paddingTop
            val right  = txtContent.paddingRight
            val bottom = txtContent.paddingBottom
            txtContent.setPadding(left + paddingLeft, top, right, bottom)
        }

        txtContent.text = message

        textColor?.let {
            txtContent.setTextColor( getColor(context, it) )
        }
    }

    private fun setBtnConfirm() {
        btnConfirm.text = builder.confirmText
        when {
            builder.confirmTextColor != null -> {
                btnConfirm.setTextColor(
                    getColor(
                        builder.context,
                        builder.confirmTextColor!!
                    )
                )
            }
            builder.confirmTextColorString != null -> {
                btnConfirm.setTextColor(Color.parseColor(builder.confirmTextColorString))
            }
            else -> {
                btnConfirm.setTextColor(
                    getColor(
                        builder.context,
                        R.color.colorPrimary
                    )
                )
            }
        }
        if (builder.confirmTextSize != null) { // Default value = 14dp
            btnConfirm.setTextSize(TypedValue.COMPLEX_UNIT_DIP, builder.confirmTextSize!!.toFloat())
        }
        if (builder.isConfirmTextBold) btnConfirm.setTypeface(null, Typeface.BOLD)
        btnConfirm.tag = BtnAction.CONFIRM
        btnConfirm.setOnClickListener(this)
    }

    private fun setBtnCancel() {
        btnCancel.visibility = View.VISIBLE

        when {
            builder.cancelTextColor != null -> {
                btnCancel.setTextColor(
                    getColor(
                        builder.context,
                        builder.cancelTextColor!!
                    )
                )
            }
            builder.cancelTextColorString != null -> {
                btnCancel.setTextColor(Color.parseColor(builder.cancelTextColorString))
            }
            else -> {
                btnCancel.setTextColor(
                    getColor(
                        builder.context,
                        R.color.colorPrimary
                    )
                )
            }
        }

        builder.cancelText?.let {
            btnCancel.text = it
        }

        builder.cancelTextSize?.let { // Default value = 14dp
            btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.toFloat())
        }

        if (builder.isCancelTextBold) btnCancel.setTypeface(null, Typeface.BOLD)

        btnCancel.tag = BtnAction.CANCEL
        btnCancel.setOnClickListener(this)
    }

    @UiThread
    private fun setLayout(builder: General) {
        when (builder.type) {
            Type.GENERAL -> {
                setContentView(R.layout.brouding_simple_dialog_default)
                transitionsContainer = findViewById(R.id.layoutDialog)

                // TODO: 백그라운드 작업중
                builder.backgroundResourceId?.let {
                    transitionsContainer.setBackgroundResource(it)
                }

                setContent(builder.textContent, builder.contentPaddingLeft, builder.contentTextColor)

                builder.textTitle?.let {
                    setTitle(it, builder.titleTextColor)
                }

                builder.cancelText?.let {
                    btnCheckPermanent.visibility = View.GONE
                    setBtnCancel()
                }

                setBtnConfirm()
            }

            Type.LOADING -> {
                val loadingBuilder = builder as Loading
                setContentView(R.layout.brouding_simple_dialog_loading)
                transitionsContainer = findViewById(R.id.layoutDialog)

                fun setProgressBar(context: Context) {
                    val progressGif = transitionsContainer.findViewById(R.id.imgLoading) as ImageView
                    progressGif.visibility = View.VISIBLE
                    val customProgressGIF: Any? = loadingBuilder.customProgressGIF

                    Glide.with(context)
                        .load(customProgressGIF ?: R.raw.simple_dialog_progress_default)
                        .into(progressGif)
                }

                layoutCancel = transitionsContainer.findViewById(R.id.layoutCancel) as LinearLayout
                setContent(loadingBuilder.textContent, loadingBuilder.contentPaddingLeft)

                setProgressBar(loadingBuilder.context)
                val handler = Handler()
                handler.postDelayed({
                    TransitionManager.beginDelayedTransition(transitionsContainer)
                    layoutCancel!!.visibility = View.VISIBLE
                }, loadingBuilder.btnCancelShowTime.toLong())

                setBtnCancel()
            }

            Type.SELECTION -> {
                val selectionBuilder = builder as Selection
                setContentView(R.layout.brouding_simple_dialog_selection)
                transitionsContainer = findViewById(R.id.layoutDialog)

                selectionBuilder.textTitle?.let {
                    setTitle(it, selectionBuilder.titleTextColor)
                }

                selectionBuilder.selectionList?.let { dataList ->
                    listOfData.adapter = SelectionAdapter(object: SelectionAdapter.OnSelectListener {
                        override fun onSelect(option: String) {
                            selectionBuilder.selectionCallback?.invoke(option)
                            dismiss()
                        }
                    }, selectionBuilder.paddingLeftDp)
                    listOfData.setHasFixedSize(true)

                    (listOfData.adapter as SelectionAdapter).submitList(dataList.toMutableList())
                }
            }

            Type.CUSTOM -> {
                setContentView(R.layout.brouding_simple_dialog_custom)  // NOT_AGAIN = setContentView(R.layout.brouding_simple_dialog_default)
                transitionsContainer = findViewById(R.id.layoutDialog)

                val layoutTitleContentContainer = transitionsContainer.findViewById(R.id.layoutTitleContentContainer) as LinearLayout
                val imageGuide = transitionsContainer.findViewById(R.id.imgGuide) as ImageView
                val customBuilder = builder as Custom

                if( customBuilder.customView != null )
                    customView = customBuilder.customView!!

                fun setBtnPermanentCheck() {
                    val txtPermanent = btnCheckPermanent.findViewById(R.id.txtBtnPermanent) as TextView
                    txtPermanent.text = customBuilder.permanentCheckText

                    customBuilder.permanentTextSize?.let {    // Default value = 13dp
                        txtPermanent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, it.toFloat())
                    }

                    txtPermanent.setTypeface(null, if (customBuilder.isPermanentTextBold) Typeface.BOLD else Typeface.NORMAL)
                }

                checkbox = transitionsContainer.findViewById(R.id.checkBoxPermanent)
                btnCheckPermanent.setOnClickListener { checkbox!!.isChecked = !checkbox!!.isChecked }

                customBuilder.textTitle?.let {
                    layoutTitleContentContainer.visibility = View.VISIBLE
                    setTitle(it, customBuilder.titleTextColor)
                }

                customBuilder.textContent?.let {
                    setContent(it, customBuilder.contentPaddingLeft)
                }

                customBuilder.guideImageId?.let {
                    imageGuide.visibility = View.VISIBLE
                    imageGuide.setImageResource(it)
                }

                customBuilder.guideImagePadding?.let {
                    imageGuide.setPadding(it, it, it, it)
                }

                if (customBuilder.guideImageWidth != null || builder.guideImageHeight != null) {
                    val params = imageGuide.layoutParams as LinearLayout.LayoutParams
                    params.width = if (customBuilder.guideImageWidth != null) customBuilder.guideImageWidth!! else params.width
                    params.height = if (customBuilder.guideImageHeight != null) customBuilder.guideImageHeight!! else params.height
                    params.gravity = Gravity.CENTER
                    imageGuide.layoutParams = params
                }

                customBuilder.cancelText?.let {
                    btnCheckPermanent.visibility = View.GONE
                    setBtnCancel()

                    check(!isPermanentSet) {
                        "PermanentCheck and Cancel button cannot be used together"
                    }
                }

                if (isPermanentSet) {
                    btnCheckPermanent.visibility = View.VISIBLE
                    btnCancel.visibility = View.GONE
                    setBtnPermanentCheck()
                }

                setBtnConfirm()
            }
        }

        setGeneralLayout()
    }

    private fun setGeneralLayout() {
        setCancelable(builder.isCancelable)

        builder.dialogActionCallback?.let { dialogActionCallback ->
            setOnShowListener {
                dialogActionCallback.invoke(DialogAction.SHOW)
            }

            setOnDismissListener {
                dialogActionCallback.invoke(DialogAction.DISMISS)
            }

            setOnCancelListener {
                dialogActionCallback.invoke(DialogAction.CANCEL)
            }
        }

        if (builder.isCancelableOnTouchOutside) {
            val layoutOutside = findViewById<ConstraintLayout>(R.id.layoutContainer)

            transitionsContainer.tag = BtnAction.DIALOG_INSIDE
            transitionsContainer.setOnClickListener(this)
            layoutOutside.tag = BtnAction.DIALOG_OUTSIDE
            layoutOutside.setOnClickListener(this)
        }
    }

    val tag: Any?
        get() = builder.tag

    override fun onClick(v: View) {
        val tag = v.tag as BtnAction
        when (tag) {
            BtnAction.CONFIRM -> {
                super.dismiss()

                if (isPermanentSet) {
                    val builder = builder as Custom
                    if (checkbox!!.isChecked) {
                        builder.applyPermanentCheck()

                        builder.btnActionCallback?.invoke(BtnAction.CONFIRM_WITH_PERMANENT_CHECK)
                    } else
                        builder.btnActionCallback?.invoke(BtnAction.CONFIRM_WITHOUT_PERMANENT_CHECK)
                } else
                    builder.btnActionCallback?.invoke(BtnAction.CONFIRM)
            }

            BtnAction.CANCEL -> {
                super.dismiss()
                builder.btnActionCallback?.invoke(BtnAction.CANCEL)
            }

            BtnAction.DIALOG_OUTSIDE -> super.dismiss()

            BtnAction.DIALOG_INSIDE -> { }
        }

        // Dismiss after an action
        if (tag != BtnAction.DIALOG_INSIDE) hide()
    }

    private val isPermanentSet: Boolean
        get() = when(builder.type) {
                Type.CUSTOM -> {
                    val builder = builder as Custom
                    builder.isPermanentSet()
                }
                else -> false
            }

    // DECENT_HEIGHT is just for looks of the view
    private var customView: View
        get() {
            return (builder as Custom).customView!!
        }
        private set(customView) {
            val mScollView = transitionsContainer.findViewById(R.id.scrollViewCustom) as ScrollView
            customView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            transitionsContainer.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

            val windowManager: WindowManager = builder.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val displaySize = Point()
            display.getSize(displaySize)

            val params = mScollView.layoutParams as LinearLayout.LayoutParams
            params.width = LinearLayout.LayoutParams.MATCH_PARENT
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT
            val DECENT_HEIGHT = 300
            val prettyHeight = displaySize.y - transitionsContainer.measuredHeight - DECENT_HEIGHT // DECENT_HEIGHT is just for looks of the view

            if (customView.measuredHeight > prettyHeight) {
                params.height = prettyHeight
                if (builder.textTitle != null) {
                    params.height = prettyHeight - 50
                }
            }

            mScollView.layoutParams = params
            mScollView.addView(customView)
        }

    fun showIfNotChecked() {
        val customBuilder = builder as Custom

        check(isPermanentSet) {
            "setPermanentCheck function is not used properly"
        }

        customBuilder.showIfPermanentValueIsFalse()
    }

    override fun show() {
        builder.checkBeforeShow()
        super.show()
    }

    fun showAndReturn(): SimpleDialog {
        show()
        return this
    }

    fun getDialog(): SimpleDialog = this

    private class DialogException internal constructor(message: String?) : BadTokenException(message)
}