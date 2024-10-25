package com.example.ebskk.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.ebskk.R;

//添加以下两行代码即可
import android.annotation.SuppressLint;
@SuppressLint("AppCompatCustomView")
public class VNLineTextView extends TextView {
    private Context context;
    public VNLineTextView(Context context) {
        super(context);
        this.context = context;
    }

    public VNLineTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public VNLineTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    private  int lineColor = Color.TRANSPARENT;
    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        Log.i("abc","color"+color);
        Log.i("abc","darkcolor"+context.getResources().getColor(R.color.dark));
        Log.i("abc","colorDefault"+context.getResources().getColor(R.color.colorDefault));
        if (color == context.getResources().getColor(R.color.colorDefault))
            lineColor = Color.TRANSPARENT;
        else
            lineColor = context.getResources().getColor(R.color.primary);
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        Paint paint = new Paint();
//        paint.setColor(lineColor);
//        paint.setAntiAlias(true);  //去掉锯齿
//        int lineHeight = QMUIDisplayHelper.dpToPx(4);
//        paint.setStrokeWidth((lineHeight));
//        int width = getWidth();
//        int height = getHeight();
//        canvas.drawRoundRect(0,height-lineHeight,width,height,2,2,paint);
//    }
}
