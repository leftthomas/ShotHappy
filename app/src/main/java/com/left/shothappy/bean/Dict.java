package com.left.shothappy.bean;

import java.util.List;

/**
 * Created by left on 16/3/29.
 * 词典类，根据XML解析
 * 实现XML－－>javabean的转换
 */
public class Dict {

    private String key;//单词
    private List<Ps_pron> ps_prons;//音标与发音（美音与英音）
    private List<Pos_acceptation> pos_acceptations;//词性与释义（一般有多组）
    private List<Sent> sents;//例句（多组）

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Ps_pron> getPs_prons() {
        return ps_prons;
    }

    public void setPs_prons(List<Ps_pron> ps_prons) {
        this.ps_prons = ps_prons;
    }

    public List<Pos_acceptation> getPos_acceptations() {
        return pos_acceptations;
    }

    public void setPos_acceptations(List<Pos_acceptation> pos_acceptations) {
        this.pos_acceptations = pos_acceptations;
    }

    public List<Sent> getSents() {
        return sents;
    }

    public void setSents(List<Sent> sents) {
        this.sents = sents;
    }
}
