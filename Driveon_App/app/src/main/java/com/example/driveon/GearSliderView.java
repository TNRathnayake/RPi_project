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

public class GearSliderView extends View {
    
    private Paint trackPaint;
    private Paint thumbPaint;
    private LinearGradient gradient;
    
    private float thumbY;
    private float centerY;
    private float trackHeight;
    private float thumbRadius = 50f;
    private float trackWidth = 200f;
    
    private int currentValue = 0;
    private boolean isDragging = false;
    
    private OnValueChangeListener listener;
    
    public interface OnValueChangeListener {
        void onValueChanged(int value);
    }
    
    public GearSliderView(Context context) {
        super(context);
        init();
    }
    
    public GearSliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public GearSliderView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        
        centerY = h / 2f;
        trackHeight = h - 100f; // Leave some margin
        thumbY = centerY;
        
        // Create red to blue gradient (red at top, blue at bottom)
        gradient = new LinearGradient(
            w / 2f, 50f, // Start at top with margin
            w / 2f, h - 50f, // End at bottom with margin
            Color.RED,
            Color.BLUE,
            Shader.TileMode.CLAMP
        );
        trackPaint.setShader(gradient);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        float centerX = getWidth() / 2f;
        
        // Draw track
        canvas.drawRect(
            centerX - trackWidth / 2f,
            50f,
            centerX + trackWidth / 2f,
            getHeight() - 50f,
            trackPaint
        );
        
        // Draw thumb
        canvas.drawCircle(centerX, thumbY, thumbRadius, thumbPaint);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (Math.abs(y - thumbY) <= thumbRadius * 1.5f) {
                    isDragging = true;
                    updateThumbPosition(y);
                    return true;
                }
                break;
                
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    updateThumbPosition(y);
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
    
    private void updateThumbPosition(float y) {
        // Constrain to track bounds
        float minY = 50f + thumbRadius;
        float maxY = getHeight() - 50f - thumbRadius;
        thumbY = Math.max(minY, Math.min(maxY, y));
        
        // Calculate value (-100 to +100)
        float trackRange = getHeight() - 100f - 2 * thumbRadius;
        float relativePosition = (thumbY - minY) / trackRange;
        currentValue = Math.round((relativePosition - 0.5f) * 200f);
        
        // Notify listener
        if (listener != null) {
            listener.onValueChanged(currentValue);
        }
        
        invalidate();
    }
    
    private void snapBackToCenter() {
        ValueAnimator animator = ValueAnimator.ofFloat(thumbY, centerY);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            thumbY = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
        
        // Reset value to 0
        currentValue = 0;
        if (listener != null) {
            listener.onValueChanged(currentValue);
        }
    }
    
    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.listener = listener;
    }
    
    public int getCurrentValue() {
        return currentValue;
    }
} 