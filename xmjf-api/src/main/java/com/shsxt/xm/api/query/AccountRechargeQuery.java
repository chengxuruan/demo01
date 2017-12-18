package com.shsxt.xm.api.query;

import java.io.Serializable;

/**
 * Created by lp on 2017/12/14.
 */
public class AccountRechargeQuery extends  BaseQuery implements Serializable {

    private static final long serialVersionUID = -7124396545453515114L;
    private  Integer userId;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
