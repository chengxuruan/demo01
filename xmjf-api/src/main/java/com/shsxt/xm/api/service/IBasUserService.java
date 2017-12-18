package com.shsxt.xm.api.service;

import com.shsxt.xm.api.po.BasUser;

/**
 * Created by lp on 2017/12/7.
 *  用户模块接口方法定义
 */
public interface IBasUserService {
    //根据id查询用户信息
    public BasUser queryBasUserById(Integer id);

    //根据手机号查询用户信息
    public  BasUser queryBasUserByPhone(String phone);


    /**
     * 保存用户记录
     * @param phone
     * @param password
     */
    public  void saveBasUser(String phone,String password);


    /**
     * 用户普通登录
     */
    public BasUser userLogin(String phone,String password);


    /**
     * 用户快捷登录
     */
    public BasUser quickLogin(String phone);


}
