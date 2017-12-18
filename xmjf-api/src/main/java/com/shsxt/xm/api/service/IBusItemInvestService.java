package com.shsxt.xm.api.service;

import com.shsxt.xm.api.query.BusItemInvestQuery;
import com.shsxt.xm.api.utils.PageList;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/11.
 */
public interface IBusItemInvestService {

    public PageList queryBusItemInvestsByParams(BusItemInvestQuery busItemInvestQuery);

    public void addBusItemInvest(Integer userId, Integer itemId, BigDecimal amount, String businessPassword);

    public Map<String,Object[]> queryInvestInfoByUserId(Integer id);
}
