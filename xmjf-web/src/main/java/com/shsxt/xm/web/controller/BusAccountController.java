package com.shsxt.xm.web.controller;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.PayDto;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.query.AccountRechargeQuery;
import com.shsxt.xm.api.service.IBusAccountRechargeService;
import com.shsxt.xm.api.service.IBusAccountService;
import com.shsxt.xm.api.utils.PageList;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/13.
 */
@Controller
@RequestMapping("account")
public class BusAccountController {


    @Resource
    private IBusAccountService busAccountService;


    @Resource
    private IBusAccountRechargeService busAccountRechargeService;

    //跳转到充值页面
    @RequestMapping("rechargePage")
    public String toAccountRechargePage(HttpServletRequest request){
        request.setAttribute("ctx",request.getContextPath());
        return "user/recharge";
    }


    /**
     * 发起支付请求转发页面
     * @param amount
     * @param picCode
     * @param bussinessPassword
     */
    @RequestMapping("doAccountRechargeToRechargePage")
    public String doAccountRechargeToRechargePage(BigDecimal amount,String picCode, String bussinessPassword, HttpServletRequest request, Model model){
        model.addAttribute("ctx",request.getContextPath());
        String sessionPicCode = (String) request.getSession().getAttribute(P2PConstant.PICTURE_VERIFY_CODE);

        if (StringUtils.isBlank(sessionPicCode)){
            System.out.println("验证码不匹配!!!");
            return "user/pay";
        }

        if(!picCode.equals(sessionPicCode)) {
            System.out.println("验证码不匹配!");
            return "user/pay";
        }

        BasUser basUser = (BasUser) request.getSession().getAttribute("userInfo");
        //构建支付请求参数
        PayDto payDto = busAccountService.addRechargeRequestInfo(amount,bussinessPassword,basUser.getId());
        model.addAttribute("pay",payDto);

        return "user/pay";
    }


    /**
     * 订单支付回调地址
     * @param totalFee
     * @param outOrderNo
     * @param sign
     * @param tradeNo
     * @param tradeStatus
     * @param session
     * @return
     */
    @RequestMapping("callback")
    public String  callback(@RequestParam(defaultValue = "0",name = "total_fee") BigDecimal totalFee,
                            @RequestParam(defaultValue = "1",name = "out_order_no") String outOrderNo,
                            @RequestParam(defaultValue = "2017",name = "trade_no") String tradeNo, String sign,
                            @RequestParam(defaultValue ="success",name = "trade_status") String tradeStatus,
                            HttpSession session, RedirectAttributes redirectAttributes){


        BasUser basUser = (BasUser) session.getAttribute("userInfo");

        try {
            //支付回调
            busAccountService.updateAccountRecharge(basUser.getId(),totalFee,outOrderNo,sign,tradeNo,tradeStatus);
            redirectAttributes.addAttribute("result","success");
        }catch (ParamsExcetion e) {
            e.printStackTrace();
            System.out.println(e.getErrorMsg());
            redirectAttributes.addAttribute("result","failed");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("系统异常!");
            redirectAttributes.addAttribute("result","failed");
        }
        return "redirect:/account/result";// 重定向到controller 指定方法

    }

    //视图跳转
    @RequestMapping("result")
    public String result(String result,Model model,HttpServletRequest request){
        model.addAttribute("ctx",request.getContextPath());
        model.addAttribute("result",result);

        return "result";
    }

    //视图跳转 订单详情列表
    @RequestMapping("rechargeRecodePage")
    public String rechargeRecodePage(HttpServletRequest request){

        request.setAttribute("ctx",request.getContextPath());
        return "user/recharge_record";
    }


    //订单详情列表展示
    @RequestMapping("queryRechargeRecodesByUserId")
    @ResponseBody
    public PageList queryRechargeRecodesByUserId(AccountRechargeQuery accountRechargeQuery,HttpSession session){
        BasUser basUser= (BasUser) session.getAttribute("userInfo");

        //AccountRechargeQuery accountRechargeQuery=new AccountRechargeQuery();
        //accountRechargeQuery.setPageNum(pageNum);
        accountRechargeQuery.setUserId(basUser.getId());

        return busAccountRechargeService.queryRechargeRecodesByParams(accountRechargeQuery);
    }

    /**
     * 跳转账户详情
     */
    @RequestMapping("accountInfoPage")
    public String toAccountInfoPage(HttpServletRequest request){
        request.setAttribute("ctx",request.getContextPath());

        return "user/account_info";
    }


    /**
     * 用户投资详情展示
     */
    @RequestMapping("accountInfo")
    @ResponseBody
    public Map<String,Object> queryAccountInfoByUserId(HttpSession session){
        BasUser basUser = (BasUser) session.getAttribute("userInfo");

        return busAccountService.queryAccountInfoByUserId(basUser.getId());
    }

}