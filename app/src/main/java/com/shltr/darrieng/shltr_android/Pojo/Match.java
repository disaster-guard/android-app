package com.shltr.darrieng.shltr_android.Pojo;

/**
 * Basic POJO containing information about matches to other users in the database.
 */
public class Match {

    private String name;
    private Double prob;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getProb() {
        return prob;
    }

    public void setProb(Double prob) {
        this.prob = prob;
    }

}
