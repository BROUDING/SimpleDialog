package com.brouding.simpledialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
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
    private LinearLayout btnConfirm, btnCancel;
    private CheckBox     checkbox;

    private SharedPreferences mPreferences;
    private Builder builder;

    public enum ButtonAction {
        CONFIRM,
        CANCEL,
        DIALOG_INSIDE,
        DIALOG_OUTSIDE
    }

    private void setTitle(String message) {
        TextView txtTitle = (TextView) transitionsContainer.findViewById(R.id.txt_header);
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.setText(message);
    }

    private void setContent(String message) {
        TextView txtContent = (TextView) transitionsContainer.findViewById(R.id.txt_content);
        txtContent.setText(message);
    }

    private void setBtnCancel() {
        TextView txtCancel = (TextView) btnCancel.findViewById(R.id.text_btn_cancel);

        if (!builder.showProgress)
            btnCancel.setVisibility(View.VISIBLE);

        if( builder.cancelTextColor > 0 )
            txtCancel.setTextColor(ContextCompat.getColor(builder.context, builder.cancelTextColor));

        txtCancel.setText(builder.cancelText);

        btnCancel.setTag(ButtonAction.CANCEL);
        btnCancel.setOnClickListener(this);
    }

    private void setBtnConfirm() {
        TextView txtConfirm = (TextView) btnConfirm.findViewById(R.id.text_btn);
        txtConfirm.setText(builder.confirmText);

        if( builder.confirmTextColor > 0 )
            txtConfirm.setTextColor( ContextCompat.getColor(builder.context, builder.confirmTextColor) );

        btnConfirm.setTag(ButtonAction.CONFIRM);
        btnConfirm.setOnClickListener(this);
    }

	private void setProgressBar(Context context) {
        ImageView progressGif = (ImageView) transitionsContainer.findViewById(R.id.image_loading);
        progressGif.setVisibility(View.VISIBLE);
        Glide.with(context).load(R.raw.loading_default).into(progressGif);
	}

    @UiThread
    private void setLayout(final SimpleDialog dialog) {
        final SimpleDialog.Builder builder = dialog.builder;

        if (builder.showProgress) {
            setContentView(R.layout.dialog_simple_progress);

            transitionsContainer = (ViewGroup) findViewById(R.id.layout_container);
            btnCancel    = (LinearLayout) transitionsContainer.findViewById(R.id.btn_cancel);

            setProgressBar(builder.context);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(transitionsContainer);
                    btnCancel.setVisibility(View.VISIBLE);
                }
            }, 2500);

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

            if( builder.textTitle.length() > 0 ) {
                setTitle(builder.textTitle);
            }

            if( builder.imageId > 0 ) {
                imageGuide.setVisibility(View.VISIBLE);
                imageGuide.setImageResource( builder.imageId );
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
        dialog.setCancelable(builder.cancelable);

        if( builder.dismissListener != null ) {
            dialog.setOnDismissListener(builder.dismissListener);
        }

        if( builder.cancelListener != null ) {
            dialog.setOnCancelListener(builder.cancelListener);
        }

        if( builder.cancelableOnTouchOutside ) {
            LinearLayout layoutDialog = (LinearLayout) transitionsContainer.findViewById(R.id.layout_dialog);
            layoutDialog.setTag(ButtonAction.DIALOG_INSIDE);
            layoutDialog.setOnClickListener(this);

            transitionsContainer.setTag(ButtonAction.DIALOG_OUTSIDE);
            transitionsContainer.setOnClickListener(this);
        }
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
        ButtonAction tag = (ButtonAction) v.getTag();
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

        if( !tag.equals(ButtonAction.DIALOG_INSIDE) && this.isShowing() )
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
        super.dismiss();
    }

    /** An alternate way to define a single callback. */
    public interface SingleButtonCallback {
        void onClick(@NonNull SimpleDialog dialog, @NonNull ButtonAction which);
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public static class Builder {
        protected final Context context;
        protected String textTitle="", textContent="";
        protected String confirmText = null;
        protected String neutralText = null;
        protected String cancelText  = null;
        protected SingleButtonCallback onConfirmCallback = null;
        protected SingleButtonCallback onCancelCallback = null;
        protected OnShowListener showListener;
        protected OnDismissListener dismissListener = null;
        protected OnCancelListener  cancelListener  = null;
        protected boolean cancelable = false;
        protected boolean cancelableOnTouchOutside = false;

        protected boolean showProgress = false;
        protected String  preferenceName = null, permanentCheckKey = null;
        protected int imageId = 0, confirmTextColor=0, cancelTextColor=0;

        protected Object tag;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public final Context getContext() {
            return context;
        }

        public Builder title(@NonNull String message) {
            this.textTitle = message;
            return this;
        }

        public Builder content(@NonNull String message) {
            this.textContent = message;
            return this;
        }

        public Builder btnConfirmText(@NonNull String message) {
            this.confirmText = message;
            return this;
        }

        public Builder btnConfirmTextColor(@NonNull int color) {
            this.confirmTextColor = color;
            return this;
        }

        public Builder btnNeutralText(@NonNull String message) {
            this.neutralText = message;
            return this;
        }

        public Builder btnCancelText(@NonNull String message) {
            this.cancelText = message;
            return this;
        }

        public Builder btnCancelTextColor(@NonNull int color) {
            this.cancelTextColor = color;
            return this;
        }

        public Builder onConfirm(@NonNull SingleButtonCallback callback) {
            this.onConfirmCallback = callback;
            return this;
        }

        public Builder onCancel(@NonNull SingleButtonCallback callback) {
            this.onCancelCallback = callback;
            return this;
        }

        public Builder cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            this.cancelableOnTouchOutside = cancelable;
            return this;
        }

        public Builder cancelableOnTouchOutside(boolean canceledOnTouchOutside) {
            this.cancelableOnTouchOutside = canceledOnTouchOutside;
            return this;
        }

        public Builder guideImage(int imageId) {
            this.imageId = imageId;
            return this;
        }

        public Builder preferenceName(String preferenceName) {
            this.preferenceName = preferenceName;
            return this;
        }

        public Builder permanentCheckKey(String preferenceKey) {
            this.permanentCheckKey = preferenceKey;
            return this;
        }

        public Builder showProgress(boolean showProgress) {
            this.showProgress = showProgress;
            return this;
        }

        public Builder showListener(@NonNull OnShowListener listener) {
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

        public Builder tag(@Nullable Object tag) {
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