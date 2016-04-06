package com.left.shothappy.views;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;
import com.left.shothappy.R;

import java.util.ArrayList;

/**
 * 学习进度的页面
 */
public class RateoflearningFragment extends Fragment {

    //进步曲线（总进度，统计历史量）
    private LineChart mLineChart;
    //每日学习量（近七天单日学习量）
    private BarChart mBarChart;
    // 自定义颜色
    private int mLineColors = Color.rgb(137, 230, 81);
    private int mBarColors = Color.rgb(240, 240, 30);
    // 自定义字体
    private Typeface mTf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rateoflearning, container, false);
        mTf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Bold.ttf");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLineChart = (LineChart) view.findViewById(R.id.linechart);
        mBarChart = (BarChart) view.findViewById(R.id.barchart);
        // 生产数据
        LineData lineData = getLineData(36, 100);
        BarData barData = getBarData(7, 100);
        setupLineChart(mLineChart, lineData, mLineColors);
        setupBarChart(mBarChart, barData, mBarColors);

    }

    // 设置显示的样式
    private void setupLineChart(final LineChart chart, LineData data, int color) {

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

        CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.view_marker);

        // set the marker to the chart
        chart.setMarkerView(mv);

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

        XLabels x = chart.getXLabels(); // x轴显示的标签
        x.setTextColor(Color.WHITE);
        x.setTypeface(mTf);

        // animate calls invalidate()...
        chart.animateX(3000); // 立即执行的动画,x轴
    }


    // 设置显示的样式
    private void setupBarChart(final BarChart chart, BarData data, int color) {

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

        XLabels x = chart.getXLabels(); // x轴显示的标签
        x.setTextColor(Color.WHITE);
        x.setTypeface(mTf);

        // animate calls invalidate()...
        chart.animateY(3000); // 立即执行的动画,x轴
    }

    // 生成进步曲线数据
    private LineData getLineData(int count, float range) {
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xVals.add(i + "");
        }

        // y轴的数据
        ArrayList<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            float val = i;
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
    private BarData getBarData(int count, float range) {
        //x轴的数据--日期（近七天）
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            xVals.add(i + "");
        }


        //y轴的数据--学习单词数（对应日期）
        ArrayList<BarEntry> yVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 3;
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
}
