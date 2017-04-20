package com.shltr.darrieng.shltr_android.Pojo;

/**
 * Model containing all information about a user.
 */
public class CompleteIdentificationModel {
    private UserModel user_model;
    private AgeModel age_model;

    public UserModel getUser_model() {
        return user_model;
    }

    public void setUser_model(UserModel user_model) {
        this.user_model = user_model;
    }

    public AgeModel getAge_model() {
        return age_model;
    }

    public void setAge_model(AgeModel age_model) {
        this.age_model = age_model;
    }

}

