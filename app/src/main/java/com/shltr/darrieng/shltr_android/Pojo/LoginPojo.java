package com.shltr.darrieng.shltr_android.Pojo;

/**
 * Simple POJO used for getting user login data and passing it as JSON.
 */
public class LoginPojo {
    String grant_type = "password";
    String client_id = "4";
    String client_secret = "p7lrAtXIa15549Qq5a8gGNoOzuVwYRQfOYTcMWyh";
    String username;
    String password;
    String scope = "*";

    public LoginPojo(
        String username,
        String password) {

        this.username = username;
        this.password = password;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "LoginPojo{" +
            "grant_type='" + grant_type + '\'' +
            ", client_id='" + client_id + '\'' +
            ", client_secret='" + client_secret + '\'' +
            ", username='" + username + '\'' +
            ", password='" + password + '\'' +
            ", scope='" + scope + '\'' +
            '}';
    }
}
