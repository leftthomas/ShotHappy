package com.left.shothappy.utils;

import android.app.Activity;

import com.left.shothappy.bean.Schedule;
import com.left.shothappy.bean.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by left on 16/4/6.
 */
public class ScheduleUtils {

    private static List<Schedule> schedules;

    /**
     * 更新每日学习量，内部使用
     *
     * @param activity
     * @param key
     */
    private static void updateSchedule(Activity activity, String key) {
        //如果今天还没有Schedule
        if (schedules == null || schedules.size() == 0) {
            //生成一条Schedule到云端
            User user = BmobUser.getCurrentUser(activity, User.class);
            // 创建Schedule信息
            Schedule schedule = new Schedule();
            List<String> word = new ArrayList<>();
            word.add(key);
            schedule.setWords(word);
            //添加一对一关联
            schedule.setUser(user);
            schedule.save(activity, new SaveListener() {

                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int code, String msg) {

                }
            });
        } else {
            List<String> words = schedules.get(0).getWords();
            //用来判断此key今天是否已经学过了,默认没学过
            boolean flag = true;
            for (int i = 0; i < words.size(); i++) {
                if (words.get(i).equals(key)) {
                    flag = false;
                    break;
                }
            }
            //根据条件更新此条Schedule
            if (flag) {
                //这时才需要更新
                Schedule p = new Schedule();
                words.add(key);
                p.setWords(words);
                p.update(activity, schedules.get(0).getObjectId(), new UpdateListener() {

                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
            }
        }
    }

    /**
     * 获取当天0点的时间
     *
     * @return
     */
    public static Date getTodayZero() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取当天23:59:59的值
     *
     * @return
     */
    public static Date getTodayEnd() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /**
     * 获取当天的Schedules(实际上只可能有一条)
     * 更新每日学习量，开放给外部
     *
     * @return
     */
    public static void UpdateSchedule(final Activity activity, final String key) {

        //先检查云端数据库里是否有当前用户今日的Schedule
        User user = BmobUser.getCurrentUser(activity, User.class);
        BmobQuery<Schedule> query = new BmobQuery<>();
        List<BmobQuery<Schedule>> and = new ArrayList<>();
        //大于00：00：00
        BmobQuery<Schedule> q1 = new BmobQuery<>();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(sdf.format(getTodayZero()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        q1.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(date));
        and.add(q1);

        //小于23：59：59
        BmobQuery<Schedule> q2 = new BmobQuery<>();
        Date date1 = null;
        try {
            date1 = sdf.parse(sdf.format(getTodayEnd()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        q2.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date1));
        and.add(q2);
        //添加复合与查询
        query.and(and);

        query.addWhereEqualTo("user", user);    // 查询当前用户的所有Schedule
        query.order("-updatedAt");
        query.findObjects(activity, new FindListener<Schedule>() {
            @Override
            public void onSuccess(List<Schedule> object) {
                schedules = object;
                //一定要成功了之后再做
                updateSchedule(activity, key);
            }

            @Override
            public void onError(int code, String msg) {
                schedules = null;
            }
        });
    }
}