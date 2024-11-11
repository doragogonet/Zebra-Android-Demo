package com.zebra.demo.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Paint;
import android.content.Context;

import com.zebra.demo.view.LineChartData;

public class BarChartView extends View {

    private Paint barPaint;
    private LineChartData mData;
    private int barChartRssi;

    private String rssi;

    public BarChartView(Context context) {
        super(context);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.barPaint = new Paint();
        this.barPaint.setColor(Color.GREEN);
        this.barPaint.setStyle(Paint.Style.FILL);
        this.barPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        if (mData == null) {
//            return;
//        }

        //this.drawText(canvas, mData.getName());

        this.drawLine(canvas);

        this.drawText(canvas);
    }

    private void drawLine(Canvas canvas) {
//        double chart_length = (getWidth() - mTextW) / (double) mMaxV;
//        int start_complete_left = mTextW + 10,
//        start_complete_top = 5,
//        start_complete_right = start_complete_left + (int) (chart_length * mData.getRecover_complete())- dip2px(getContext(), 6),
//        start_uncomplete_right = start_complete_left + (int) (chart_length * (mData.getRecover_complete() + mData.getRecover_uncomplete()))- dip2px(getContext(), 6);
//
//        Log.e("TAG", start_complete_left + "..." + start_complete_top + ",,," + start_complete_right + "ds"
//                + start_uncomplete_right);

        //canvas.drawRect(start_complete_left, start_complete_top, start_uncomplete_right, mChartH, barPaint);

        //this.barPaint.setColor(Color.YELLOW);
        //canvas.drawRect(start_complete_left, start_complete_top, start_complete_right, mChartH, barPaint);
        canvas.drawRect(10, 10, this.barChartRssi, 50, this.barPaint);
    }

    private void drawText(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(dip2px(getContext(), 10));
        canvas.drawText(this.rssi, 15, 40, textPaint);
    }

//    private void drawText(Canvas canvas, String text) {
//        int x = getWidth();
//        int y = getHeight();
//
//        Paint textPaint = new Paint();
//        textPaint.setColor(Color.WHITE); 
//        textPaint.setTextSize(dip2px(getContext(), 16));
//
//        textPaint.setTextAlign(Paint.Align.RIGHT);
//        float tX = (x - getFontlength(textPaint, text)) / 2;
//        float tY = (y - getFontHeight(textPaint)) / 2 + getFontLeading(textPaint);
//
//        canvas.drawText(text, mTextW, tY, textPaint);
//    }


    public static float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }


    public static float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }


    public static float getFontLeading(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }

    public void setData(int barChartRssi, String rssi) {

        this.barChartRssi = barChartRssi;
        this.rssi = rssi;
        this.postInvalidate();
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
