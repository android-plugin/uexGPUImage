package org.zywx.wbpalmstar.plugin.uexgpuimage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

import static android.R.attr.path;

/**
 * Created by ylt on 2016/11/1.
 */

public class ImageFilterActivity extends Activity {

    private static final int PREVIEW_WIDTH_DP = 100; //预览图的宽度
    public static final String KEY_IMAGE_PATH = "image_path";

    private int mSelectIndex = 0;
    private SquareGpuImageView mGpuImageView;
    private LinearLayout mContainerLayout;
    private String mImagePath;
    int currentPreviewPosition = 0;//当前处理第几个预览
    TextView mCancelTV;
    TextView mSaveTV;
    String mFilterName = "1977";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(EUExUtil.getResLayoutID("plugin_uexgpuimage_imagefilter_activity_layout"));
        handleIntent();
        initViews();
        processImages();
        loadPreviews();
    }

    private void initViews() {
        mGpuImageView = (SquareGpuImageView) findViewById(EUExUtil.getResIdID("main_gpu_imageview"));
        mGpuImageView.setBackgroundColor(Color.TRANSPARENT);
        mGpuImageView.setDrawingCacheBackgroundColor(Color.TRANSPARENT);
        mContainerLayout = (LinearLayout) findViewById(EUExUtil.getResIdID("container_layout"));
        mCancelTV = (TextView) findViewById(EUExUtil.getResIdID("cancel"));
        mSaveTV = (TextView) findViewById(EUExUtil.getResIdID("save"));
        mCancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        mSaveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = ProgressDialog.show(ImageFilterActivity.this, null, "正在保存，请稍等");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String path = saveImageToGallery(ImageFilterActivity.this, mGpuImageView.capture());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.cancel();
                                    Intent intent = new Intent();
                                    intent.putExtra("path", path);
                                    setResult(RESULT_OK, intent);
                                    finish();

                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }

    private void processImages() {
        mGpuImageView.setImage(new File(mImagePath));
    }


    private void loadPreviews() {
        final GPUImageFilterTools.FilterList filterList = GPUImageFilterTools.getFilterList();
        final List<GPUImageFilterTools.FilterType> filterTypes = filterList.filters;
        final List<GPUImageFilter> imageFilters = new ArrayList<GPUImageFilter>();
        for (int i = 0; i < filterTypes.size(); i++) {
            final LinearLayout previewLayout = (LinearLayout) getLayoutInflater().inflate(EUExUtil.getResLayoutID("plugin_uexgpuimage_preview_item_layout"),
                    mContainerLayout, false);
            ImageView imageView = (ImageView) previewLayout.findViewById(EUExUtil.getResIdID("preview_image"));
            TextView filterName = (TextView) previewLayout.findViewById(EUExUtil.getResIdID("filter_name"));
            filterName.setText(filterList.names.get(i));
            final GPUImageFilter filter = GPUImageFilterTools.createFilterForType(ImageFilterActivity.this, filterTypes
                    .get(i));
            imageFilters.add(filter);
            final int finalI = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mSelectIndex = finalI;
                    mFilterName = filterList.names.get(finalI);
                    mGpuImageView.setFilter(filter);
                    if (finalI > 1) {
                        ((HorizontalScrollView) mContainerLayout.getParent()).smoothScrollTo((finalI - 1) * previewLayout
                                .getWidth(), 0);
                    }
                }
            });
            mContainerLayout.addView(previewLayout);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int thumbWidth = EUExUtil.dipToPixels(PREVIEW_WIDTH_DP);
                final Bitmap bitmap = BUtility.createBitmapWithPath(mImagePath, thumbWidth, thumbWidth);

                GPUImage.getBitmapForMultipleFilters(bitmap, imageFilters, new GPUImage.ResponseListener<Bitmap>() {
                    @Override
                    public void response(final Bitmap item) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView previewImg = (ImageView) mContainerLayout.getChildAt(currentPreviewPosition).findViewById(EUExUtil.getResIdID("preview_image"));
                                previewImg.setImageBitmap(item);
                                currentPreviewPosition++;
                            }
                        });
                    }

                });
            }
        }).start();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            mImagePath = intent.getStringExtra(KEY_IMAGE_PATH);
            if (mImagePath == null) {
                finish();
            }
        }
    }

    public String saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File originFile = new File(mImagePath);

        String fileName = originFile.getName().substring(0, originFile.getName().lastIndexOf(".")) + "_" + mFilterName + ".jpg";
        File file = new File(originFile.getParentFile(), fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        return file.getAbsolutePath();
    }

}
