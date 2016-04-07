package com.left.shothappy.bean;

import java.util.Date;

/**
 * Created by left on 16/4/7.
 * 用在学习进度页面绘制坐标
 */
public class DayCoordinate {
    //日期英文名
    private String name;

    //日期时间值
    private Date date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
