package com.shsxt.xm.api.service;



/**
 * Created by Administrator on 2017/12/8.
 */
public interface ISmsService {

    // 发送手机短信
    public void sendPhoneSms(String phone, String code, Integer typr);
}
