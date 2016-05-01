package com.left.shothappy.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by left on 16/4/6.
 * 每日奖励视频
 */
public class RewardVideo extends BmobObject {

    //视频文件
    private BmobFile video;
    //视频名称
    private String name;

    public BmobFile getVideo() {
        return video;
    }

    public void setVideo(BmobFile video) {
        this.video = video;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
