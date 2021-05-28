package com.zhxumao.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/**
 * Created by zhxumao on 2021-05-27 23:09
 */
public class ZoomNestScrollView extends NestedScrollView {

    private final static String TAG_ZOOM_BG = "zoom_bg";

    private View zoomView;
    private Rect zoomViewSrcRect = new Rect();

    private ViewGroup.LayoutParams zoomViewLp;

    private int startY;
    private int currentY;
    private int lastY;
    private int offset;

    public ZoomNestScrollView(Context context) {
        this(context, null);
    }

    public ZoomNestScrollView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomNestScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        zoomView = findViewWithTag(TAG_ZOOM_BG);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (zoomView == null) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                lastY = startY;
                zoomViewSrcRect.set(zoomView.getLeft(), zoomView.getTop(), zoomView.getRight(), zoomView.getBottom());
                zoomViewLp = zoomView.getLayoutParams();
                break;
            case MotionEvent.ACTION_MOVE:
                currentY = (int) ev.getY();
                offset = currentY - lastY;
                lastY = currentY;
                if (((isVisibleLocal(zoomView, true) && offset > 0) || (zoomView.getBottom() > zoomViewSrcRect.bottom))) {
                    zoomViewLp.height = (int) (zoomViewLp.height + offset * 0.45);
                    zoomView.setLayoutParams(zoomViewLp);
                }
                if ((zoomView.getBottom() > zoomViewSrcRect.bottom)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isVisibleLocal(zoomView, true)) {
                    countDownTimer.cancel();
                    countDownTimer.start();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private final CountDownTimer countDownTimer = new CountDownTimer(600, 10) {
        @Override
        public void onTick(long millisUntilFinished) {
            zoomViewLp = zoomView.getLayoutParams();
            zoomViewLp.height = zoomViewLp.height - (int) ((zoomViewLp.height - zoomViewSrcRect.bottom) * ((float) (600 - millisUntilFinished) / 600));
            zoomView.setLayoutParams(zoomViewLp);
        }

        @Override
        public void onFinish() {
            zoomViewLp = zoomView.getLayoutParams();
            zoomViewLp.height = zoomViewSrcRect.bottom;
            zoomView.setLayoutParams(zoomViewLp);
        }
    };

    /**
     * 判断View是否可见
     *
     * @param target   View
     * @param judgeAll 为 true时,判断 View 全部可见才返回 true
     * @return boolean
     */
    public static boolean isVisibleLocal(View target, boolean judgeAll) {
        Rect rect = new Rect();
        target.getLocalVisibleRect(rect);
        if (judgeAll) {
            return rect.top == 0;
        } else {
            return rect.top >= 0;
        }
    }
}
