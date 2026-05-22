package com.lenkeng.udpdemo.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lenkeng.udpdemo.inf.OnPercentChangeListener;

/**
 * @ClassName: DragArcView
 * @Author: chenpengchi
 * @Date: 2026/5/9 0009
 * @Description: *    ┏┓   ┏┓   <-摸摸脑袋，神兽会保佑你的代码
 * *   ┏┛┻━━━┛┻┓
 * *   ┃       ┃
 * *   ┃   ━   ┃
 * *   ┃ ┳┛ ┗┳ ┃
 * *   ┃       ┃
 * *   ┃   ┻   ┃
 * *   ┃       ┃
 * *   ┗━┓   ┏━┛
 * *     ┃   ┃神兽保佑
 * *     ┃   ┃代码无BUG！
 * *     ┃   ┗━━━┓
 * *     ┃       ┣┓
 * *     ┃       ┏┛
 * *     ┗┓┓┏━┳┓┏┛
 * *      ┃┫┫ ┃┫┫
 * *      ┗┻┛ ┗┻┛
 * * ━━━━━━神兽出没━━━━━━
 */
public class DragArcView extends View {
    private int viewWidth;
    private int viewHeight;
    private float centerX;
    private float centerY;
    private float radius;  // 圆环半径（到中心线的距离）
    private RectF arcRect;  // 用于画圆弧的矩形区域
    // 画笔
    private Paint bgRingPaint;
    private Paint textPaint;
    private Paint smallTextPaint;
    private Paint progressRingPaint;  // 新增：彩色进度环画笔
    private Paint dotPaint;  // 新增：光点画笔
    private Paint dotGlowPaint;  // 新增：光点光晕画笔

    // 数据
    private int percent = 0;
    private int animatingPercent = 0;  // 动画过程中实际显示的值
    private boolean isDragging = false;  // 是否正在拖拽
    // 动画
    private ValueAnimator percentAnimator;
    // 放在其他成员变量附近
    private OnPercentChangeListener listener;

    public void setOnPercentChangeListener(OnPercentChangeListener listener) {
        this.listener = listener;
    }
    private void init() {
        // 背景圆环画笔
        bgRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgRingPaint.setStyle(Paint.Style.STROKE);
        bgRingPaint.setStrokeWidth(dp2px(20f));
        bgRingPaint.setColor(Color.parseColor("#3A3A4A"));

        // 彩色进度环画笔
        progressRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressRingPaint.setStyle(Paint.Style.STROKE);
        progressRingPaint.setStrokeWidth(dp2px(20f));
        progressRingPaint.setStrokeCap(Paint.Cap.BUTT);  // 改成 BUTT，去掉圆头

        // 光点画笔（白色小球）
        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setStyle(Paint.Style.FILL);
        dotPaint.setColor(Color.WHITE);

        // 光点光晕画笔（紫色半透明）
        dotGlowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotGlowPaint.setStyle(Paint.Style.FILL);
        dotGlowPaint.setColor(Color.parseColor("#A855F7"));
        dotGlowPaint.setAlpha((int) (0.4f * 255));  // 40% 透明度

        // 大号文字画笔（百分比）
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLUE);
        textPaint.setTextSize(sp2px(36f));
        textPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        // 小号文字画笔（"电量"）
        smallTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        smallTextPaint.setTextAlign(Paint.Align.CENTER);
        smallTextPaint.setColor(Color.parseColor("#888888"));
        smallTextPaint.setTextSize(sp2px(12f));
        // 初始化动画
        percentAnimator = new ValueAnimator();
        percentAnimator.setDuration(400);
        percentAnimator.setInterpolator(new DecelerateInterpolator());
        percentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatingPercent = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
    }
    public DragArcView(Context context) {
        super(context);
        init();
    }

    public DragArcView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        // 获取当前要显示的值（动画中取动画值，拖拽时取实际值）
        int displayPercent = isDragging ? percent : animatingPercent;
        // 1. 画灰色底环
        canvas.drawCircle(centerX, centerY, radius, bgRingPaint);

        // 2. 设置进度环的渐变色（从青色到紫色，从12点钟方向开始）
        SweepGradient sweepGradient = new SweepGradient(
                centerX, centerY,
                new int[]{Color.parseColor("#00D2FF"), Color.parseColor("#A855F7")},
                null
        );
        // 旋转渐变起点到12点钟方向（默认SweepGradient是从3点钟方向开始的）
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.preRotate(-90f, centerX, centerY);
        sweepGradient.setLocalMatrix(matrix);
        progressRingPaint.setShader(sweepGradient);

        // 3. 画彩色进度环
        float sweepAngle = percent * 3.6f;
        canvas.drawArc(arcRect, -90f, sweepAngle, false, progressRingPaint);

        // 4. 画光点
        float dotAngle = -90f + sweepAngle;
        double dotRadians = Math.toRadians(dotAngle);
        float dotRadius = isDragging ? dp2px(12f) : dp2px(10f);  // 拖拽时放大
        float dotGlowRadius = isDragging ? dp2px(16f) : dp2px(14f);  // 光晕也放大

        float dotX = centerX + radius * (float) Math.cos(dotRadians);
        float dotY = centerY + radius * (float) Math.sin(dotRadians);

        // 拖拽时光晕透明度提高
        if (isDragging) {
            dotGlowPaint.setAlpha((int) (0.7f * 255));
        } else {
            dotGlowPaint.setAlpha((int) (0.4f * 255));
        }

        canvas.drawCircle(dotX, dotY, dotGlowRadius, dotGlowPaint);
        canvas.drawCircle(dotX, dotY, dotRadius, dotPaint);

        // 5. 画文字（同样显示动画中的值）
        String percentText = displayPercent + "%";
        Rect textBounds = new Rect();
        textPaint.getTextBounds(percentText, 0, percentText.length(), textBounds);
        float textY = centerY - textBounds.height() / 2f;
        canvas.drawText(percentText, centerX, textY, textPaint);
        canvas.drawText("电量", centerX, centerY + dp2px(18f), smallTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 拖拽时停止正在进行的动画
                if (percentAnimator != null && percentAnimator.isRunning()) {
                    percentAnimator.cancel();
                }

                float dotAngle = -90f + percent * 3.6f;
                double dotRadians = Math.toRadians(dotAngle);
                float dotX = centerX + radius * (float) Math.cos(dotRadians);
                float dotY = centerY + radius * (float) Math.sin(dotRadians);
                float touchToDotDistance = (float) Math.hypot(touchX - dotX, touchY - dotY);

                if (touchToDotDistance <= dp2px(30f)) {
                    isDragging = true;
                    if (listener != null) {
                        listener.onDragStart();
                    }
                    getParent().requestDisallowInterceptTouchEvent(true);
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    double radians = Math.atan2(touchY - centerY, touchX - centerX);
                    double angle = Math.toDegrees(radians);

                    angle = angle + 90f;
                    if (angle < 0) {
                        angle = 360 + angle;
                    }

                    int newPercent = (int) Math.round(angle / 3.6);
                    newPercent = Math.max(0, Math.min(100, newPercent));

                    if (newPercent != percent) {
                        percent = newPercent;
                        // 拖拽时同步更新动画显示值，避免跳变
                        animatingPercent = percent;
                        if (listener != null) {
                            listener.onPercentChanged(percent, true);
                        }
                        invalidate();
                    }
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDragging) {
                    isDragging = false;
                    if (listener != null) {
                        listener.onDragEnd(percent);
                    }
                    getParent().requestDisallowInterceptTouchEvent(false);
                    invalidate();
                    return true;
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        viewWidth = w;
        viewHeight = h;

        // 内置缩进：预留光晕空间（例如 15dp），避免被边界裁剪
        float inset = dp2px(8f);  // 四周缩进距离

        // 考虑已有的 padding（如果有的话）
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        // 实际可用区域 = View尺寸 - 内置缩进 - 原有padding
        int availableWidth = w - (int)(inset * 2) - paddingLeft - paddingRight;
        int availableHeight = h - (int)(inset * 2) - paddingTop - paddingBottom;

        // 圆心位置 = 缩进 + padding + 可用区域的一半
        centerX = inset + paddingLeft + availableWidth / 2f;
        centerY = inset + paddingTop + availableHeight / 2f;

        // 圆环半径
        float ringStrokeWidth = dp2px(20f);
        radius = Math.min(availableWidth, availableHeight) / 2f - ringStrokeWidth / 2f;

        // 画出圆弧的矩形区域
        float left = centerX - radius;
        float top = centerY - radius;
        float right = centerX + radius;
        float bottom = centerY + radius;
        arcRect = new RectF(left, top, right, bottom);
    }
    /**
     * 设置百分比（带动画）
     * @param percent 目标百分比 0-100
     */
    public void setPercent(int percent) {
        int targetPercent = Math.max(0, Math.min(100, percent));
        if (this.percent == targetPercent) {
            return;
        }

        // 如果正在拖拽，不执行动画
        if (isDragging) {
            return;
        }

        this.percent = targetPercent;
        if (listener != null) {
            listener.onPercentChanged(targetPercent, false);
        }
        // 停止当前动画
        if (percentAnimator != null && percentAnimator.isRunning()) {
            percentAnimator.cancel();
        }
        //接口回调
        if (listener != null) {
            listener.onPercentChanged(targetPercent, false);
        }
        // 开始新动画：从当前 animatingPercent 到 targetPercent
        percentAnimator.setIntValues(animatingPercent, targetPercent);
        percentAnimator.start();
    }

    /**
     * 立即设置百分比（无动画）
     */
    public void setPercentImmediately(int percent) {
        int targetPercent = Math.max(0, Math.min(100, percent));
        this.percent = targetPercent;
        this.animatingPercent = targetPercent;

        if (percentAnimator != null && percentAnimator.isRunning()) {
            percentAnimator.cancel();
        }
        invalidate();
    }

    public int getPercent() {
        return percent;
    }

    private float dp2px(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private float sp2px(float sp) {
        return sp * getResources().getDisplayMetrics().scaledDensity;
    }
}
