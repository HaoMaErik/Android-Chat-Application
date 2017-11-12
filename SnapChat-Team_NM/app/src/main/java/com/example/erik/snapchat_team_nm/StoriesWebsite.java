package com.example.erik.snapchat_team_nm;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Erik on 2016/10/15.
 */
public class StoriesWebsite extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_website);

        WebView webView = (WebView)findViewById(R.id.storiesWebview);
        webView.setWebViewClient(new StoriesWebsite.MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(getIntent().getStringExtra("url"));
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
