package com.example.arpet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.webkit.WebViewAssetLoader;
import androidx.webkit.WebViewClientCompat;

/**
 * 国内机型友好方案：使用 WebAR（A-Frame + AR.js），不依赖 Google ARCore。
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 1001;
    private static final String HOME_PAGE_URL =
            "https://appassets.androidplatform.net/assets/www/index.html";
    private static final String AR_PAGE_URL =
            "https://appassets.androidplatform.net/assets/www/ar_mobile.html";

    private WebView webView;
    private LinearLayout loadingPanel;
    private TextView loadingHint;
    private TextView statusText;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean pageShown;
    private String pendingArPageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        loadingPanel = findViewById(R.id.loadingPanel);
        loadingHint = findViewById(R.id.loadingHint);
        statusText = findViewById(R.id.statusText);

        webView.setBackgroundColor(Color.BLACK);
        setupWebView();
        loadHomePage();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CAMERA_PERMISSION) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (pendingArPageUrl != null) {
                webView.loadUrl(pendingArPageUrl);
                pendingArPageUrl = null;
            }
        } else if (pendingArPageUrl != null) {
            pendingArPageUrl = null;
            Toast.makeText(this, "需要相机权限才能进入 AR 体验", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.addJavascriptInterface(new WebAppBridge(), "AndroidBridge");

        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        webView.setWebViewClient(new WebViewClientCompat() {
            @Override
            public void onPageFinished(WebView view, String url) {
                showWebContent();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("ar_mobile.html")) {
                    return openArPage(url);
                }
                return false;
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(
                    WebView view,
                    WebResourceRequest request
            ) {
                WebResourceResponse response = assetLoader.shouldInterceptRequest(request.getUrl());
                return response != null ? response : super.shouldInterceptRequest(view, request);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                runOnUiThread(() -> {
                    if (!hasCameraPermission()) {
                        request.deny();
                        return;
                    }
                    request.grant(request.getResources());
                });
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress > 0 && newProgress < 100) {
                    loadingHint.setText("正在加载 AR 资源 " + newProgress + "%…");
                }
            }
        });
    }

    private boolean openArPage(String url) {
        if (hasCameraPermission()) {
            return false;
        }
        pendingArPageUrl = url;
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                REQUEST_CAMERA_PERMISSION
        );
        return true;
    }

    private void loadHomePage() {
        pageShown = false;
        loadingPanel.setVisibility(View.VISIBLE);
        webView.setVisibility(View.INVISIBLE);
        loadingHint.setText("正在加载主页…");
        webView.loadUrl(HOME_PAGE_URL);

        // 超时提示，避免一直卡在启动页
        mainHandler.postDelayed(() -> {
            if (!pageShown && loadingPanel.getVisibility() == View.VISIBLE) {
                loadingHint.setText("加载较慢，请检查网络后稍候…");
            }
        }, 8000);
    }

    private void showWebContent() {
        if (pageShown) {
            return;
        }
        pageShown = true;
        loadingPanel.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.GONE);
    }

    private void showCameraError(String message) {
        loadingPanel.setVisibility(View.GONE);
        webView.setVisibility(View.INVISIBLE);
        statusText.setVisibility(View.VISIBLE);
        statusText.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mainHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private class WebAppBridge {
        @JavascriptInterface
        public void onCameraError(String message) {
            runOnUiThread(() -> showCameraError(message));
        }
    }
}
