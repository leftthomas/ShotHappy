package com.left.shothappy.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rateoflearning, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LineChart mChart;
        int mColors = Color.rgb(137, 230, 81); // 自定义颜色
        mChart = (LineChart) view.findViewById(R.id.linechart);
        // 生产数据
        LineData data = getData(36, 100);
        setupChart(mChart, data, mColors);
    }

    // 设置显示的样式
    void setupChart(final LineChart chart, LineData data, int color) {

        Typeface mTf; // 自定义显示字体
        // 自定义字体
        mTf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Bold.ttf");

        // disable the drawing of values into the chart
        chart.setDrawYValues(false);

        chart.setDrawBorder(false);

        //不显示数据描述
        chart.setDescription("");

        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        chart.setNoDataTextDescription("没有数据呢(⊙o⊙)");

        // enable / disable grid lines
        chart.setDrawVerticalGrid(false); // 是否显示垂直线

        // enable / disable grid background
        chart.setDrawGridBackground(false); // 是否显示表格颜色
        chart.setGridColor(Color.WHITE & 0x70FFFFFF); // 表格的颜色，在这里是给颜色设置一个透明度
        chart.setGridWidth(1.25f);// 表格线的线宽

        // disable touch gestures
        chart.setTouchEnabled(true); // 设置是否可以触摸
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
        chart.animateX(3000); // 立即执行的动画,x轴
    }


    // 生成一个数据，
    LineData getData(int count, float range) {
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            // x轴显示的数据，这里默认使用数字下标显示
            xVals.add(i + "");
        }

        // y轴的数据
        ArrayList<Entry> yVals = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 3;
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
}
