package com.left.shothappy.views;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.MarkerView;
import com.left.shothappy.MainActivity;
import com.left.shothappy.R;

/**
 * Created by left on 16/3/30.
 */
public class CustomMarkerView extends MarkerView {

    private TextView tvContent;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvContent.setTypeface(MainActivity.typeFace);
    }

    @Override
    public void refreshContent(Entry e, int dataSetIndex) {
        tvContent.setText(String.valueOf((int) e.getVal()));
    }

    @Override
    public float getXOffset() {
        return -(getWidth() / 2);
    }

    @Override
    public float getYOffset() {
        return -getHeight();
    }
}
