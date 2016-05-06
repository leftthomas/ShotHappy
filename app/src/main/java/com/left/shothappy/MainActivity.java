package com.left.shothappy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

//    private ImageView today_card;//今日卡片

    private Button to_ar, to_thesaurus, to_rateoflearning, to_test;
    private ImageView to_rank, share_app, to_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        to_ar = (Button) findViewById(R.id.to_ar);
        to_thesaurus = (Button) findViewById(R.id.to_thesaurus);
        to_rateoflearning = (Button) findViewById(R.id.to_rateoflearning);
        to_test = (Button) findViewById(R.id.to_test);
        to_rank = (ImageView) findViewById(R.id.to_rank);
        share_app = (ImageView) findViewById(R.id.share_app);
        to_setting = (ImageView) findViewById(R.id.to_setting);

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
                startActivity(new Intent(getApplicationContext(), TestActivity.class));
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


//        today_card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                BmobQuery<Schedule> query = new BmobQuery<>();
//                List<BmobQuery<Schedule>> and = new ArrayList<>();
//                //大于00：00：00
//                BmobQuery<Schedule> q1 = new BmobQuery<>();
//                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = null;
//                try {
//                    date = sdf.parse(sdf.format(ScheduleUtils.getTodayZero()));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                q1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));
//                and.add(q1);
//
//                //小于23：59：59
//                BmobQuery<Schedule> q2 = new BmobQuery<>();
//                Date date1 = null;
//                try {
//                    date1 = sdf.parse(sdf.format(ScheduleUtils.getTodayEnd()));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                q2.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date1));
//                and.add(q2);
//                //添加复合与查询
//                query.and(and);
//
//                query.addWhereEqualTo("user", user);    // 查询当前用户当日Schedule
//                query.order("createdAt");
//                query.findObjects(getApplicationContext(), new FindListener<Schedule>() {
//                    @Override
//                    public void onSuccess(List<Schedule> list) {
//                        if (list == null || list.size() == 0 || list.get(0).getWords() == null || list.get(0).getWords().size() < 10) {
//                            Snackbar.make(navigationView, getString(R.string.today_tip), Snackbar.LENGTH_SHORT).show();
//                        } else {
//                            //查找到当日的奖励视频
//                            BmobQuery<RewardVideo> videoBmobQuery = new BmobQuery<>();
//                            List<BmobQuery<RewardVideo>> videoand = new ArrayList<>();
//                            //大于00：00：00
//                            BmobQuery<RewardVideo> q3 = new BmobQuery<>();
//                            Date date = null;
//                            try {
//                                date = sdf.parse(sdf.format(ScheduleUtils.getTodayZero()));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            q3.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));
//                            videoand.add(q3);
//
//                            //小于23：59：59
//                            BmobQuery<RewardVideo> q4 = new BmobQuery<>();
//                            Date date1 = null;
//                            try {
//                                date1 = sdf.parse(sdf.format(ScheduleUtils.getTodayEnd()));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            q4.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date1));
//                            videoand.add(q4);
//                            //添加复合与查询
//                            videoBmobQuery.and(videoand);
//                            videoBmobQuery.order("createdAt");
//                            videoBmobQuery.findObjects(getApplicationContext(), new FindListener<RewardVideo>() {
//                                @Override
//                                public void onSuccess(List<RewardVideo> list) {
//                                    if (list == null || list.size() == 0 || list.get(0).getVideo() == null) {
//                                        Snackbar.make(navigationView, getString(R.string.reward_tip), Snackbar.LENGTH_SHORT).show();
//                                    } else {
//                                         /* 开始播放视频 */
//                                        Intent intent = new Intent();
//                                        //设置传递方向
//                                        intent.setClass(getApplicationContext(), RewardActivity.class);
//                                        //绑定数据
//                                        intent.putExtra("name", list.get(0).getName());
//                                        intent.putExtra("path", list.get(0).getVideo().getFileUrl(getApplicationContext()));
//                                        //启动activity
//                                        startActivity(intent);
//                                    }
//                                }
//
//                                @Override
//                                public void onError(int i, String s) {
//                                    Snackbar.make(navigationView, getString(R.string.error_network), Snackbar.LENGTH_SHORT).show();
//                                }
//                            });
//                        }
//                    }
//
//                    @Override
//                    public void onError(int i, String s) {
//                        Snackbar.make(navigationView, getString(R.string.error_network), Snackbar.LENGTH_SHORT).show();
//                    }
//                });
//                drawer.closeDrawer(GravityCompat.START);
//            }
//        });
    }

}
