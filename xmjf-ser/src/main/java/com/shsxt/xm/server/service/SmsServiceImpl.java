package com.shsxt.xm.server.service;

import com.alibaba.fastjson.JSON;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.service.ISmsService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.server.SmsType;
import com.shsxt.xm.server.constant.TaoBaoConstant;
import com.taobao.api.*;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/8.
 * 用户注册/登录获取验证码
 */
@Service
public class SmsServiceImpl implements ISmsService {

    @Resource
    private IBasUserService basUserService;
    @Override
    public void sendPhoneSms(String phone, String code, Integer type) {
        //手机号非空判断
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号非法!");
        //手机号验证码非空判断
        AssertUtil.isTrue(StringUtils.isBlank(code),"手机号验证码不能为空!");

        AssertUtil.isTrue(null==type,"短信验证码类型不匹配");
        //判断是否是注册类型值
        AssertUtil.isTrue(!type.equals(SmsType.NOTIFY.getType())
                &&!type.equals(SmsType.REGISTER.getType()),"短信验证码类型不匹配!");

        //如果是注册用户
        if (type.equals(SmsType.REGISTER.getType())){

            /**
             * 注册时用户手机号不能重复
             */
            //根据手机号查询用户对象
             BasUser basUser = basUserService.queryBasUserByPhone(phone);
             AssertUtil.isTrue(null !=basUser,"该手机号已注册");
              //调用短信发送获取验证码方法
             // doSend(phone,code,TaoBaoConstant.SMS_TEMATE_CODE_REGISTER);
        }


        //如果是登录用户
        if (type.equals(SmsType.NOTIFY.getType())){
           //doSend(phone,code,TaoBaoConstant.SMS_TEMATE_CODE_lOGIN);
        }
    }


    /**
     *  执行发送短信获取验证码
     */
    public void doSend(String phone ,String code,String templateCode){

        TaobaoClient  client = new DefaultTaobaoClient(TaoBaoConstant.SERVER_URL,TaoBaoConstant.APP_KEY, TaoBaoConstant.APP_SECRET);

        AlibabaAliqinFcSmsNumSendRequest request = new AlibabaAliqinFcSmsNumSendRequest();

        request.setExtend("");
        //普通类型
        request.setSmsType(TaoBaoConstant.SMS_TYPE);
        //蚂蚁金服
        request.setSmsFreeSignName(TaoBaoConstant.SMS_FREE_SIGN_NAME);

        Map<String,String> map = new HashMap<>();
        map.put("code",code);
        request.setSmsParamString(JSON.toJSONString(map));

        request.setRecNum(phone);
        request.setSmsTemplateCode(TaoBaoConstant.SMS_TEMATE_CODE_lOGIN);
        AlibabaAliqinFcSmsNumSendResponse rsp = null;

        try {
            rsp = client.execute(request);
            AssertUtil.isTrue(!rsp.isSuccess(),"短信发送失败,请稍后再试!");
        }catch (ApiException e){
            e.printStackTrace();
        }

    }
}
