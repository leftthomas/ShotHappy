package com.left.shothappy;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.left.shothappy.bean.DayCoordinate;
import com.left.shothappy.bean.Schedule;
import com.left.shothappy.utils.ScheduleUtils;
import com.left.shothappy.views.CustomMarkerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 学习进度的页面
 */
public class RateoflearningActivity extends BaseActivity {

    //进步曲线（总进度，统计历史量）
    public static LineChart mLineChart;
    //每日学习量（近七天单日学习量）
    public static BarChart mBarChart;
    // 自定义颜色
    public static int mLineColors = Color.rgb(137, 230, 81);
    public static int mBarColors = Color.rgb(240, 240, 30);
    // 自定义字体
    private static Typeface mTf;
    private static View view;
    private static ValueFormatter valueFormatter = new ValueFormatter() {
        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value);
        }
    };

    // 设置显示的样式
    public static void setupLineChart(final LineChart chart, LineData data, int color) {

        // disable the drawing of values into the chart
        chart.setDrawYValues(false);
        chart.setDrawBorder(false);

        //不显示数据描述
        chart.setDescription("");

        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        chart.setNoDataTextDescription("没有数据呢(⊙o⊙),快去学习吧");

        // enable / disable grid lines
        chart.setDrawVerticalGrid(false); // 是否显示垂直线

        // enable / disable grid background
        chart.setDrawGridBackground(false); // 是否显示表格颜色
        chart.setGridColor(Color.WHITE & 0x70FFFFFF); // 表格的颜色，在这里是给颜色设置一个透明度
        chart.setGridWidth(1.25f);// 表格线的线宽

        // 设置是否可以缩放
        chart.setDoubleTapToZoomEnabled(false);
        chart.setScaleEnabled(false);

        chart.setBackgroundColor(color);// 设置背景

        chart.setValueTypeface(mTf);// 设置字体

        chart.setData(data); // 设置数据

        CustomMarkerView mv = new CustomMarkerView(view.getContext(), R.layout.view_marker);

        // set the marker to the chart
        chart.setMarkerView(mv);
        chart.setValueFormatter(valueFormatter);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend(); // 设置标示

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.CIRCLE);// 样式
        l.setFormSize(6f);// 字体
        l.setTextColor(Color.WHITE);// 颜色
        l.setTypeface(mTf);// 字体

        YLabels y = chart.getYLabels(); // y轴的标示
        y.setTextColor(Color.WHITE);
        y.setTypeface(mTf);
        y.setFormatter(valueFormatter);

        XLabels x = chart.getXLabels(); // x轴显示的标签
        x.setTextColor(Color.WHITE);
        x.setTypeface(mTf);

        // animate calls invalidate()...
        chart.animateX(3000); // 立即执行的动画,x轴
    }

    // 设置显示的样式
    public static void setupBarChart(final BarChart chart, BarData data, int color) {

        chart.setDrawBarShadow(false);
        chart.setDrawBorder(false);
        chart.setDrawValueAboveBar(true);

        //不显示数据描述
        chart.setDescription("");

        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        chart.setNoDataTextDescription("没有数据呢(⊙o⊙),快去学习吧");

        // enable / disable grid lines
        chart.setDrawVerticalGrid(false); // 是否显示垂直线

        // enable / disable grid background
        chart.setDrawGridBackground(false); // 是否显示表格颜色
        chart.setGridColor(Color.WHITE & 0x70FFFFFF); // 表格的颜色，在这里是给颜色设置一个透明度
        chart.setGridWidth(1.25f);// 表格线的线宽

        // 设置是否可以缩放
        chart.setDoubleTapToZoomEnabled(false);
        chart.setScaleEnabled(false);
        chart.setBackgroundColor(color);// 设置背景

        chart.setValueTypeface(mTf);// 设置字体

        chart.setValueFormatter(valueFormatter);

        chart.setData(data); // 设置数据
        // get the legend (only possible after setting data)
        Legend l = chart.getLegend(); // 设置标示

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.CIRCLE);// 样式
        l.setFormSize(6f);// 字体
        l.setTextColor(Color.WHITE);// 颜色
        l.setTypeface(mTf);// 字体

        YLabels y = chart.getYLabels(); // y轴的标示
        y.setTextColor(Color.WHITE);
        y.setTypeface(mTf);
        y.setFormatter(valueFormatter);

        XLabels x = chart.getXLabels(); // x轴显示的标签
        x.setTextColor(Color.WHITE);
        x.setTypeface(mTf);

        // animate calls invalidate()...
        chart.animateY(3000); // 立即执行的动画,x轴
    }

    // 生成进步曲线数据
    public static LineData getLineData(List<DayCoordinate> dayCoordinates, List<Map<String, Integer>> maps) {
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < dayCoordinates.size(); i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xVals.add(dayCoordinates.get(i).getName());
        }

        // y轴的数据
        ArrayList<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < maps.size(); i++) {
            float val = maps.get(i).get(dayCoordinates.get(i).getName());
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        // y轴的数据集合
        LineDataSet set = new LineDataSet(yVals, "进步曲线");

        set.setLineWidth(1.75f); // 线宽
        set.setCircleSize(3f);// 显示的圆形大小
        set.setColor(Color.WHITE);// 显示颜色
        set.setCircleColor(Color.WHITE);// 圆形的颜色
        set.setHighLightColor(Color.WHITE); // 高亮的线的颜色

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        return data;
    }

    // 生成每日学习量数据，
    public static BarData getBarData(List<DayCoordinate> dayCoordinates, List<Schedule> schedules) {
        //x轴的数据--日期（近七天）
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < dayCoordinates.size(); i++) {
            xVals.add(dayCoordinates.get(i).getName());
        }

        //y轴的数据--学习单词数（对应日期）
        ArrayList<BarEntry> yVals = new ArrayList<>();
        for (int i = 0; i < schedules.size(); i++) {
            float val;
            List<String> words = schedules.get(i).getWords();
            if (words == null) {
                val = 0;
            } else if (words.size() == 0) {
                val = 0;
            } else {
                val = (float) schedules.get(i).getWords().size();
            }
            yVals.add(new BarEntry(val, i));
        }

        // y轴的数据集合
        BarDataSet set = new BarDataSet(yVals, "每日学习量");

        set.setColor(Color.GREEN);// 显示颜色
        set.setHighLightColor(Color.BLUE);

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set); // add the datasets

        // create a data object with the datasets
        BarData data = new BarData(xVals, dataSets);

        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rateoflearning);
        setTitle(R.string.title_rateoflearning);
        mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Bold.ttf");
        mLineChart = (LineChart) findViewById(R.id.linechart);
        mBarChart = (BarChart) findViewById(R.id.barchart);
        view = findViewById(R.id.rateoflearning_view);
        //绘制BarChart
        ScheduleUtils.getDailyData(this, mBarChart, mBarColors);
        //绘制LineChart
        ScheduleUtils.getImprovementData(this, mLineChart, mLineColors);
    }

}
