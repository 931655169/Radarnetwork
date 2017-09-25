package com.example.administrator.radarnetwork;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class RadarView extends View {
    private int mCount;
    private float mAngle;
    private float radius;
    private int centerX;
    private int centerY;
    private ArrayList<Item> mItem=new ArrayList<Item>();
    private float maxValue;
    private Paint mainPaint;
    private Paint valuePaint;
    private Paint textPaint;

    public RadarView(Context context) {
        super(context);
        init();
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        getAttrs(context,attrs);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        getAttrs(context,attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        radius = Math.min(h, w) / 2 * 0.9f;
        centerX = w / 2;
        centerY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        init();
        drawPolygon(canvas);
        drawLines(canvas);
        drawText(canvas);
        drawRegion(canvas);
        super.onDraw(canvas);
    }

    private void init() {
        mAngle= (float) (Math.PI * 2 / mCount);
        mCount = Math.min(mItem.size(), mItem.size());
        mainPaint = new Paint();
        mainPaint.setAntiAlias(true);
        mainPaint.setStyle(Paint.Style.STROKE);
        valuePaint = new Paint();
        valuePaint.setAntiAlias(true);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint = new Paint();
        textPaint.setTextSize(20);
        textPaint.setStyle(Paint.Style.FILL);
    }

    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        float r = radius / (mCount - 1);
        for (int i = 1; i < mCount; i++) {
            float curR = r * i;
            path.reset();
            for (int j = 0; j < mCount; j++) {
                if (j == 0) {
                    path.moveTo(centerX + curR, centerY);
                } else {
                    float x = (float) (centerX + curR * Math.cos(mAngle * j));
                    float y = (float) (centerY + curR * Math.sin(mAngle * j));
                    path.lineTo(x, y);
                }
            }
            path.close();
            canvas.drawPath(path, mainPaint);
        }
    }

    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < mCount; i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            float x = (float) (centerX + radius * Math.cos(mAngle * i));
            float y = (float) (centerY + radius * Math.sin(mAngle * i));
            path.lineTo(x, y);
            canvas.drawPath(path, mainPaint);
        }
    }

    private void drawText(Canvas canvas) {
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        for (int i = 0; i < mCount; i++) {
            float x = (float) (centerX + (radius + fontHeight / 2) * Math.cos(mAngle * i));
            float y = (float) (centerY + (radius + fontHeight / 2) * Math.sin(mAngle * i));
            /*
            象限处理（4,3,2,1）
             */
            if (mAngle * i >= 0 && mAngle * i < Math.PI / 2) {
                canvas.drawText(mItem.get(i).getTitles(), x, y, textPaint);
            } else if (mAngle * i >= 3 * Math.PI / 2 && mAngle * i <= Math.PI * 2) {
                canvas.drawText(mItem.get(i).getTitles(), x, y, textPaint);
            } else if (mAngle * i > Math.PI / 2 && mAngle * i <= Math.PI) {
                float dis = textPaint.measureText(mItem.get(i).getTitles());
                canvas.drawText(mItem.get(i).getTitles(), x - dis, y, textPaint);
            } else if (mAngle * i >= Math.PI && mAngle * i < 3 * Math.PI / 2) {
                float dis = textPaint.measureText(mItem.get(i).getTitles());
                canvas.drawText(mItem.get(i).getTitles(), x - dis, y, textPaint);
            }
        }
    }

    private void drawRegion(Canvas canvas) {
        Path path = new Path();
        valuePaint.setAlpha(255);
        for (int i = 0; i < mCount; i++) {
            double percent = mItem.get(i).getData() / maxValue;
            float x = (float) (centerX + radius * Math.cos(mAngle * i) * percent);
            float y = (float) (centerY + radius * Math.sin(mAngle * i) * percent);
            if (i == 0) {
                path.moveTo(x, centerY);
            } else {
                path.lineTo(x, y);
            }
            canvas.drawCircle(x, y, 10, valuePaint);
        }
        valuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, valuePaint);
        valuePaint.setAlpha(127);
        valuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, valuePaint);
    }
    public static class Item{
        private String mTitles;
        private double mData;

        public String getTitles() {
            return mTitles;
        }

        public void setTitles(String mTitles) {
            this.mTitles = mTitles;
        }

        public double getData() {
            return mData;
        }

        public void setData(double mData) {
            this.mData = mData;
        }
    }
    public void setItem(ArrayList<Item> item){
        this.mItem=item;
    }
    /*
    设置属性
     */
    private void getAttrs(Context context,AttributeSet attributeSet){
        TypedArray typedArray=context.obtainStyledAttributes(attributeSet,R.styleable.RadarView);
        maxValue=typedArray.getFloat(R.styleable.RadarView_maxValue,100);
        mainPaint.setColor(typedArray.getColor(R.styleable.RadarView_MainPaintColor,Color.GRAY));
        valuePaint.setColor(typedArray.getColor(R.styleable.RadarView_ValuePaintColor,Color.BLUE));
        textPaint.setColor(typedArray.getColor(R.styleable.RadarView_TextPaintColor,Color.BLACK));
        typedArray.recycle();
    }
    /*
    实现接口
     */
    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public void setMainPaintColor(int color) {mainPaint.setColor(color);}

    public void setValuePaintColor(int color) {
        valuePaint.setColor(color);
    }

    public void setTextPaintColor(int color) {
        textPaint.setColor(color);
    }
}
