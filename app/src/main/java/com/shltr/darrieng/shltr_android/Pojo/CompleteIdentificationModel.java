package com.shltr.darrieng.shltr_android.Pojo;

/**
 * Model containing all information about a user.
 */
public class CompleteIdentificationModel {
    private UserModel userModel;
    private AgeModel ageModel;

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public AgeModel getAgeModel() {
        return ageModel;
    }

    public void setAgeModel(AgeModel ageModel) {
        this.ageModel = ageModel;
    }

}

