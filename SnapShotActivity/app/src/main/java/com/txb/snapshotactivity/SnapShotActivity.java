package com.txb.snapshotactivity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.txb.snapshotactivity.service.FileService;

/**
 * 获取webView快照与屏幕的截屏
 */
public class SnapShotActivity extends Activity {

    private static final String TAG = "SnapShotActivity";
    private Bitmap bmp = null;
    private WebView webView = null;
    private ImageView image = null;
    private FileService fileService = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snap_shot);

        //初始化view
        Button btnWebViewSnapShot = (Button) findViewById(R.id.btnWebViewSnapShot);
        Button btnSreenShot = (Button) findViewById(R.id.btnSreenShot);
        Button btnWebViewSreen = (Button) findViewById(R.id.btnWebViewSreen);
        image = (ImageView) findViewById(R.id.imageView);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setDrawingCacheEnabled(true);
        //设置不跳转到浏览器页面
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl("http://www.baidu.com");

        fileService = new FileService(this);

        //获取webView快照
        btnWebViewSnapShot.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                bmp = captureWebView(webView);
                Log.i(TAG, "获取快照");
                saveImage();
            }
        });

        //获取截屏
        btnSreenShot.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                bmp = captureScreen(SnapShotActivity.this);
                Log.i(TAG, "获取截屏");
                saveImage();
            }
        });

        //获取webView显示区域的截图
        btnWebViewSreen.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                bmp = captureWebViewVisibleSize(webView);
                Log.i(TAG, "获取webView显示区域的截图");
                saveImage();
            }
        });
    }

    private void saveImage() {
        System.out.println("bmpWidth=" + bmp.getWidth());
        System.out.println("bmpHeight=" + bmp.getHeight());
        image.setBackground(new BitmapDrawable(getResources(),bmp));
        String fileName = fileService.saveBitmapToSDCard("" + System.currentTimeMillis() + ".png", bmp);
        Toast.makeText(getApplicationContext(), "文件" + fileName + "保存成功！", Toast.LENGTH_SHORT).show();
    }


    /**
     * 截取webView可视区域的截图
     *
     * @param webView 前提：WebView要设置webView.setDrawingCacheEnabled(true);
     * @return
     */
    private Bitmap captureWebViewVisibleSize(WebView webView) {
        Bitmap bmp = webView.getDrawingCache(true);
        return bmp;
    }

    /**
     * 截取webView快照(webView加载的整个内容的大小)
     *
     * @param webView
     * @return
     */
    private Bitmap captureWebView(WebView webView) {
        Picture snapShot = webView.capturePicture();

        Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(), snapShot.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        snapShot.draw(canvas);
        return bmp;
    }

    /**
     * 截屏
     *
     * @param context
     * @return
     */
    private Bitmap captureScreen(Activity context) {
        View cv = context.getWindow().getDecorView();
        Bitmap bmp = Bitmap.createBitmap(cv.getWidth(), cv.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        cv.draw(canvas);
        return bmp;
    }

    /**
     * 回收图片
     */
    public void destoryBitmap() {
        if ((null != bmp) && (!bmp.isRecycled())) {
            bmp.recycle();
            System.out.println("回收图片！");
        }

    }

    protected void onDestroy() {
        super.onDestroy();
    }

}