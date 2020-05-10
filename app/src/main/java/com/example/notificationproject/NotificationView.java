package com.example.notificationproject;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;

import static android.content.Context.WINDOW_SERVICE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class NotificationView extends LinearLayout {

    private static final int NOT_SHOWED = 0;
    private static final int SHOWED = 1;

    private ImageView mImageView;
    private TextView mTextView;
    private @NotificationState int mState = NOT_SHOWED;
    private WindowManager.LayoutParams mParams;

    @Retention(SOURCE)
    @IntDef({NOT_SHOWED, SHOWED})
    public @interface NotificationState {}

    public NotificationView(Context context) {
        super(context);
        init();
    }

    public NotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.notification_view, this);
        mImageView = findViewById(R.id.image);
        mTextView = findViewById(R.id.text);
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.performClick();
                }
                Log.d("MY_TAG", "MotionEvent");
                return true;
            }
        });

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSPARENT);
        mParams.gravity = Gravity.TOP;
        mParams.windowAnimations = R.style.Animation_NotificationView;
    }

    public void set(int iconResId, @NonNull String text) {
        if (iconResId == -1) {
            mImageView.setVisibility(GONE);
        } else {
            mImageView.setVisibility(VISIBLE);
            mImageView.setImageResource(iconResId);
        }

        mTextView.setText(text);
    }

    public void set(@NonNull String text) {
        set(-1, text);
    }

    public synchronized void show() {
        WindowManager wm = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
        if (mState == NOT_SHOWED && wm != null) {
            wm.addView(this, mParams);
            mState = SHOWED;
        }
    }

    public synchronized void dismiss() {
        WindowManager wm = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
        if (mState == SHOWED && wm != null) {
            wm.removeView(this);
            mState = NOT_SHOWED;
        }
    }

    public void showAndDismissAfter(int delay) {
        show();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, delay);
    }
}
