package com.grayraven.eclogintest.PoJos;

import java.util.List;

/**
 * Created by Jim on 6/15/2016.
 */
public class Election {

    Election() {}

    public Election(String title, String remark, int year, List<State> states){
        this.title = title;
        this.remark = remark;
        this.year = year;
        this.states = states;
    }

    private String title;
    private String remark;
    private int year;
    private List<State> states;

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getRemark() {

        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Election{" +
                "remark='" + remark + '\'' +
                ", title='" + title + '\'' +
                ", year=" + year +
                '}';
    }
}
