package com.leeewy.tlswebview.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.leeewy.tlswebview.R;
import com.leeewy.tlswebview.fragments.MessageDialogFragment;
import com.leeewy.tlswebview.managers.KeyboardManager;
import com.leeewy.tlswebview.managers.NetworkManager;
import com.leeewy.tlswebview.ssl.TLSSocketFactory;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static final String CSS_FILE = ".css";
    private static final String JS_FILE = ".js";
    private static final String CSS_DIR = "/css/";
    private static final String JS_DIR = "/js/";

    private WebView webView;
    private ImageView button;
    private EditText urlView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);

        urlView = (EditText) findViewById(R.id.url);

        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(buildClient());
        webView.setWebChromeClient(buildWebChromeClient());

        button = (ImageView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick();
            }
        });
    }

    private WebViewClient buildClient() {
        return new WebViewClient() {
            private OkHttpClient okHttp = new OkHttpClient.Builder().sslSocketFactory(new TLSSocketFactory(), TLSSocketFactory.getTrustManager()).build();

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                Log.e(TAG, "URL -> " + url);

                if (url.contains(CSS_FILE) || url.contains(JS_FILE) || url.contains(CSS_DIR) || url.contains(JS_DIR)) {
                    Log.e(TAG, "Css or js -> default interceptor");
                    return super.shouldInterceptRequest(view, url);
                } else {
                    Request okHttpRequest = new Request.Builder().url(url).header("User-Agent", "OkHttp Example").build();
                    try {
                        Response response = okHttp.newCall(okHttpRequest).execute();
                        return new WebResourceResponse("text/html", "UTF-8", response.body().byteStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                MessageDialogFragment.getInstance(R.string.information, getString(R.string.oh_no) + description).show(getSupportFragmentManager(), "MessageDialogFragment");
            }
        };
    }

    private WebChromeClient buildWebChromeClient() {
        return new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }

                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        };
    }

    private void onButtonClick() {
        KeyboardManager.hideKeyboard(this, getCurrentFocus());

        if(NetworkManager.isNetworkEnable(this)) {
            String url = urlView.getText().toString();
            if (StringUtils.isNotBlank(url)) {
                if(!(url.startsWith(HTTP) || url.startsWith(HTTPS))){
                    url = HTTP + url;
                }

                webView.loadUrl(url);
            } else {
                MessageDialogFragment.getInstance(R.string.information, getString(R.string.wrong_url)).show(getSupportFragmentManager(), "MessageDialogFragment");
            }
        } else {
            MessageDialogFragment.getInstance(R.string.information, getString(R.string.no_network)).show(getSupportFragmentManager(), "MessageDialogFragment");
        }
    }
}
