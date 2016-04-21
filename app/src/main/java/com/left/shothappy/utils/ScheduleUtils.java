package com.left.shothappy.utils;

import android.app.Activity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.LineData;
import com.left.shothappy.bean.DayCoordinate;
import com.left.shothappy.bean.Schedule;
import com.left.shothappy.bean.User;
import com.left.shothappy.views.RateoflearningFragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
            //只有在key之前未添加过的情况下才会被添加
            Schedule p = new Schedule();
            p.addUnique("words", key);
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

    /**
     * 获取当天0点的时间
     *
     * @return
     */
    private static Date getTodayZero() {
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
    private static Date getTodayEnd() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }


    /**
     * 获得某日的零点
     *
     * @param date
     * @return
     */
    private static Date getDayZero(Date date) {
        Calendar dayStart = Calendar.getInstance();
        dayStart.setTime(date);
        dayStart.set(Calendar.HOUR_OF_DAY, 0);
        dayStart.set(Calendar.MINUTE, 0);
        dayStart.set(Calendar.SECOND, 0);
        dayStart.set(Calendar.MILLISECOND, 0);
        return dayStart.getTime();
    }


    /**
     * 获得某日23:59:59的值
     *
     * @param date
     * @return
     */
    private static Date getDayEnd(Date date) {
        Calendar dayEnd = Calendar.getInstance();
        dayEnd.setTime(date);
        dayEnd.set(Calendar.HOUR_OF_DAY, 23);
        dayEnd.set(Calendar.MINUTE, 59);
        dayEnd.set(Calendar.SECOND, 59);
        dayEnd.set(Calendar.MILLISECOND, 999);
        return dayEnd.getTime();
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
        query.order("createdAt");
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


    /**
     * 格式化最近七日数据量
     * 因为查询得到的列表不一定刚好7条数据，有可能少几条，需要做填充
     *
     * @param orins
     * @return
     */
    private static List<Schedule> formatNearSchedules(List<Schedule> orins, List<DayCoordinate> weeks, User user) throws ParseException {
        List<Schedule> afters = new ArrayList<>();
        if (orins == null) {
            for (int i = 0; i < 7; i++) {
                Schedule sc = new Schedule();
                sc.setUser(user);
                sc.setWords(null);
                afters.add(sc);
            }
            return afters;
        } else {
            if (orins.size() >= 7) {
                return orins;
            } else {
                if (orins.size() == 0) {
                    for (int i = 0; i < 7; i++) {
                        Schedule sc = new Schedule();
                        sc.setUser(user);
                        sc.setWords(null);
                        afters.add(sc);
                    }
                    return afters;
                } else {
                    //0<size<7
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    //生成7条数据
                    for (int i = 0; i < weeks.size(); i++) {
                        boolean flag = true;
                        int j = 0;
                        while (flag) {
                            Date date = sdf.parse(orins.get(j).getCreatedAt());
                            //找到了同一日的数据
                            if (getDayZero(date).equals(getDayZero(weeks.get(i).getDate()))) {
                                flag = false;
                                //跳出循环
                                afters.add(orins.get(j));
                            } else {
                                //没找到
                                j++;
                                //当前遍历完
                                if (j >= orins.size()) {
                                    Schedule sc = new Schedule();
                                    sc.setUser(user);
                                    sc.setWords(null);
                                    afters.add(sc);
                                    flag = false;
                                    //跳出循环
                                }
                                //没遍历完，继续往下找
                            }
                        }
                    }
                    return afters;
                }
            }
        }
    }


    /**
     * 获得距离今天前7天的日期英文表示形式
     *
     * @return
     */
    private static List<DayCoordinate> getNearWeekTime() {

        List<DayCoordinate> weeks = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("MMM dd", Locale.ENGLISH);

        DayCoordinate dayCoordinate = new DayCoordinate();
        dayCoordinate.setDate(c.getTime());
        dayCoordinate.setName(df.format(c.getTime()));
        weeks.add(dayCoordinate);
        for (int i = 0; i < 6; i++) {
            c.add(Calendar.DATE, -1);
            Date monday = c.getTime();
            String preMonday = df.format(monday);

            DayCoordinate day = new DayCoordinate();
            day.setDate(monday);
            day.setName(preMonday);
            weeks.add(day);
        }
        //注意，要将日期排序翻转下，得出的数据日期才是正序排好的
        Collections.reverse(weeks);
        return weeks;
    }


    /**
     * 通过给定日期获取此日之后的数据(包含当天)
     * 获取每日学习量曲线绘制所需的最近七日数据量
     *
     * @param activity
     * @return
     */
    public static void getDailyData(final Activity activity, final BarChart chart, final int color) {

        final List<DayCoordinate> weeks = getNearWeekTime();
        final User user = BmobUser.getCurrentUser(activity, User.class);
        BmobQuery<Schedule> query = new BmobQuery<>();
        //大于00：00：00
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date beforedate = null;
        try {
            beforedate = sdf.parse(sdf.format(getDayZero(weeks.get(0).getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        query.addWhereGreaterThanOrEqualTo("createdAt", new BmobDate(beforedate));
        query.addWhereEqualTo("user", user);    // 查询当前用户的所有Schedule
        query.order("createdAt");
        query.findObjects(activity, new FindListener<Schedule>() {
            @Override
            public void onSuccess(List<Schedule> object) {
                try {
                    //记得一定要做数据填充检查
                    List<Schedule> sc = formatNearSchedules(object, weeks, user);
                    //设置bar数据
                    BarData barData = RateoflearningFragment.getBarData(weeks, sc);
                    //设置bar样式
                    RateoflearningFragment.setupBarChart(chart, barData, color);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(int code, String msg) {
            }
        });

    }


    /**
     * 通过给定日期获取此日之前的数据(包含当天)
     *
     * @param activity
     * @return 获取进步曲线绘制所需的最近七日数据量
     * （日期，累计学习单词量）
     */
    public static void getImprovementData(Activity activity, final LineChart chart, final int color) {

        final List<DayCoordinate> weeks = getNearWeekTime();
        User user = BmobUser.getCurrentUser(activity, User.class);
        BmobQuery<Schedule> query = new BmobQuery<>();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date afterdate = null;
        try {
            afterdate = sdf.parse(sdf.format(getDayEnd(weeks.get(weeks.size() - 1).getDate())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(afterdate));
        query.addWhereEqualTo("user", user);    // 查询当前用户的所有Schedule
        query.order("createdAt");
        query.findObjects(activity, new FindListener<Schedule>() {
            @Override
            public void onSuccess(List<Schedule> object) {
                List<Map<String, Integer>> maps = formatSchedules(object, weeks);
                //设置line数据
                LineData lineData = RateoflearningFragment.getLineData(weeks, maps);
                //设置line样式
                RateoflearningFragment.setupLineChart(chart, lineData, color);
            }

            @Override
            public void onError(int code, String msg) {
            }
        });
    }

    /**
     * 格式化进步曲线数据
     *
     * @param orins
     * @param weeks
     * @return
     */
    private static List<Map<String, Integer>> formatSchedules(List<Schedule> orins, List<DayCoordinate> weeks) {
        List<Map<String, Integer>> afters = new ArrayList<>();
        //初始化
        for (int i = 0; i < 7; i++) {
            Map<String, Integer> sc = new HashMap<>();
            sc.put(weeks.get(i).getName(), 0);
            afters.add(sc);
        }
        if (orins == null) {
            return afters;
        } else {
            if (orins.size() == 0) {
                return afters;
            } else {
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (int i = 0; i < orins.size(); i++) {
                    try {
                        Date date = sdf.parse(orins.get(i).getCreatedAt());

                        for (int j = 0; j < weeks.size(); j++) {
                            if (getDayZero(date).getTime() <= getDayZero(weeks.get(j).getDate()).getTime()) {
                                afters.get(j).put(weeks.get(j).getName(), afters.get(j).get(weeks.get(j).getName()) + orins.size());
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                return afters;
            }
        }
    }
}