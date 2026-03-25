package com.example.stylica_app.services;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class CaptchaService {

    public interface CaptchaListener {
        void onTokenReceived(String token);
        void onError(String error);
    }

    public void setupCaptcha(WebView webView, Activity activity, String siteKey, CaptchaListener listener){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.addJavascriptInterface(new Object(){
            @JavascriptInterface
            public void onCaptchaSuccess(String token) {
                activity.runOnUiThread(()->{
                    if(token != null && !token.isEmpty()) {
                        listener.onTokenReceived(token);
                    }else {
                        listener.onError("Empty Token");
                    }
                });
            }
        },"Android");
        webView.loadUrl("file:///android_asset/recaptcha.html?siteKey=" + siteKey);
    }
}
