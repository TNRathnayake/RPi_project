package com.example.driveon;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class SteeringSliderView extends View {
    
    private Paint trackPaint;
    private Paint thumbPaint;
    private LinearGradient gradient;
    
    private float thumbX;
    private float centerX;
    private float trackWidth;
    private float thumbRadius = 50f;
    private float trackHeight = 200f;

    private static final int MIN_ANGLE    = 45;
    private static final int MAX_ANGLE    = 135;
    private static final int CENTER_ANGLE = (MIN_ANGLE + MAX_ANGLE) / 2;
    private int currentAngle = CENTER_ANGLE;
    private boolean isDragging = false;
    
    private OnAngleChangeListener listener;
    
    public interface OnAngleChangeListener {
        void onAngleChanged(int angle);
    }
    
    public SteeringSliderView(Context context) {
        super(context);
        init();
    }
    
    public SteeringSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public SteeringSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trackPaint.setStyle(Paint.Style.FILL);
        
        thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setColor(Color.WHITE);
        thumbPaint.setShadowLayer(4f, 0f, 2f, Color.BLACK);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        centerX = w / 2f;
        trackWidth = w - 100f; // Leave some margin
        thumbX = centerX;
        
        // Create yellow to green gradient (yellow at left, green at right)
        gradient = new LinearGradient(
            50f, h / 2f, // Start at left with margin
            w - 50f, h / 2f, // End at right with margin
            Color.YELLOW,
            Color.GREEN,
            Shader.TileMode.CLAMP
        );
        trackPaint.setShader(gradient);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        float centerY = getHeight() / 2f;
        
        // Draw track
        canvas.drawRect(
            50f,
            centerY - trackHeight / 2f,
            getWidth() - 50f,
            centerY + trackHeight / 2f,
            trackPaint
        );
        
        // Draw thumb
        canvas.drawCircle(thumbX, centerY, thumbRadius, thumbPaint);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (Math.abs(x - thumbX) <= thumbRadius * 1.5f) {
                    isDragging = true;
                    updateThumbPosition(x);
                    return true;
                }
                break;
                
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    updateThumbPosition(x);
                    return true;
                }
                break;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (isDragging) {
                    isDragging = false;
                    snapBackToCenter();
                    return true;
                }
                break;
        }
        
        return super.onTouchEvent(event);
    }
    
    private void updateThumbPosition(float x) {
        // Constrain to track bounds
        float minX = 50f + thumbRadius;
        float maxX = getWidth() - 50f - thumbRadius;
        thumbX = Math.max(minX, Math.min(maxX, x));
        
        // Calculate angle (50 to 150)
        float trackRange = getWidth() - 100f - 2 * thumbRadius;
        float relativePosition = (thumbX - minX) / trackRange;
        currentAngle = Math.round(
                MIN_ANGLE + relativePosition * (MAX_ANGLE - MIN_ANGLE)
        );
        
        // Notify listener
        if (listener != null) {
            listener.onAngleChanged(currentAngle);
        }
        
        invalidate();
    }
    
    private void snapBackToCenter() {
        ValueAnimator animator = ValueAnimator.ofFloat(thumbX, centerX);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            thumbX = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
        
        // Reset angle to center (100)
        currentAngle = CENTER_ANGLE;
        if (listener != null) {
            listener.onAngleChanged(currentAngle);
        }
    }
    
    public void setOnAngleChangeListener(OnAngleChangeListener listener) {
        this.listener = listener;
    }
    
    public int getCurrentAngle() {
        return currentAngle;
    }
} 