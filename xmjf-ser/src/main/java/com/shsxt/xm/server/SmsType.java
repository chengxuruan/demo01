package com.shsxt.xm.server;

/**
 * Created by lp on 2017/12/8.
 */
public enum SmsType {
    REGISTER(1),
    NOTIFY(2);
    private Integer type;

    SmsType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }
}
