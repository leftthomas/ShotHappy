package com.left.shothappy;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.left.shothappy.utils.PicUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.lang.reflect.Method;

/**
 * Created by left on 16/5/5.
 */
public class BaseActivity extends AppCompatActivity {


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        return true;
    }

    //利用反射机制使每一个Action按钮对应的图标显示出来
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 各社交平台分享功能
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.share_wechatmoments) {
            share(SHARE_MEDIA.WEIXIN_CIRCLE);
            return true;
        }
        if (id == R.id.share_wechat) {
            share(SHARE_MEDIA.WEIXIN);
            return true;
        }
        if (id == R.id.share_qzone) {
            share(SHARE_MEDIA.QZONE);
            return true;
        }
        if (id == R.id.share_qq) {
            share(SHARE_MEDIA.QQ);
            return true;
        }
        if (id == R.id.share_weibo) {
            share(SHARE_MEDIA.SINA);
            return true;
        }
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 分享
     */
    private void share(SHARE_MEDIA num) {
        Bitmap shot = PicUtils.takeShot(this);

        PicUtils.share(num, this, shot);
    }

    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

        super.onBackPressed();
    }

}
