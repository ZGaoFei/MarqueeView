package com.zgf.marqueview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 实现滚动播放文字的效果
 *
 * 在FrameLayout里面加入一个TextView来实现
 * 通过TextView的translationX动画来实现
 * 文字样式交给TextView来处理
 *
 * 1、默认MarqueeLayout没有包含子View会自动生成一个TextView
 * 2、包含一个TextView
 */
public class MarqueeLayout extends FrameLayout {
    private static final int DEFAULT_TIME = 6000; // 6S
    public static final int MODE_ONCE = 0;
    public static final int MODE_FOREVER = -1;

    private TextView textView;
    private int textLength;

    private int mode = MODE_ONCE;

    private ObjectAnimator animator;
    private int time = DEFAULT_TIME;

    private boolean isRun;
    private boolean isChanged;

    private String text;

    private ProgressListenerAdapter listener;

    public MarqueeLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public MarqueeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarqueeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        isChanged = true;
        if (isRun) {
            run();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        if (childCount > 1) {
            throw new IllegalArgumentException("marquee layout must only one child view");
        }
        if (childCount == 0) {
            createTextView();
            addView(textView);
            textView.setText(this.text);
        } else {
            View child = getChildAt(0);
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                textView.setMaxLines(1);
                this.textView = textView;
                this.text = textView.getText().toString();
            } else {
                throw new IllegalArgumentException("marquee layout must only one child view is TextView");
            }
        }
        setTextViewWidth(this.text);
    }

    private void createTextView() {
        textView = new TextView(getContext());
        textView.setMaxLines(1);
    }

    public TextView getTextView() {
        return textView;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    private int getLength(String text) {
        if (textView == null) {
            return 0;
        }
        if (TextUtils.isEmpty(text)) {
            text = "";
        }
        int padding = textView.getCompoundDrawablePadding();
        Drawable[] drawables = textView.getCompoundDrawables();
        int allDrableWidth = 0;
        if (drawables != null && drawables.length > 0) {
            for (int i = 0; i < drawables.length; i++) {
                Drawable drawable = drawables[i];
                if (drawable != null) {
                    Rect rect = drawables[i].getBounds();
                    int left = rect.left;
                    int right = rect.right;
                    allDrableWidth += (right - left);
                }
            }
        }
        Paint paint = textView.getPaint();
        float length = paint.measureText(text);
        return (int) length + allDrableWidth + padding;
    }

    private void setTextViewWidth(String text) {
        if (textView == null) {
            return;
        }
        ViewGroup.LayoutParams params = textView.getLayoutParams();
        textLength = getLength(text);
        params.width = textLength;
        textView.setLayoutParams(params);
    }

    public void setTime(int time) {
        if (time < 0) {
            time = DEFAULT_TIME;
        }
        this.time = time;
    }

    public void stop() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
            animator = null;
        }
    }

    public void run() {
        isRun = true;
        if (!isChanged) {
            return;
        }

        if (animator != null && animator.isRunning()) {
            // 这里限制重复点击开始
//            return;

            // 不限制重复点击开始，每次点击动画重新开始
            animator.cancel();
            animator = null;
        }
        int layoutWidth = getWidth();
        animator = ObjectAnimator.ofFloat(textView, "translationX", layoutWidth, -textLength);
        animator.setDuration(time);
        animator.setRepeatCount(this.mode);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if (listener != null) {
                    listener.onAnimationCancel();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (listener != null) {
                    listener.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
                if (listener != null) {
                    listener.onAnimationRepeat();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (listener != null) {
                    listener.onAnimationStart();
                }
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
                if (listener != null) {
                    listener.onAnimationPause();
                }
            }

            @Override
            public void onAnimationResume(Animator animation) {
                super.onAnimationResume(animation);
                if (listener != null) {
                    listener.onAnimationResume();
                }
            }
        });
        animator.start();
    }

    public void setListener(ProgressListenerAdapter listener) {
        this.listener = listener;
    }
}
