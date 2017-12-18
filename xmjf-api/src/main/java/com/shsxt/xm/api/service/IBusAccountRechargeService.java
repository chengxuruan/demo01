package com.shsxt.xm.api.service;

import com.shsxt.xm.api.query.AccountRechargeQuery;
import com.shsxt.xm.api.utils.PageList;

/**
 * Created by Administrator on 2017/12/14.
 */
public interface IBusAccountRechargeService {
    public PageList queryRechargeRecodesByParams(AccountRechargeQuery accountRechargeQuery);
}
