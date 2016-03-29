package com.left.shothappy.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by left on 16/3/26.
 */
public class User extends BmobUser {

    //头像
    private BmobFile head;
    private boolean pronunciation;//默认美音

    public boolean isPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(boolean pronunciation) {
        this.pronunciation = pronunciation;
    }

    public BmobFile getHead() {
        return head;
    }

    public void setHead(BmobFile head) {
        this.head = head;
    }

}
