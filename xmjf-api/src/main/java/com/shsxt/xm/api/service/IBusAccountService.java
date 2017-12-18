package com.shsxt.xm.api.service;

import com.shsxt.xm.api.dto.PayDto;
import com.shsxt.xm.api.po.BusAccount;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/11.
 */
public interface IBusAccountService {

    public BusAccount queryBusAccountByUserId(Integer id);
    //构建支付请求
    public PayDto addRechargeRequestInfo(BigDecimal amount, String bussinessPassword, Integer id);

    //支付回调
    public  void updateAccountRecharge(Integer userId, BigDecimal totalFee,String outOrderNo,String sign, String  tradeNo, String  tradeStatus);

    //投资详情展示
    public Map<String,Object> queryAccountInfoByUserId(Integer userId);
}
