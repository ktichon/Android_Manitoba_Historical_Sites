package com.example.winnipeghistoricalsites;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebviewActivity extends AppCompatActivity {
    private WebView webView;
    //Better and easier to pull up link in browser than in my own webview. So this code is now unused in the application.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = (WebView) findViewById(R.id.wvInfo);
        String url = getIntent().getStringExtra(getString(R.string.webviewUrl));
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        //String pdf = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";

        try {
            webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + url);
            //webView.loadUrl(url);
        } catch (Error e)
        {
            Toast.makeText(this, "Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
        }





    }
}