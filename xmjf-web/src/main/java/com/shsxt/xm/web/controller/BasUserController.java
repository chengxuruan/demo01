package com.shsxt.xm.web.controller;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.service.IBasUserSecurityService;
import com.shsxt.xm.api.service.IBasUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.server.SessionTracker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * Created by Administrator on 2017/12/7.
 */
@Controller
@RequestMapping("user")
public class BasUserController {

    @Resource
    private IBasUserService basUserService;


    @Resource
    private IBasUserSecurityService basUserSecurityService;


    //测试代码
    @RequestMapping("queryBasUserById")
    @ResponseBody
    public BasUser queryBasUserById(Integer id){

        return basUserService.queryBasUserById(id);
    }


    /**
     * 用户注册
     */
    @RequestMapping("register")
    @ResponseBody
    public ResultInfo userRegister(String phone, String picCode, String code, String password, HttpSession session){
        ResultInfo resultInfo = new ResultInfo();

        //获取session中的picCode值
        String sessionPicCode = (String) session.getAttribute(P2PConstant.PICTURE_VERIFY_CODE);
        if (StringUtils.isBlank(sessionPicCode)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("图片验证码失效。。。。");
            return resultInfo;
        }

        if (!picCode.equals(sessionPicCode)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("验证码不匹配。。。");
            return resultInfo;
        }

        // 发送验证码时间
        Date sessionTime = (Date) session.getAttribute(P2PConstant.PHONE_VERIFY_CODE_EXPIRE_TIME+phone);
        if (sessionTime==null){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("验证码失效。。。");
            return resultInfo;
        }

        Date currTime = new Date();
        long time = (currTime.getTime()-sessionTime.getTime())/1000;

        if (time>180){
           resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
           resultInfo.setMsg("手机验证码失效...");
           return resultInfo;
        }

        String sessionCode = (String) session.getAttribute(P2PConstant.PHONE_VERIFY_CODE+phone);
        if (!sessionCode.equals(code)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("手机验证码不匹配");
            return resultInfo;
        }

        try {
            //调用server的注册方法；
            basUserService.saveBasUser(phone, password);

            // 移除session 中存储的key 信息
            session.removeAttribute(P2PConstant.PICTURE_VERIFY_CODE);
            session.removeAttribute(P2PConstant.PHONE_VERIFY_CODE + phone);
            session.removeAttribute(P2PConstant.PHONE_VERIFY_CODE_EXPIRE_TIME + phone);
        } catch (ParamsExcetion e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(e.getErrorMsg());
        }catch (Exception e){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }
        return resultInfo;
    }


    //用户普通登录
    @RequestMapping("userLogin")
    @ResponseBody
    public ResultInfo userLogin(String phone, String password, HttpServletRequest request){
        ResultInfo resultInfo = new ResultInfo();

        try {
           BasUser basUser = basUserService.userLogin(phone,password);
            request.getSession().setAttribute("userInfo",basUser);

        }catch (ParamsExcetion e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(e.getErrorMsg());
        }catch (Exception e){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }
        return resultInfo;
    }



    //快捷登录

    @RequestMapping("quickLogin")
    @ResponseBody
    public ResultInfo quickLogin(String phone,String picCode, String code, HttpSession session){
        ResultInfo resultInfo = new ResultInfo();

        String sessionPicCode = (String) session.getAttribute(P2PConstant.PICTURE_VERIFY_CODE);
        if (StringUtils.isBlank(sessionPicCode)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("图片验证码已失效");
            return resultInfo;
        }

        if (! picCode.equals(sessionPicCode)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("图片验证码不对。。。");
            return resultInfo;
        }

        // 发送验证码时间
        Date sessionTime = (Date) session.getAttribute(P2PConstant.PHONE_VERIFY_CODE_EXPIRE_TIME+phone);
        if (sessionTime == null){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("手机验证码已失效!");
            return  resultInfo;
        }

        //对比验证码失效时间
        Date currTime = new Date();
        long time = (currTime.getTime()-sessionTime.getTime())/1000;
        if (time>180){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("手机验证码已失效!");
            return  resultInfo;
        }

        String sessionCode= (String) session.getAttribute(P2PConstant.PHONE_VERIFY_CODE+phone);
        if(!sessionCode.equals(code)){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg("手机验证码不正确!");
            return  resultInfo;
        }

        try {
           BasUser basUser = basUserService.quickLogin(phone);

           session.setAttribute("userInfo",basUser);
            // 移除session 中存储的key 信息
            session.removeAttribute(P2PConstant.PICTURE_VERIFY_CODE);
            session.removeAttribute(P2PConstant.PHONE_VERIFY_CODE_EXPIRE_TIME+phone);
            session.removeAttribute(P2PConstant.PHONE_VERIFY_CODE+phone);

        }catch (ParamsExcetion e) {
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(e.getErrorMsg());
        }catch (Exception e){
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }
        return resultInfo;
    }


    /**
     * 退出。。。。
     * @param request
     * @return
     */
    @RequestMapping("exit")
    public String exit(HttpServletRequest request){
        //清空session用户信息
        request.getSession().removeAttribute("userInfo");

        request.setAttribute("ctx",request.getContextPath());
        return "login";
    }



    /**
     * 用户认证校验
     */
    @RequestMapping("userAuthCheck")
    @ResponseBody
    public ResultInfo userAuthCheck(HttpServletRequest request){
        BasUser basUser = (BasUser) request.getSession().getAttribute("userInfo");

        ResultInfo resultInfo =basUserSecurityService.userAuthCheck(basUser.getId());

        return resultInfo;
    }


    /**
     * 转发到用户认证页面
     * @param request
     * @return
     */
    @RequestMapping("auth")
    public String toAuthPage(HttpServletRequest request){
        request.setAttribute("ctx",request.getContextPath());

        return "user/auth";
    }


    /**
     * 用户认证
     */
    @RequestMapping("userAuth")
    @ResponseBody
    public ResultInfo doUserAuth(String realName,String idCard,String businessPassword,String confirmPassword,HttpSession session){
        ResultInfo resultInfo = new ResultInfo();
        //取出用户ID
        BasUser basUser = (BasUser) session.getAttribute("userInfo");
        try {
            basUserSecurityService.doUserAuth(realName,idCard,businessPassword,confirmPassword,basUser.getId());
        }catch (ParamsExcetion e) {
            e.printStackTrace();
            resultInfo.setMsg(e.getErrorMsg());
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
        }
        return resultInfo;
    }



}
