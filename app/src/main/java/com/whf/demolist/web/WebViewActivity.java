package com.whf.demolist.web;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import com.whf.demolist.R;

import wendu.dsbridge.DWebView;
import wendu.dsbridge.OnReturnValue;

@RequiresApi(api = Build.VERSION_CODES.M)
public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Web_" + WebViewActivity.class.getSimpleName();
    public static long startTime;

    private DWebView webView;
    private RelativeLayout rlRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        String url = getIntent().getStringExtra("web_url");
        Log.d(TAG, "web url = " + url);

        rlRoot = findViewById(R.id.rl_root);
        webView = new DWebView(this);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.END_OF, R.id.scroll_view);
        webView.setLayoutParams(layoutParams);
        rlRoot.addView(webView);
        if (!TextUtils.isEmpty(url)) {
            initWebSetting(webView);
            initWebViewClient(webView);
            initDsBridge(webView);
            webView.loadUrl(url);
        }

        findViewById(R.id.tv_back).setOnClickListener(this);
        findViewById(R.id.tv_forward).setOnClickListener(this);
        findViewById(R.id.tv_pause).setOnClickListener(this);
        findViewById(R.id.tv_resume).setOnClickListener(this);
        findViewById(R.id.tv_call_js).setOnClickListener(this);
        findViewById(R.id.tv_call_js_async).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                webView.goBack();
                Log.d(TAG, "goBack");
                break;
            case R.id.tv_forward:
                webView.goForward();
                Log.d(TAG, "goForward");
                break;
            case R.id.tv_pause:
                webView.onPause();
                Log.d(TAG, "onPause");
                break;
            case R.id.tv_resume:
                webView.onResume();
                Log.d(TAG, "onResume");
                break;
            case R.id.tv_call_js:
                Log.d(TAG, "start call js!");
                webView.callHandler("getHello", new Object[]{1.0f, 2.0f}, new OnReturnValue<String>() {
                    @Override
                    public void onValue(String retValue) {
                        Log.d(TAG, "call js getHello = " + retValue);
                    }
                });
                break;
            case R.id.tv_call_js_async:
                Log.d(TAG, "start call js!");
                webView.callHandler("append", new Object[]{"a", "b", "c"}, new OnReturnValue<String>() {
                    @Override
                    public void onValue(String retValue) {
                        Log.d(TAG, "call js getHello = " + retValue);
                    }
                });
                break;
        }
    }

    private void checkMemory() {
        System.gc();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int[] pids = new int[]{Process.myPid()};
        Debug.MemoryInfo processInfo = activityManager.getProcessMemoryInfo(pids)[0];
        Log.d(TAG, "checkMemory = " + processInfo.getMemoryStats());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewClient(final WebView webView) {
        webView.setWebChromeClient(new WebChromeClient() {

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
                this.openFileChooser(uploadMsg);
            }

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                this.openFileChooser(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg) {

            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            /**
             * 开始加载页面
             */
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "onPageStarted duration = " + (System.currentTimeMillis() - startTime));
            }

            /**
             * 拦截某一次的 request 来返回我们自己加载的数据
             */
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.d(TAG, "shouldInterceptRequest!");
                return super.shouldInterceptRequest(view, request);
            }

            /**
             * 加载页面资源时会回调
             */
            @Override
            public void onLoadResource(WebView view, String url) {
                Log.d(TAG, "onLoadResource!");
                super.onLoadResource(view, url);
            }

            /**
             * 访问 url 出错
             */
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.d(TAG, "onReceivedError = " + error);
            }

            /**
             * 访问 url 出错
             */
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                Log.d(TAG, "onReceivedHttpError = " + errorResponse.getStatusCode());
            }

            /**
             * ssl 访问证书出错
             *
             * handler.cancel()   取消加载
             * handler.proceed()  对然错误也继续加载。
             */
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                Log.d(TAG, "onReceivedSslError = " + error);
            }

            /**
             * 页面加载结束
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d(TAG, "onPageFinished duration = " + (System.currentTimeMillis() - startTime));
            }
        });
    }

    private void initDsBridge(DWebView webView) {
        webView.addJavascriptObject(new JsApi(), null);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebSetting(WebView webView) {
        WebSettings settings = webView.getSettings();
        //是否允许执行JavaScript，默认为false
        settings.setJavaScriptEnabled(true);

        //解决对某些标签的不支持出现白屏
        settings.setDomStorageEnabled(true);
        //设置缓存方式：LOAD_DEFAULT、LOAD_CACHE_ELSE_NETWORK、LOAD_NO_CACHE、LOAD_CACHE_ONLY，默认值LOAD_DEFAULT
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //设置 Application 缓存API是否开启，默认false
        settings.setAppCacheEnabled(false);
        //设置当前 Application 缓存文件路径
        settings.setAppCachePath(getCacheDir().getPath());

        //是否支持缩放，默认为true
        settings.setSupportZoom(false);
        //是否使用内置缩放
        settings.setBuiltInZoomControls(false);
        //是否显示默认的缩放控件
        settings.setDisplayZoomControls(false);

        //设置是否允许WebView使用File协议，默认设置为true，即允许在File域下执行任意JavaScript代码
        settings.setAllowFileAccess(false);

        /*
         * 设置是否允许通过 file url 加载的 Javascript 读取其他的本地文件，
         * 这个设置在 JELLY_BEAN(android 4.1) 以前的版本默认是允许，在 JELLY_BEAN 及以后的版本中默认是禁止的.
         * */
        settings.setAllowFileAccessFromFileURLs(false);

        /*
         * 设置是否允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源.
         * 这个设置在 JELLY_BEAN 以前的版本默认是允许，在 JELLY_BEAN 及以后的版本中默认是禁止的。
         * */
        settings.setAllowUniversalAccessFromFileURLs(false);

        //是否保存密码,默认为true，改为false，避免造成用户的个人敏感数据泄露
        settings.setSavePassword(false);

        /*
         * 设置当一个安全站点企图加载来自一个不安全站点资源时WebView的行为
         *
         * 5.0以下，默认alway_allow模式，允许同时加载https和http,
         * 5.0以后，默认是never_allow,即总不允许webView同时加载https和http
         */
        if (Build.VERSION.SDK_INT >= 21) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        /*
         * 设置自适应网页大小
         * */
        settings.setUseWideViewPort(true);
    }


}
