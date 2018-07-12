package cn.com.codeteenager.wxrecordbutton;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jiangshuaijie on 2018/7/1.
 */

public class WXRecordButton extends View {
    private final int MODE_NORMAL = 0;
    private final int MODE_RECORD = 1;

    private int buttonMode = MODE_NORMAL;
    private Paint mPaint;
    private float mRadiusBig = 0f;
    private float mRadiusSmall = 0f;
    private float mRadius = 0f;
    private float zoom = 0.8f;
    private int mMax = 0;
    private int mProgress = 0;
    private float angle = 0f;
    private Paint progressPaint;
    private RectF oval = new RectF();
    private GestureListener gestureListener;
    private float stroke = getResources().getDimension(R.dimen.dp6);
    private float dp10 = getResources().getDimension(R.dimen.dp10);
    private ValueAnimator changeModeAnim;
    private Executor singleThreadPool = Executors.newSingleThreadExecutor();
    private boolean isRecording = false;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            gestureListener.onLongPressStart();
            singleThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    isRecording = true;
                    int progress = 0;
                    while (isRecording) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        progress += 50;
                        setProgress(progress);
                    }
                }
            });
            changeButtonMode(MODE_RECORD);
        }
    };

    public WXRecordButton(Context context) {
        super(context);
        init();
    }

    public WXRecordButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WXRecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface GestureListener {
        void onClick();

        void onLongPressStart();

        void onLongPressEnd();

        void onLongPressForceOver();
    }

    public void setGestureListener(GestureListener listener) {
        this.gestureListener = listener;
    }

    public void setMax(int max) {
        this.mMax = max;
    }

    public void setProgress(int progress) {
        this.mProgress = progress;
        if (this.mMax != 0) {
            this.angle = ((float) mProgress) / ((float) mMax) * 360.0f;
        } else {
            this.angle = 0f;
        }
        if (angle >= 360f) {
            this.mProgress = 0;
            this.gestureListener.onLongPressForceOver();
            isRecording = false;
            post(new Runnable() {
                @Override
                public void run() {
                    changeModeAnim.cancel();
                    changeButtonMode(MODE_NORMAL);
                }
            });

        } else {
            postInvalidate();
        }
    }

    private void changeButtonAnim(Float start, Float end) {
        if (changeModeAnim == null || !changeModeAnim.isRunning()) {
            changeModeAnim = ValueAnimator.ofFloat(start, end).setDuration(200);
            changeModeAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float animValue = Float.parseFloat(animation.getAnimatedValue().toString());
                    mRadiusBig = mRadius * (zoom + animValue);
                    mRadiusSmall = mRadius * (zoom - animValue) - dp10;
                    oval = new RectF(mRadius - mRadiusBig + stroke / 2f, mRadius - mRadiusBig + stroke / 2f,
                            mRadius + mRadiusBig - stroke / 2f, mRadius + mRadiusBig - stroke / 2f);
                    postInvalidate();
                }
            });
            changeModeAnim.start();
        }
    }

    private void changeButtonMode(int targetMode) {
        this.buttonMode = targetMode;
        switch (buttonMode) {
            case MODE_NORMAL:
                changeButtonAnim(0.2f, 0f);
                break;
            case MODE_RECORD:
                mProgress = 0;
                angle = 0f;
                changeButtonAnim(0.0f, 0.2f);
                break;
        }
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(Color.GREEN);
        progressPaint.setStrokeWidth(stroke);
        progressPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mRadius = Math.min(getWidth(), getHeight()) / 2f;
        mRadiusBig = zoom * mRadius;
        mRadiusSmall = zoom * mRadius - dp10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(getResources().getColor(R.color.video_gray));
        canvas.drawCircle(mRadius, mRadius, mRadiusBig, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mRadius, mRadius, mRadiusSmall, mPaint);
        if (buttonMode == MODE_RECORD) {
            canvas.drawArc(oval, 270f, angle, false, progressPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.sendEmptyMessageDelayed(0, 500);
                break;
            case MotionEvent.ACTION_UP:
                if (mHandler.hasMessages(0)) {
                    mHandler.removeMessages(0);
                    //short click
                    gestureListener.onClick();
                } else {
                    //long press
                    gestureListener.onLongPressEnd();
                    isRecording = false;
                    changeButtonMode(MODE_NORMAL);
                }
                break;
        }
        return true;
    }

}
