package com.shltr.darrieng.shltr_android.Pojo;

import java.util.List;

/**
 * Simple POJO used to contain information about matches to other users.
 */
public class UserModel {

    private String status;
    private List<Match> matches;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}
