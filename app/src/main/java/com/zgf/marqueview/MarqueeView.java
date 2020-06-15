package com.zgf.marqueview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

/**
 * 实现滚动播放文字的效果
 *
 * 自己绘制文字内容，利用canvas.translate()方法来实现
 * 文字样式需要自己绘制
 */
public class MarqueeView extends View {
    private static final int DEFAULT_SIZE = 300;
    private static final int DEFAULT_TIME = 6000; // 3S
    public static final int MODE_ONCE = 0;
    public static final int MODE_FOREVER = -1;

    private Paint paint;
    private String text;
    private int time = DEFAULT_TIME;
    private float translate = 0;
    private float textLength;
    private int mode = MODE_ONCE;

    private ValueAnimator animator;
    private boolean isRun = false;
    private boolean isChanged = false;

    private ProgressListenerAdapter listener;

    public MarqueeView(Context context) {
        super(context);
        init();
    }

    public MarqueeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarqueeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);
        paint.setStrokeWidth(10);
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
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int resultWidth;
        int resultHeight;
        if (widthMode == MeasureSpec.EXACTLY) {
            resultWidth = width;
        } else {
            resultWidth = DEFAULT_SIZE;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            resultHeight = height;
        } else {
            resultHeight = DEFAULT_SIZE;
        }

        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        if (!TextUtils.isEmpty(text)) {
            canvas.translate(-translate, 0);
            canvas.drawText(text, 0, text.length(), paint);
        }
    }

    private void getTextLength() {
        textLength = paint.measureText(this.text);
    }

    public void setTime(int time) {
        if (time < 0) {
            time = DEFAULT_TIME;
        }
        this.time = time;
    }

    public void setText(String text) {
        this.text = text;
        getTextLength();
    }

    public void setMode(int mode) {
        this.mode = mode;
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
        float start = -getWidth();
        float end = textLength + 30;
        animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(time);
        animator.setRepeatCount(this.mode);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                translate = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
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
