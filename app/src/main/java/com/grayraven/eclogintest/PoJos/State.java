package com.grayraven.eclogintest.PoJos;

/**
 * Created by Jim on 6/15/2016.
 */
public class State {

    private String abbr;
    private String name;
    private int votes;
    private boolean splitable;
    private int dems;
    private int reps;
    private int third;

    public State(String abbr, String name,  boolean splitable, int dems,  int reps, int third, int votes) {
        this.abbr = abbr;
        this.dems = dems;
        this.name = name;
        this.reps = reps;
        this.splitable = splitable;
        this.third = third;
        this.votes = votes;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public int getDems() {
        return dems;
    }

    public void setDems(int dems) {
        this.dems = dems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public boolean isSplitable() {
        return splitable;
    }

    public void setSplitable(boolean splitable) {
        this.splitable = splitable;
    }

    public int getThird() {
        return third;
    }

    public void setThird(int third) {
        this.third = third;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    State() {}

    @Override
    public String toString() {
        return "State{" +
                "abbr='" + abbr + '\'' +
                ", name='" + name + '\'' +
                ", votes=" + votes +
                ", splitable=" + splitable +
                ", dems=" + dems +
                ", reps=" + reps +
                ", third=" + third +
                '}';
    }

}
