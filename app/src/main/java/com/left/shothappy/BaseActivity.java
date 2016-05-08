package com.left.shothappy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by left on 16/5/8.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

}
