package com.left.shothappy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.left.shothappy.bean.Schedule;
import com.left.shothappy.bean.User;
import com.left.shothappy.config.MyApplication;
import com.left.shothappy.utils.ScheduleUtils;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;

public class MainActivity extends BaseActivity {

    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    private Button to_ar, to_thesaurus, to_rateoflearning, to_test;
    private ImageView to_rank, share_app, to_setting,card_box,reward_card;
    private User user;
    private View main_view,card_panel;
    private TextView unlock;
    //用来查询显示的奖励图片
    private Map<String,String> rewards_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = BmobUser.getCurrentUser(this, User.class);

        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(this, user.getObjectId(), new GetListener<User>() {
            @Override
            public void onSuccess(User object) {
                //及时设置下全局的rewards，传给ndk层
                if (object.getRewards() != null)
                    ((MyApplication) getApplication()).setRewards(object.getRewards());
            }
            @Override
            public void onFailure(int code, String arg0) {

            }
        });

        rewards_map=new HashMap<>();
        rewards_map.put("2016-05-07", "friend.png");
        rewards_map.put("2016-05-08", "mouse.png");
        rewards_map.put("2016-05-09", "dumb.png");
        rewards_map.put("2016-05-10", "crayon.jpg");
        rewards_map.put("2016-05-11", "detective.jpg");
        rewards_map.put("2016-05-12", "dragonball.jpg");
        rewards_map.put("2016-05-13","ninja.jpg");
        rewards_map.put("2016-05-14","robot.jpg");
        rewards_map.put("2016-05-15","sheep.jpg");

        to_ar = (Button) findViewById(R.id.to_ar);
        to_thesaurus = (Button) findViewById(R.id.to_thesaurus);
        to_rateoflearning = (Button) findViewById(R.id.to_rateoflearning);
        to_test = (Button) findViewById(R.id.to_test);
        to_rank = (ImageView) findViewById(R.id.to_rank);
        share_app = (ImageView) findViewById(R.id.share_app);
        to_setting = (ImageView) findViewById(R.id.to_setting);
        card_box= (ImageView) findViewById(R.id.card_box);
        main_view = findViewById(R.id.main_view);
        card_panel=findViewById(R.id.card_panel);
        reward_card= (ImageView) findViewById(R.id.reward_card);
        unlock= (TextView) findViewById(R.id.unlock);
        unlock.setTypeface(MyApplication.typeFace);
        card_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                BmobQuery<Schedule> query = new BmobQuery<>();
                List<BmobQuery<Schedule>> and = new ArrayList<>();
                //大于00：00：00
                BmobQuery<Schedule> q1 = new BmobQuery<>();
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = sdf.parse(sdf.format(ScheduleUtils.getTodayZero()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                q1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));
                and.add(q1);

                //小于23：59：59
                BmobQuery<Schedule> q2 = new BmobQuery<>();
                Date date1 = null;
                try {
                    date1 = sdf.parse(sdf.format(ScheduleUtils.getTodayEnd()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                q2.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date1));
                and.add(q2);
                //添加复合与查询
                query.and(and);

                query.addWhereEqualTo("user", user);    // 查询当前用户当日Schedule
                query.order("createdAt");
                query.findObjects(getApplicationContext(), new FindListener<Schedule>() {
                    @Override
                    public void onSuccess(List<Schedule> list) {

                        if (list == null || list.size() == 0 || list.get(0).getWords() == null || list.get(0).getWords().size() < 10) {
                            Snackbar.make(main_view, getString(R.string.today_tip), Snackbar.LENGTH_SHORT).show();
                        } else {
                            try {
                                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                final String tex=sdf.format(ScheduleUtils.getTodayZero());
                                InputStream in=getAssets().open("videos/"+rewards_map.get(tex));
                                Bitmap bmp= BitmapFactory.decodeStream(in);
                                reward_card.setImageBitmap(bmp);
                                card_panel.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable(){
                                    public void run() {
                                        card_panel.setVisibility(View.INVISIBLE);}
                                }, 3000);
                            } catch (Exception e) {
                            }
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Snackbar.make(main_view, getString(R.string.error_network), Snackbar.LENGTH_SHORT).show();
                    }
                });

            }
        });


        to_ar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ARActivity.class));
            }
        });

        to_thesaurus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ThesaurusActivity.class));
            }
        });

        to_rateoflearning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RateoflearningActivity.class));
            }
        });

        to_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BmobQuery<Schedule> query = new BmobQuery<>();
                List<BmobQuery<Schedule>> and = new ArrayList<>();
                //大于00：00：00
                BmobQuery<Schedule> q1 = new BmobQuery<>();
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = null;
                try {
                    date = sdf.parse(sdf.format(ScheduleUtils.getTodayZero()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                q1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));
                and.add(q1);

                //小于23：59：59
                BmobQuery<Schedule> q2 = new BmobQuery<>();
                Date date1 = null;
                try {
                    date1 = sdf.parse(sdf.format(ScheduleUtils.getTodayEnd()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                q2.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date1));
                and.add(q2);
                //添加复合与查询
                query.and(and);

                query.addWhereEqualTo("user", user);    // 查询当前用户当日Schedule
                query.order("createdAt");
                query.findObjects(getApplicationContext(), new FindListener<Schedule>() {
                    @Override
                    public void onSuccess(List<Schedule> list) {

                        if (list == null || list.size() == 0 || list.get(0).getWords() == null || list.get(0).getWords().size() < 10) {
                            Snackbar.make(main_view, getString(R.string.today_tip), Snackbar.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                            intent.putStringArrayListExtra("words", new ArrayList<>(list.get(0).getWords()));
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(int i, String s) {
                        Snackbar.make(main_view, getString(R.string.error_network), Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        to_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去评分，打开应用商场
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用分享
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            intent.putExtra(Intent.EXTRA_TEXT, getText(R.string.share_app_text));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, getString(R.string.app_name)));
            }
        });

        to_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // 在当前的activity中注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("ExitApp");
        this.registerReceiver(this.broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(this.broadcastReceiver);
    }
}
