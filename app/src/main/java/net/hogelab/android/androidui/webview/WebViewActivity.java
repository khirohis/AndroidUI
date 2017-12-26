package net.hogelab.android.androidui.webview;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.hogelab.android.androidui.R;

/**
 * Created by kobayasi on 2017/12/25.
 */

public class WebViewActivity extends AppCompatActivity {
    private final String TAG = WebViewActivity.class.getSimpleName();

    private WebView webView;
    private CustomWebChromeClient webChromeClient;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        webView = (WebView) findViewById(R.id.webview);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        webChromeClient = new CustomWebChromeClient((ViewGroup) findViewById(R.id.childRootView));
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new CustomWebViewClient());

        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        webView.loadUrl("https://www.google.com/");
    }

    @Override
    public void onBackPressed() {
        if (webChromeClient.isChildOpened()) {
            webChromeClient.closeChild();
        } else if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    private class CustomWebChromeClient extends WebChromeClient {
        final ViewGroup childRootView;
        WebView childWebView;


        CustomWebChromeClient(ViewGroup childRootView) {
            this.childRootView = childRootView;
            childRootView.setVisibility(View.INVISIBLE);
        }


        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            Log.d(TAG, "CustomWebChromeClient#onCreateWindow");

            childWebView = new WebView(WebViewActivity.this);
            WebSettings settings = childWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setSupportMultipleWindows(false);
            settings.setJavaScriptCanOpenWindowsAutomatically(false);

            childWebView.setWebViewClient(new CustomWebViewClient());

            childWebView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            childRootView.addView(childWebView);
            childRootView.setVisibility(View.VISIBLE);

            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(childWebView);
            resultMsg.sendToTarget();

            return true;
        }


        public boolean isChildOpened() {
            return childRootView.getVisibility() == View.VISIBLE;
        }

        public void closeChild() {
            if (childWebView != null) {
                childRootView.removeView(childWebView);
                childWebView = null;
            }

            childRootView.removeAllViews();
            childRootView.setVisibility(View.INVISIBLE);
        }

        public void goBack() {
            if (childWebView.canGoBack()){
                childWebView.goBack();
            } else {
                closeChild();
            }
        }
    }


    private class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url) {
            Log.d(TAG, "shouldOverrideUrlLoading: URL=" + url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.d(TAG, "shouldOverrideUrlLoading: Request=" + request.toString());
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "onPageStarted: URL=" + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "onPageFinished: URL=" + url);
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d(TAG, "onReceivedError: URL=" + failingUrl);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }
}
