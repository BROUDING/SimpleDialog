package com.brouding.simpledialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.annotation.StringDef;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.transitionseverywhere.TransitionManager;

import static android.content.Context.MODE_PRIVATE;

public class SimpleDialog extends Dialog implements View.OnClickListener {
    private ViewGroup    transitionsContainer;
    private LinearLayout btnCheck, btnConfirm, btnCancel, layoutCancel;
    private CheckBox     checkbox;

    private SharedPreferences mPreferences;
    private Builder builder;

    public enum BtnAction {
        CONFIRM,
        CANCEL,
        DIALOG_INSIDE,
        DIALOG_OUTSIDE
    }

    private void setTitle(String message) {
        TextView txtTitle = (TextView) transitionsContainer.findViewById(R.id.txt_title);

        if( builder.isTitleBold )
            txtTitle.setTypeface(null, Typeface.BOLD);

        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(message);
    }

    private void setContent(String message, Integer paddingLeft) {
        TextView txtContent = (TextView) transitionsContainer.findViewById(R.id.txt_content);

        if( paddingLeft != null ) {
            int left   = txtContent.getPaddingLeft(),
                top    = txtContent.getPaddingTop(),
                right  = txtContent.getPaddingRight(),
                bottom = txtContent.getPaddingBottom();

            txtContent.setPadding( left +paddingLeft, top, right, bottom );
        }

        txtContent.setText(message);
    }

    private void setBtnPermanentCheck() {
        if( builder.showProgress ) {
            throw new IllegalStateException("setBtnPermanentCheck - is unavailable when you want showProgress()");
        } else if ( builder.cancelText != null ) {
            throw new IllegalStateException("setBtnPermanentCheck - cannot be used with Cancel button");
        }

        TextView txtPermanent = (TextView) btnCheck.findViewById(R.id.text_btn_permanent);
        txtPermanent.setText(builder.permanentCheckText);

        if( builder.permanentTextSize != null ) {   // Default value = 13dp
            txtPermanent.setTextSize( TypedValue.COMPLEX_UNIT_DIP, builder.permanentTextSize );
        }

        if( builder.isPermanentTextBold )
            txtPermanent.setTypeface(null, Typeface.BOLD);
    }

    private void setBtnConfirm() {
        TextView txtConfirm = (TextView) btnConfirm.findViewById(R.id.text_btn);
        txtConfirm.setText(builder.confirmText);

        if( builder.confirmTextColor != null ) {
            txtConfirm.setTextColor( ContextCompat.getColor(builder.context, builder.confirmTextColor) );
        } else if ( builder.confirmTextColorString != null ) {
            txtConfirm.setTextColor( Color.parseColor( builder.confirmTextColorString ) );
        } else {
            txtConfirm.setTextColor( ContextCompat.getColor(builder.context, R.color.colorPrimary) );
        }

        if( builder.confirmTextSize != null ) { // Default value = 14dp
            txtConfirm.setTextSize( TypedValue.COMPLEX_UNIT_DIP, builder.confirmTextSize );
        }

        if( builder.isConfirmTextBold )
            txtConfirm.setTypeface(null, Typeface.BOLD);

        btnConfirm.setTag(BtnAction.CONFIRM);
        btnConfirm.setOnClickListener(this);
    }

    private void setBtnCancel() {
        TextView txtCancel = (TextView) btnCancel.findViewById(R.id.text_btn_cancel);

        if( !builder.showProgress )
            btnCancel.setVisibility(View.VISIBLE);

        if( builder.cancelTextColor != null ) {
            txtCancel.setTextColor( ContextCompat.getColor(builder.context, builder.cancelTextColor) );
        } else if ( builder.cancelTextColorString != null ) {
            txtCancel.setTextColor( Color.parseColor( builder.cancelTextColorString ) );
        } else {
            txtCancel.setTextColor( ContextCompat.getColor(builder.context, R.color.colorPrimary) );
        }

        if( builder.cancelTextSize != null ) {  // Default value = 14dp
            txtCancel.setTextSize( TypedValue.COMPLEX_UNIT_DIP, builder.cancelTextSize );
        }

        if( builder.isCancelTextBold )
            txtCancel.setTypeface(null, Typeface.BOLD);

        if( builder.cancelText != null )
            txtCancel.setText(builder.cancelText);

        btnCancel.setTag(BtnAction.CANCEL);
        btnCancel.setOnClickListener(this);
    }

	private void setProgressBar(Context context) {
        if( builder.customView != null ) {
            throw new IllegalStateException("setProgressBar - You cannot use it when you want customView");
        } else if ( builder.cancelText == null ) {
            throw new IllegalStateException("setProgressBar - cancelText needs to be set");
        }

        ImageView progressGif = (ImageView) transitionsContainer.findViewById(R.id.image_loading);
        progressGif.setVisibility(View.VISIBLE);

        Object customProgressGIF = builder.customProgressGIF;
        Glide.with(context).load(customProgressGIF==null ? R.raw.simple_dialog_progress_default : customProgressGIF).into(progressGif);
	}

	private void setCustomView(View customView) {
        if( builder.textContent != null ) {
            throw new IllegalStateException("setCustomView - You cannot use it when you have content");
        } else if( builder.showProgress ) {
            throw new IllegalStateException("setCustomView - You cannot use it when you want progress SimpleDialog");
        }

        ScrollView mScollView = (ScrollView) transitionsContainer.findViewById(R.id.layout_custom);

        customView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        transitionsContainer.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        WindowManager windowManager;
        windowManager   = (WindowManager) builder.context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point displaySize = new Point();
        display.getSize(displaySize);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mScollView.getLayoutParams();
        params.width  = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;

        int DECENT_HEIGHT = 300;
        int prettyHeight  = displaySize.y -transitionsContainer.getMeasuredHeight() -DECENT_HEIGHT;    // DECENT_HEIGHT is just for looks of the view

        if( customView.getMeasuredHeight() > prettyHeight ) {
            params.height = prettyHeight;

            if( builder.textTitle != null ) {
                params.height = prettyHeight -50;
            }
        }

        mScollView.setLayoutParams( params );
        mScollView.addView( customView );
    }

    @UiThread
    private void setLayout(final SimpleDialog dialog) {
        final SimpleDialog.Builder builder = dialog.builder;

        if( builder.showProgress ) {
            setContentView(R.layout.brouding_simple_dialog_progress);
            transitionsContainer = (ViewGroup) findViewById(R.id.layout_dialog);

            layoutCancel = (LinearLayout) transitionsContainer.findViewById(R.id.layout_cancel);
            btnCancel    = (LinearLayout) transitionsContainer.findViewById(R.id.btn_cancel);

            setContent(builder.textContent, builder.contentPaddingLeft);
            setProgressBar(builder.context);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(transitionsContainer);
                    layoutCancel.setVisibility(View.VISIBLE);
                }
            }, builder.btnCancelShowTime);

            setBtnCancel();

        } else if( builder.customView != null ) {
            setContentView(R.layout.brouding_simple_dialog_custom);
            transitionsContainer = (ViewGroup) findViewById(R.id.layout_dialog);

            LinearLayout layoutTitleContentContainer = (LinearLayout) transitionsContainer.findViewById(R.id.layout_title_content_container);

            View gapView = transitionsContainer.findViewById(R.id.view_gap);
            btnConfirm = (LinearLayout) transitionsContainer.findViewById(R.id.btn_okay);
            btnCancel  = (LinearLayout) transitionsContainer.findViewById(R.id.btn_cancel);
            btnCheck   = (LinearLayout) transitionsContainer.findViewById(R.id.btn_check_permanent);
            checkbox   = (CheckBox)     transitionsContainer.findViewById(R.id.check_permanent);
            btnCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkbox.setChecked( !checkbox.isChecked() );
                }
            });

            if( builder.textTitle != null ) {
                layoutTitleContentContainer.setVisibility(View.VISIBLE);
                setTitle(builder.textTitle);
            }

            setCustomView( builder.customView );

            if( builder.cancelText != null ) {
                gapView .setVisibility(View.GONE);
                btnCheck.setVisibility(View.GONE);
                setBtnCancel();
            }

            if( isPermanentSet() ) {
                btnCheck .setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                setBtnPermanentCheck();
            }

            setBtnConfirm();

        } else {
            setContentView(R.layout.brouding_simple_dialog_default);
            transitionsContainer = (ViewGroup) findViewById(R.id.layout_dialog);

            ImageView imageGuide = (ImageView) transitionsContainer.findViewById(R.id.image_guide);
            View gapView = transitionsContainer.findViewById(R.id.view_gap);
            btnConfirm = (LinearLayout) transitionsContainer.findViewById(R.id.btn_okay);
            btnCancel  = (LinearLayout) transitionsContainer.findViewById(R.id.btn_cancel);
            btnCheck   = (LinearLayout) transitionsContainer.findViewById(R.id.btn_check_permanent);
            checkbox   = (CheckBox)     transitionsContainer.findViewById(R.id.check_permanent);
            btnCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkbox.setChecked( !checkbox.isChecked() );
                }
            });

            setContent(builder.textContent, builder.contentPaddingLeft);

            if( builder.textTitle != null ) {
                setTitle(builder.textTitle);
            }

            if( builder.guideImageId != null ) {
                imageGuide.setVisibility(View.VISIBLE);
                imageGuide.setImageResource( builder.guideImageId );
            }

            if( builder.guideImagePadding != null ) {
                int pad = builder.guideImagePadding;
                imageGuide.setPadding(pad, pad, pad, pad);
            }

            if( builder.guideImageWidth != null || builder.guideImageHeight != null ) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)imageGuide.getLayoutParams();
                params.width   = builder.guideImageWidth  != null ? builder.guideImageWidth  : params.width;
                params.height  = builder.guideImageHeight != null ? builder.guideImageHeight : params.height;
                params.gravity = Gravity.CENTER;

                imageGuide.setLayoutParams(params);
            }

            if( builder.cancelText != null ) {
                gapView .setVisibility(View.GONE);
                btnCheck.setVisibility(View.GONE);
                setBtnCancel();
            }

            if( isPermanentSet() ) {
                btnCheck .setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                setBtnPermanentCheck();
            }

            setBtnConfirm();

        }

        dialog.setCancelable(builder.isCancelable);

        if( builder.dismissListener != null ) {
            dialog.setOnDismissListener(builder.dismissListener);
        }

        if( builder.cancelListener != null ) {
            dialog.setOnCancelListener(builder.cancelListener);
        }

        if( builder.isCancelableOnTouchOutside ) {
            LinearLayout layoutOutside = (LinearLayout) findViewById(R.id.layout_container);
            transitionsContainer.setTag(BtnAction.DIALOG_INSIDE);
            transitionsContainer.setOnClickListener(this);

            layoutOutside.setTag(BtnAction.DIALOG_OUTSIDE);
            layoutOutside.setOnClickListener(this);
        }
    }

    public Object getTag() {
        return builder.tag;
    }

    @SuppressLint("InflateParams")
    protected SimpleDialog(Builder builder) {
        super(builder.context, R.style.TransparentDialogTheme);

        this.builder = builder;

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.3f;

        if( getWindow() != null )
            getWindow().setAttributes(lpWindow);
        else
            throw new DialogException("Bad window token, you cannot show a dialog " +
                    "before an Activity is created or after it's hidden.");

        if( isPermanentSet() )
            mPreferences = builder.context.getSharedPreferences(builder.preferenceName, MODE_PRIVATE);

        setLayout(this);
    }

    @Override
    public final void onClick(View v) {
        BtnAction tag = (BtnAction) v.getTag();
        switch (tag) {
            case CONFIRM:
                if( isPermanentSet() ) {
                    setPermanentCheck();

                    if (builder.onConfirmWithPermanentCheckCallback != null) {
                        builder.onConfirmWithPermanentCheckCallback.onClick(this, tag, checkbox.isChecked());
                    }
                }

                if (builder.onConfirmCallback != null) {
                    builder.onConfirmCallback.onClick(this, tag);

                    if( isPermanentSet() ) {
                        Log.e("SimpleDialog", "Use BtnCallbackWithPermanentCheck instead of BtnCallback " +
                                "to see if permanentCheck is checked or not !");
                    }
                }
                break;

            case CANCEL:
            case DIALOG_OUTSIDE:
                if (builder.onCancelCallback != null) {
                    builder.onCancelCallback.onClick(this, tag);
                }
                break;

            case DIALOG_INSIDE:
                // Do nothing
                break;
        }

        if( !tag.equals(BtnAction.DIALOG_INSIDE) )
            dismiss();
    }

    private void setPermanentCheck() {
        if( checkbox.isChecked() ) {
            SharedPreferences.Editor edit = mPreferences.edit();
            edit.putBoolean(builder.permanentCheckKey, true);
            edit.apply();
        }
    }

    protected boolean isPermanentSet() {
        return builder.preferenceName != null && builder.permanentCheckKey != null;
    }

    @Nullable
    public final View getCustomView() {
        return builder.customView;
    }

    private static class DialogException extends WindowManager.BadTokenException {
        DialogException(@SuppressWarnings("SameParameterValue") String message) {
            super(message);
        }
    }

    @Override
    public void dismiss() {
        if( this.isShowing() )
            super.dismiss();
    }

    public interface BtnCallback {
        void onClick(@NonNull SimpleDialog dialog, @NonNull BtnAction which);
    }

    public interface BtnCallbackWithPermanentCheck {
        void onClick(@NonNull SimpleDialog dialog, @NonNull BtnAction which, boolean isPermanentChecked);
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public static class Builder {
        protected final Context  context;
        protected View customView = null;
        protected BtnCallback    onConfirmCallback = null, onCancelCallback = null;
        protected BtnCallbackWithPermanentCheck onConfirmWithPermanentCheckCallback = null;
        protected OnShowListener showListener;
        protected OnDismissListener dismissListener = null;
        protected OnCancelListener  cancelListener  = null;
        protected boolean isCancelable = false,
                          isCancelableOnTouchOutside = false,
                          showProgress = false,
                          isTitleBold  = true,
                          isPermanentTextBold = false,
                          isConfirmTextBold = true,
                          isCancelTextBold  = true;
        protected Integer btnCancelShowTime = 2500,
                          guideImageId = null,
                          guideImagePadding = null,
                          guideImageWidth  = null,
                          guideImageHeight = null,
                          contentPaddingLeft = null,
                          permanentTextSize  = null,
                          confirmTextSize  = null,
                          confirmTextColor = null,
                          cancelTextSize   = null,
                          cancelTextColor  = null,
                          customProgressGIF = null;
        protected String  textTitle   = null,
                          textContent = null,
                          permanentCheckText = "Don't show again",
                          confirmText = "Confirm",
                          cancelText  = null,
                          confirmTextColorString = null,
                          cancelTextColorString  = null,
                          preferenceName = null,
                          permanentCheckKey = null,
                          COLOR_PATTERN = "[#][\\w]{6}|[#][\\w]{8}";
        protected Object  tag = null;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public final Context getContext() {
            return context;
        }

        public Builder setTitle(@NonNull String message) {
            return setTitle(message, true);
        }

        public Builder setTitle(@NonNull String message, boolean isBold) {
            if( this.showProgress ) {
                throw new IllegalStateException("setTitle - You cannot use it when you want progress SimpleDialog");
            }

            this.textTitle = message;
            this.isTitleBold = isBold;
            return this;
        }

        public Builder setCustomView(@LayoutRes int layoutRes) {
            LayoutInflater mInflator = LayoutInflater.from(this.context);
            return setCustomView( mInflator.inflate(layoutRes, null) );
        }

        public Builder setCustomView(@NonNull View view) {
            if( view.getParent() != null && view.getParent() instanceof ViewGroup ) {
                ( (ViewGroup) view.getParent() ).removeView(view);
            }

            this.customView = view;
            return this;
        }

        public Builder setContent(@NonNull String message) {
            return setContent(message, null);
        }

        public Builder setContent(@NonNull String message, Integer paddingLeftDp) {
            if( message.isEmpty() ) {
                throw new IllegalStateException("setContent - message cannot be empty !");
            }

            this.textContent = message;
            this.contentPaddingLeft = getPXWithDP(paddingLeftDp);
            return this;
        }

        public Builder setPermanentCheck(@NonNull String preferenceName, @NonNull String preferenceKey) {
            this.preferenceName    = preferenceName;
            this.permanentCheckKey = preferenceKey;
            return this;
        }

        public Builder setBtnPermanentCheckText(@NonNull String message) {
            setBtnPermanentCheckText(message, false);
            return this;
        }

        public Builder setBtnPermanentCheckText(@NonNull String message, boolean isBold) {
            this.permanentCheckText  = message;
            this.isPermanentTextBold = isBold;
            return this;
        }

        public Builder setBtnPermanentCheckTextSizeDp(@IntRange int textSizeDp) {
            this.permanentTextSize = textSizeDp;
            return this;
        }

        public Builder setBtnConfirmText(@NonNull String message) {
            setBtnConfirmText(message, true);
            return this;
        }

        public Builder setBtnConfirmText(@NonNull String message, boolean isBold) {
            this.confirmText = message;
            this.isConfirmTextBold = isBold;
            return this;
        }

        public Builder setBtnConfirmTextSizeDp(@IntRange int textSizeInDp) {
            this.confirmTextSize = textSizeInDp;
            return this;
        }

        public Builder setBtnConfirmTextColor(@ColorRes Integer color) {
            this.confirmTextColor = color;
            return this;
        }

        public Builder setBtnConfirmTextColor(@NonNull String color) {
            int colorLength = color.length();
            if( !color.matches(COLOR_PATTERN) ) {
                throw new IllegalStateException("setBtnConfirmTextColor - You can only use HTML color format" +
                        " or referred value from res/values/colors  -ex) \"#0074ef\" or R.color.exampleColor");
            }

            this.confirmTextColorString = color;
            return this;
        }

        public Builder onConfirm(@NonNull BtnCallback callback) {
            this.onConfirmCallback = callback;
            return this;
        }

        public Builder onConfirm(@NonNull BtnCallbackWithPermanentCheck callback) {
            this.onConfirmWithPermanentCheckCallback = callback;
            return this;
        }

        public Builder setBtnCancelText(@NonNull String message) {
            setBtnCancelText(message, true);
            return this;
        }

        // Don't use with setPermanentCheck
        public Builder setBtnCancelText(@NonNull String message, boolean isBold) {
            this.cancelText = message;
            this.isCancelTextBold = isBold;
            return this;
        }

        public Builder setBtnCancelTextSizeDp(@IntRange int textSizeInDp) {
            this.cancelTextSize = textSizeInDp;
            return this;
        }

        public Builder setBtnCancelTextColor(@ColorRes Integer color) {
            this.cancelTextColor = color;
            return this;
        }

        public Builder setBtnCancelTextColor(@NonNull String color) {
            int colorLength = color.length();
            if( !color.matches(COLOR_PATTERN) ) {
                throw new IllegalStateException("setBtnCancelTextColor - You can only use HTML color format" +
                        " or referred value from res/values/colors  -ex) \"#0074ef\" or @color/exampleColor");
            }

            this.cancelTextColorString = color;
            return this;
        }

        public Builder setBtnCancelShowTime(@IntRange int btnCancelShowTime) {
            this.btnCancelShowTime = btnCancelShowTime;
            return this;
        }

        public Builder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            this.isCancelableOnTouchOutside = isCancelable;
            return this;
        }

        public Builder setCancelableOnTouchOutside(boolean isCancelableOnTouchOutside) {
            this.isCancelableOnTouchOutside = isCancelableOnTouchOutside;
            return this;
        }

        public Builder onCancel(@NonNull BtnCallback callback) {
            this.onCancelCallback = callback;
            return this;
        }

        public Builder setGuideImage(@DrawableRes int imageId) {
            setGuideImage(imageId, null);
            return this;
        }

        public Builder setGuideImage(@DrawableRes int imageId, @IntRange Integer paddingDP) {
            this.guideImageId = imageId;
            this.guideImagePadding = getPXWithDP(paddingDP);
            return this;
        }

        public Builder setGuideImageSizeDp(@IntRange Integer width, @IntRange Integer height) {
            this.guideImageWidth  = getPXWithDP(width);
            this.guideImageHeight = getPXWithDP(height);
            return this;
        }

        public Builder showProgress(boolean showProgress) {
            this.showProgress = showProgress;
            return this;
        }

        public Builder setProgressGIF(@RawRes Integer customProgressGIF) {
            this.showProgress      = true;
            this.customProgressGIF = customProgressGIF;
            return this;
        }

        public Builder onDialogShow(@NonNull OnShowListener listener) {
            this.showListener = listener;
            return this;
        }

        public Builder onDialogDismiss(@NonNull OnDismissListener listener) {
            this.dismissListener = listener;
            return this;
        }

        public Builder onDialogCancel(@NonNull OnCancelListener listener) {
            this.cancelListener = listener;
            return this;
        }

        public Builder setTag(@Nullable Object tag) {
            this.tag = tag;
            return this;
        }

        @UiThread
        public SimpleDialog build() {
            return new SimpleDialog(this);
        }

        @UiThread
        public SimpleDialog show() {
            if( this.preferenceName != null && this.permanentCheckKey != null ) {
                Log.e("SimpleDialog", "Use showIfPermanentValueIsFalse() instead of show() for efficiency");
            }

            return showDialog();
        }

        @UiThread
        public SimpleDialog showIfPermanentValueIsFalse() {
            SharedPreferences mPreferences = context.getSharedPreferences(preferenceName, MODE_PRIVATE);
            if( mPreferences.getBoolean(permanentCheckKey, false) ) {
                return null;
            }

            return showDialog();
        }

        @UiThread
        private SimpleDialog showDialog() {
            SimpleDialog dialog = build();
            dialog.show();
            return dialog;
        }

        private Integer getPXWithDP(Integer dp) {
            if( dp == null ) {
                return null;
            }
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        }
    }
}