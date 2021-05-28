package com.zhxumao.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

/**
 * Created by zhxumao on 2021-05-27 12:09
 */
public class ZDampingNestedScrollView extends NestedScrollView {

    //Y轴本次点击的位置
    private int currentY;
    //Y轴本次down点击的位置
    private int startY = 0;
    //Y轴上次move事件点击的位置
    private int lastY;
    //Y轴上两次move事件之间的偏移量
    private int offset;
    //Y轴上两次move事件之间的偏移量*系数
    private int curOffset;
    private View childView;
    //childView原来上一次down点击的位置
    private Rect childRect = new Rect();

    public ZDampingNestedScrollView(Context context) {
        this(context, null);
    }

    public ZDampingNestedScrollView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZDampingNestedScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFillViewport(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            childView = getChildAt(0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) event.getY();
                childRect.set(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());
                break;
            case MotionEvent.ACTION_MOVE:
                currentY = (int) event.getY();
                offset = currentY - lastY;
                curOffset = (int) (offset * 0.35);
                lastY = currentY;
                if (currentY != startY && 0 < Math.abs(offset) && Math.abs(offset) < 200) {
                    childView.layout(childView.getLeft(), childView.getTop() + curOffset, childView.getRight(), childView.getBottom() + curOffset);
                }
                break;
            case MotionEvent.ACTION_UP:
                upDownMoveAnimation();
                childView.layout(childRect.left, childRect.top, childRect.right, childRect.bottom);
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return super.onInterceptTouchEvent(event);
    }

    // 初始化上下回弹的动画效果
    private void upDownMoveAnimation() {
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,
                childView.getTop(), childRect.top);
        animation.setDuration(600);
        animation.setFillAfter(true);
        //设置阻尼动画效果
        animation.setInterpolator(new DampInterpolator());
        childView.setAnimation(animation);
    }

    public static class DampInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            //没看过源码，猜测是input是时间（0-1）,返回值应该是进度（0-1）
            //先快后慢，为了更快更慢的效果，多乘了几次，现在这个效果比较满意
            return 1 - (1 - input) * (1 - input) * (1 - input) * (1 - input) * (1 - input);
        }
    }

}
