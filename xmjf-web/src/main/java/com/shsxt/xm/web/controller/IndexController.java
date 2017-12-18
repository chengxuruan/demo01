package com.shsxt.xm.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2017/12/7.
 */
@Controller
public class IndexController {

    /**
     * 返回主页视图
     * @return
     */
    @RequestMapping("index")
    public String index(HttpServletRequest request){
        String path = request.getContextPath();
        request.setAttribute("ctx",path);

        return "index";
    }


    /**
     * 返回登录视图
     * @return
     */
    @RequestMapping("login")
    public String login(HttpServletRequest request){
        String path = request.getContextPath();
        request.setAttribute("ctx",path);

        return "login";
    }


    /**
     * 返回注册视图
     * @return
     */
    @RequestMapping("register")
    public String register(HttpServletRequest request){
        String path = request.getContextPath();
        request.setAttribute("ctx",path);

        return "register";
    }


    /**
     * 返回快捷登录视图
     */
    @RequestMapping("quickLoginPage")
    public String quickLoginPage(HttpServletRequest request){
        String path = request.getContextPath();
        request.setAttribute("ctx",path);

        return "quick_login";
    }
}
