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

import java.util.IllegalFormatException;

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

    // TODO: Exceptions for things that cannot be used together !

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

        txtCancel.setText(builder.cancelText);

        btnCancel.setTag(BtnAction.CANCEL);
        btnCancel.setOnClickListener(this);
    }

	private void setProgressBar(Context context) {
        ImageView progressGif = (ImageView) transitionsContainer.findViewById(R.id.image_loading);
        progressGif.setVisibility(View.VISIBLE);

        Object customProgressGIF = builder.customProgressGIF;
        Glide.with(context).load(customProgressGIF==null ? R.raw.simple_dialog_progress_default : customProgressGIF).into(progressGif);
	}

	private void setCustomView(View customView) {
        ScrollView mScollView = (ScrollView) transitionsContainer.findViewById(R.id.layout_custom);
        final int DECENT_HEIGHT = 600;

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

        // (ShowCase) At G6, DECENT_HEIGHT is little bit longer than this limit
        if( customView.getMeasuredHeight() > displaySize.y -transitionsContainer.getMeasuredHeight() -DECENT_HEIGHT ) {    // DECENT_HEIGHT is just for looks of the view
            params.height = builder.getPXWithDP(520);   // DECENT_HEIGHT is just for looks of the view

            if( builder.textTitle != null ) {
                params.height = builder.getPXWithDP(420);
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

            if( builder.cancelText != null ) {
                setBtnCancel();
            }
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
                gapView  .setVisibility(View.GONE);
                btnCheck .setVisibility(View.GONE);
                setBtnCancel();
            }

            if( builder.permanentCheckKey!=null ) {
                btnCheck .setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                setBtnPermanentCheck();
            }

            if( builder.confirmText != null ) {
                setBtnConfirm();
            }
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
                gapView  .setVisibility(View.GONE);
                btnCheck .setVisibility(View.GONE);
                setBtnCancel();
            }

            if( builder.permanentCheckKey!=null ) {
                btnCheck .setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
                setBtnPermanentCheck();
            }

            if( builder.confirmText != null ) {
                setBtnConfirm();
            }
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

        if( builder.preferenceName != null )
            mPreferences = builder.context.getSharedPreferences(builder.preferenceName, MODE_PRIVATE);

        setLayout(this);
    }

    @Override
    public final void onClick(View v) {
        BtnAction tag = (BtnAction) v.getTag();
        switch (tag) {
            case CONFIRM:
                if( builder.permanentCheckKey!=null ) {
                    permanentCheck();

                    if (builder.onConfirmWithPermanentCheckCallback != null) {
                        builder.onConfirmWithPermanentCheckCallback.onClick(this, tag, checkbox.isChecked());
                    }
                }

                if (builder.onConfirmCallback != null) {
                    builder.onConfirmCallback.onClick(this, tag);
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

    private void permanentCheck() {
        if( checkbox.isChecked() ) {
            SharedPreferences.Editor edit = mPreferences.edit();
            edit.putBoolean(builder.permanentCheckKey, true);
            edit.apply();
        }
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
                          isTitleBold  = false,
                          isConfirmTextBold = false,
                          isPermanentTextBold = false,
                          isCancelTextBold  = false;
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
                          cancelTextColor  = null;
        protected String  textTitle   = null,
                          textContent = null,
                          permanentCheckText = "Don't show again",
                          confirmText = null,
                          cancelText  = null,
                          confirmTextColorString = null,
                          cancelTextColorString  = null,
                          preferenceName = null,
                          permanentCheckKey = null;
        protected Object  tag,
                          customProgressGIF = null;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public final Context getContext() {
            return context;
        }

        public Builder setCustomView(@LayoutRes int layoutRes) {
            LayoutInflater mInflator = LayoutInflater.from(this.context);
            return setCustomView( mInflator.inflate(layoutRes, null) );
        }

        public Builder setCustomView(@NonNull View view) {
            if( this.textContent != null ) {
                throw new IllegalStateException("You cannot use customView() when you have content");
            } else if( this.showProgress ) {
                throw new IllegalStateException("You cannot use customView() when you want progress SimpleDialog");
            }

            if( view.getParent() != null && view.getParent() instanceof ViewGroup ) {
                ( (ViewGroup) view.getParent() ).removeView(view);
            }

            this.customView = view;
            return this;
        }

        public Builder setTitle(String message) {
            if( this.showProgress ) {
                throw new IllegalStateException("You cannot use setTitle() when you want progress SimpleDialog");
            }

            this.textTitle = message;
            this.isTitleBold = true;
            return this;
        }

        public Builder setTitle(String message, boolean isBold) {
            if( this.showProgress ) {
                throw new IllegalStateException("You cannot use setTitle() when you want progress SimpleDialog");
            }

            this.textTitle = message;
            this.isTitleBold = isBold;
            return this;
        }

        public Builder setContent(@NonNull String message) {
            return setContent(message, null);
        }

        public Builder setContent(@NonNull String message, Integer paddingLeft) {
            this.textContent = message;
            this.contentPaddingLeft = getPXWithDP(paddingLeft);
            return this;
        }

        public Builder setBtnPermanentCheckText(@NonNull String message) {
            this.permanentCheckText  = message;
            this.isPermanentTextBold = false;
            return this;
        }

        public Builder setBtnPermanentCheckText(@NonNull String message, boolean isBold) {
            this.permanentCheckText  = message;
            this.isPermanentTextBold = isBold;
            return this;
        }

        public Builder setBtnPermanentCheckTextSizeDp(@IntRange int textSizeInDp) {
            this.permanentTextSize = textSizeInDp;
            return this;
        }

        public Builder setPermanentCheck(@NonNull String preferenceName, @NonNull String preferenceKey) {
            if( this.showProgress ) {
                throw new IllegalStateException("'setPermanentCheck' is unavailable when you want showProgress()");
            }

            this.preferenceName    = preferenceName;
            this.permanentCheckKey = preferenceKey;
            return this;
        }

        public Builder setBtnConfirmText(@NonNull String message) {
            this.confirmText = message;
            this.isConfirmTextBold = true;
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

        public Builder setBtnConfirmTextColor(String color) {
            int colorLength = color.length();
            if( !color.matches("[#][\\w]{6}|[#][\\w]{8}") ) {
                throw new IllegalStateException("[setBtnConfirmTextColor] You can only use HTML color format  -ex) \"#0074ef\" or \"#090074ef\"");
            }

            this.confirmTextColorString = color;
            return this;
        }

        public Builder onConfirm(BtnCallback callback) {
            this.onConfirmCallback = callback;
            return this;
        }

        public Builder onConfirm(BtnCallbackWithPermanentCheck callback) {
            this.onConfirmWithPermanentCheckCallback = callback;
            return this;
        }

        public Builder setBtnCancelText(String message) {
            this.cancelText = message;
            this.isCancelTextBold = true;
            return this;
        }

        public Builder setBtnCancelText(String message, boolean isBold) {
            this.cancelText = message;
            this.isCancelTextBold = isBold;
            return this;
        }

        public Builder setBtnCancelTextSizeDp(@IntRange int textSizeInDp) {
            this.cancelTextSize = textSizeInDp;
            return this;
        }

        public Builder setBtnCancelShowTime(@IntRange int btnCancelShowTime) {
            this.btnCancelShowTime = btnCancelShowTime;
            return this;
        }

        public Builder setBtnCancelTextColor(@ColorRes int color) {
            this.cancelTextColor = color;
            return this;
        }

        public Builder setBtnCancelTextColor(String color) {
            int colorLength = color.length();
            if( !color.matches("[#][\\w]{6}|[#][\\w]{8}") ) {
                throw new IllegalStateException("[setBtnCancelTextColor] You can only use HTML color format  -ex) \"#0074ef\"");
            }

            this.cancelTextColorString = color;
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

        public Builder onCancel(BtnCallback callback) {
            this.onCancelCallback = callback;
            return this;
        }

        public Builder setGuideImage(@DrawableRes int imageId) {
            this.guideImageId = imageId;
            return this;
        }

        public Builder setGuideImage(@DrawableRes int imageId, @IntRange int padding) {
            this.guideImageId = imageId;
            this.guideImagePadding = getPXWithDP(padding);
            return this;
        }

        public Builder setGuideImageSizeDp(@IntRange int width, @IntRange int height) {
            this.guideImageWidth  = getPXWithDP(width);
            this.guideImageHeight = getPXWithDP(height);
            return this;
        }

        public Builder showProgress(boolean showProgress) {
            if( this.customView != null ) {
                throw new IllegalStateException("You cannot use showProgress() when you want customView");
            }

            this.showProgress = showProgress;
            return this;
        }

        public Builder setProgressGIF(Object customProgressGIF) {
            if( this.customView != null ) {
                throw new IllegalStateException("You cannot use showProgress() when you want customView");
            }

            this.showProgress      = true;
            this.customProgressGIF = customProgressGIF;
            return this;
        }

        public Builder onShowListener(@NonNull OnShowListener listener) {
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
            SimpleDialog dialog = build();
            dialog.show();
            return dialog;
        }

        @UiThread
        public SimpleDialog showIfPermanentValueIsFalse() {
            SharedPreferences mPreferences = context.getSharedPreferences(preferenceName, MODE_PRIVATE);
            if( mPreferences.getBoolean(permanentCheckKey, false) ) {
                return null;
            }

            return show();
        }

        private int getPXWithDP(Integer dp) {
            if( dp == null ) {
                return 0;
            }
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
        }
    }
}