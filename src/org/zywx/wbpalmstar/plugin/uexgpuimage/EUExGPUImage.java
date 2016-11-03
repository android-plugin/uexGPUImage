package org.zywx.wbpalmstar.plugin.uexgpuimage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.widget.RelativeLayout;

import org.zywx.wbpalmstar.base.ACEImageLoader;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.base.listener.ImageLoaderListener;
import org.zywx.wbpalmstar.engine.DataHelper;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IF1977Filter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFAmaroFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFBrannanFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFEarlybirdFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFHefeFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFHudsonFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFInkwellFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFLomoFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFLordKelvinFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFNashvilleFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFRiseFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFSierraFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFSutroFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFToasterFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.filter.IFWaldenFilter;
import org.zywx.wbpalmstar.plugin.uexgpuimage.vo.OpenViewVO;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * 封装自：
 * https://github.com/sangmingming/android-instagram-filter
 */
public class EUExGPUImage extends EUExBase {

    private static final String BUNDLE_DATA = "data";

    private static final int REQUEST_CODE =100;

    private int mCallbackId=-1;
    GPUImageView gpuImageView;
    public EUExGPUImage(Context context, EBrowserView eBrowserView) {
        super(context, eBrowserView);
    }

    @Override
    protected boolean clean() {
        return false;
    }

    public void open(String[] params){
        OpenViewVO openViewVO = DataHelper.gson.fromJson(params[0],OpenViewVO.class);
        if (params.length>1){
            mCallbackId= Integer.parseInt(params[1]);
        }
        Intent intent=new Intent(mContext,ImageFilterActivity.class);
        intent.putExtra(ImageFilterActivity.KEY_IMAGE_PATH, BUtility.getRealPathWithCopyRes(mBrwView,openViewVO.getPath()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void openView(String[] params){
        OpenViewVO openViewVO = DataHelper.gson.fromJson(params[0],OpenViewVO.class);
        gpuImageView=new GPUImageView(mContext);
        RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(openViewVO.getW(), openViewVO.getH());
        lp.leftMargin= openViewVO.getX();
        lp.topMargin= openViewVO.getY();
        gpuImageView.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        gpuImageView.setFilter(getFilterWithType(openViewVO.getType()));
        ACEImageLoader.getInstance().getBitmap(openViewVO.getPath(), new ImageLoaderListener() {
            @Override
            public void onLoaded(Bitmap bitmap) {
                gpuImageView.setImage(bitmap);
            }
        });
        addViewToCurrentWindow(gpuImageView,lp);

    }

    private GPUImageFilter getFilterWithType(String type){
        if ("1977".equalsIgnoreCase(type)){
            return new IF1977Filter(mContext);
        }else if ("Amaro".equalsIgnoreCase(type)){
            return new IFAmaroFilter(mContext);
        }else if ("Brannan".equalsIgnoreCase(type)){
            return new IFBrannanFilter(mContext);
        }else if ("Earlybird".equalsIgnoreCase(type)){
            return new IFEarlybirdFilter(mContext);
        }else if ("Hefe".equalsIgnoreCase(type)){
            return new IFHefeFilter(mContext);
        }else if ("Hudson".equalsIgnoreCase(type)){
            return new IFHudsonFilter(mContext);
        }else if ("InkWell".equalsIgnoreCase(type)){
            return new IFInkwellFilter(mContext);
        }else if ("Lomo".equalsIgnoreCase(type)){
            return new IFLomoFilter(mContext);
        }else if ("LordKelvin".equalsIgnoreCase(type)){
            return new IFLordKelvinFilter(mContext);
        }else if ("Nash".equalsIgnoreCase(type)){
            return new IFNashvilleFilter(mContext);
        }else if ("Rise".equalsIgnoreCase(type)){
            return new IFRiseFilter(mContext);
        }else if ("Sierra".equalsIgnoreCase(type)){
            return new IFSierraFilter(mContext);
        }else if ("Sutro".equalsIgnoreCase(type)){
            return new IFSutroFilter(mContext);
        }else if ("Toaster".equalsIgnoreCase(type)){
            return new IFToasterFilter(mContext);
        }else if ("Walden".equalsIgnoreCase(type)){
            return new IFWaldenFilter(mContext);
        }
        return null;
    }

    public void closeView(String[] params){
        if (gpuImageView!=null){
            removeViewFromCurrentWindow(gpuImageView);
        }
    }

    @Override
    public void onHandleMessage(Message message) {
        if (message == null) {
            return;
        }
        Bundle bundle = message.getData();
        switch (message.what) {

            default:
                super.onHandleMessage(message);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode== REQUEST_CODE){
            if (resultCode== Activity.RESULT_OK){
                callbackToJs(mCallbackId,false,0,intent.getStringExtra("path"));
            }else{
                callbackToJs(mCallbackId,false,1);
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

}
