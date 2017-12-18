package com.shsxt.xm.api.service;

import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.BasUserSecurity;

/**
 * Created by Administrator on 2017/12/11.
 */
public interface IBasUserSecurityService {
   public BasUserSecurity queryBasUserSecurityByUserId(Integer userId);


    /**
     * 用户认证
     */
    public void doUserAuth(String realName, String idCard, String businessPassword, String confirmPassword, Integer userId);


    /**
     * 用户认证校验
     */
    public ResultInfo userAuthCheck(Integer userId);
}
