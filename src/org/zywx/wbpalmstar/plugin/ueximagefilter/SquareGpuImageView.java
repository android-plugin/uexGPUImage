package org.zywx.wbpalmstar.plugin.ueximagefilter;

import android.content.Context;
import android.util.AttributeSet;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by ylt on 2016/11/1.
 */

public class SquareGpuImageView extends GPUImageView {
    public SquareGpuImageView(Context context) {
        super(context);
    }

    public SquareGpuImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        int childWidthSize = getMeasuredWidth();
        //高度和宽度一样
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

     }
}
