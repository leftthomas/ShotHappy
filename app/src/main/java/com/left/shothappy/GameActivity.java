package com.left.shothappy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private WebView webView;

    private String[] games = {"http://yx8.com/game/nipenlidexiaozhu/", "http://yx8.com/game/tusimianbao2/", "http://yx8.com/game/mengxiaoguaichitiantianquan/"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        webView = (WebView) findViewById(R.id.webView);
        //设置WebView属性，能够执行Javascript脚本
        webView.getSettings().setJavaScriptEnabled(true);

        //加载需要显示的网页
        webView.loadUrl(games[new Random().nextInt(games.length)]);
        //设置Web视图
        webView.setWebViewClient(new GameWebViewClient());

    }


    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        } else if ((keyCode == KeyEvent.KEYCODE_BACK) && !webView.canGoBack()) {
            webView.loadUrl("about:blank");
            finish();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            return true;
        }
        return false;
    }

    //Web视图
    private class GameWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
