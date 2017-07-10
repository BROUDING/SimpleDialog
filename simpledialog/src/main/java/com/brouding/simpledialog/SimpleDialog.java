package com.brouding.simpledialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.transitionseverywhere.TransitionManager;

import static android.content.Context.MODE_PRIVATE;

// FIXME: NAME... SimpleDialog?  Because it has only 2 options that 'MUST HAVE OPTIONS'
public class SimpleDialog extends Dialog implements View.OnClickListener {
    private ViewGroup    transitionsContainer;
    private LinearLayout btnConfirm, btnCancel, layoutCancel;
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

    private void setContent(String message) {
        TextView txtContent = (TextView) transitionsContainer.findViewById(R.id.txt_content);
        txtContent.setText(message);
    }

    private void setBtnConfirm() {
        TextView txtConfirm = (TextView) btnConfirm.findViewById(R.id.text_btn);
        txtConfirm.setText(builder.confirmText);

        if( builder.confirmTextColor > 0 )
            txtConfirm.setTextColor( ContextCompat.getColor(builder.context, builder.confirmTextColor) );

        if( builder.isConfirmTextBold )
            txtConfirm.setTypeface(null, Typeface.BOLD);

        btnConfirm.setTag(BtnAction.CONFIRM);
        btnConfirm.setOnClickListener(this);
    }

    private void setBtnCancel() {
        TextView txtCancel = (TextView) btnCancel.findViewById(R.id.text_btn_cancel);

        if( !builder.showProgress )
            btnCancel.setVisibility(View.VISIBLE);

        if( builder.cancelTextColor > 0 )
            txtCancel.setTextColor(ContextCompat.getColor(builder.context, builder.cancelTextColor));

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
        Glide.with(context).load(customProgressGIF==null ? R.raw.loading_default : customProgressGIF).into(progressGif);
	}

    @UiThread
    private void setLayout(final SimpleDialog dialog) {
        final SimpleDialog.Builder builder = dialog.builder;

        if (builder.showProgress) {
            setContentView(R.layout.dialog_simple_progress);

            transitionsContainer = (ViewGroup) findViewById(R.id.layout_container);
            layoutCancel = (LinearLayout) transitionsContainer.findViewById(R.id.layout_cancel);
            btnCancel    = (LinearLayout) transitionsContainer.findViewById(R.id.btn_cancel);

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
        } else {
            setContentView(R.layout.dialog_simple_default);

            transitionsContainer = (ViewGroup) findViewById(R.id.layout_container);

            ImageView imageGuide = (ImageView) transitionsContainer.findViewById(R.id.image_guide);
            View gapView = transitionsContainer.findViewById(R.id.view_gap);
            btnConfirm = (LinearLayout) transitionsContainer.findViewById(R.id.btn_okay);
            btnCancel  = (LinearLayout) transitionsContainer.findViewById(R.id.btn_cancel);
            LinearLayout btnCheck = (LinearLayout) transitionsContainer.findViewById(R.id.btn_check_permanent);
            checkbox   = (CheckBox)     transitionsContainer.findViewById(R.id.check_permanent);
            btnCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkbox.setChecked( !checkbox.isChecked() );
                }
            });

            if( builder.textTitle != null ) {
                setTitle(builder.textTitle);
            }

            if( builder.guideImageId > 0 ) {
                imageGuide.setVisibility(View.VISIBLE);
                imageGuide.setImageResource( builder.guideImageId );
            }

            if( builder.cancelText != null ) {
                gapView  .setVisibility(View.GONE);
                btnCheck .setVisibility(View.GONE);
                setBtnCancel();
            }

            if( builder.permanentCheckKey!=null ) {
                btnCheck .setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
            }

            if( builder.confirmText != null ) {
                setBtnConfirm();
            }
        }

        setContent(builder.textContent);
        dialog.setCancelable(builder.isCancelable);

        if( builder.dismissListener != null ) {
            dialog.setOnDismissListener(builder.dismissListener);
        }

        if( builder.cancelListener != null ) {
            dialog.setOnCancelListener(builder.cancelListener);
        }

        if( builder.isCancelableOnTouchOutside ) {
            LinearLayout layoutDialog = (LinearLayout) transitionsContainer.findViewById(R.id.layout_dialog);
            layoutDialog.setTag(BtnAction.DIALOG_INSIDE);
            layoutDialog.setOnClickListener(this);

            transitionsContainer.setTag(BtnAction.DIALOG_OUTSIDE);
            transitionsContainer.setOnClickListener(this);
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
                permanentCheck();

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

        if( !tag.equals(BtnAction.DIALOG_INSIDE) && this.isShowing() )
            dismiss();
    }

    private void permanentCheck() {
        if( builder.permanentCheckKey!=null && checkbox.isChecked() ) {
            SharedPreferences.Editor edit = mPreferences.edit();
            edit.putBoolean(builder.permanentCheckKey, true);
            edit.apply();
        }
    }

    // TODO: 1. preferenceKey is present, but preferenceName is not present
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

    /** An alternate way to define a single callback. */
    public interface BtnCallback {
        void onClick(@NonNull SimpleDialog dialog, @NonNull BtnAction which);
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public static class Builder {
        protected final Context  context;
        protected BtnCallback    onConfirmCallback = null, onCancelCallback = null;
        protected OnShowListener showListener;
        protected OnDismissListener dismissListener = null;
        protected OnCancelListener  cancelListener  = null;
        protected boolean isCancelable = false,
                          isCancelableOnTouchOutside = false,
                          showProgress = false,
                          isTitleBold  = false,
                          isConfirmTextBold = false,
                          isCancelTextBold  = false;
        protected int     btnCancelShowTime = 2500,
                          guideImageId = 0,
                          confirmTextColor = 0,
                          cancelTextColor  = 0;
        protected String  textTitle   = null,
                          textContent = null,
                          confirmText = null,
                          cancelText  = null,
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

        public Builder setTitle(String message, boolean isBold) {
            this.textTitle = message;
            this.isTitleBold = isBold;
            return this;
        }

        public Builder setContent(@NonNull String message) {
            this.textContent = message;
            return this;
        }

        public Builder setBtnConfirmText(@NonNull String message, boolean isBold) {
            this.confirmText = message;
            this.isConfirmTextBold = isBold;
            return this;
        }

        public Builder setBtnConfirmTextColor(int color) {
            this.confirmTextColor = color;
            return this;
        }

        public Builder onConfirm(BtnCallback callback) {
            this.onConfirmCallback = callback;
            return this;
        }

        public Builder setBtnCancelText(String message, boolean isBold) {
            this.cancelText = message;
            this.isCancelTextBold = isBold;
            return this;
        }

        public Builder setBtnCancelShowTime(int btnCancelShowTime) {
            this.btnCancelShowTime = btnCancelShowTime;
            return this;
        }

        public Builder setBtnCancelTextColor(int color) {
            this.cancelTextColor = color;
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

        public Builder setGuideImage(int imageId) {
            this.guideImageId = imageId;
            return this;
        }

        public Builder setPreferenceName(String preferenceName) {
            this.preferenceName = preferenceName;
            return this;
        }

        public Builder setPermanentCheckKey(String preferenceKey) {
            this.permanentCheckKey = preferenceKey;
            return this;
        }

        public Builder showProgress(boolean showProgress) {
            this.showProgress = showProgress;
            return this;
        }

        public Builder setProgressGIF(Object customProgressGIF) {
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
    }
}