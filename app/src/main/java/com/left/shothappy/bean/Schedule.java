package com.left.shothappy.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by left on 16/4/6.
 * 每日学习量实体类，此类的数据用来给学习进度界面做数据源
 */
public class Schedule extends BmobObject {

    //学习的单词（列表size即是当日学习量）
    private List<String> words;

    //用来记录到此时总共学了有多少单词，方便查询统计
    private int num;

    private User user;

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
