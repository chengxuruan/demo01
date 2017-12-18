package com.shsxt.xm.web.controller;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.api.query.BusItemInvestQuery;
import com.shsxt.xm.api.service.IBusItemInvestService;
import com.shsxt.xm.api.utils.PageList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/11.
 */
@Controller
@RequestMapping("busItemInvest")
public class BusItemInvestController {

    @Resource
    private IBusItemInvestService busItemInvestService;


    //贷款项目投资分页列表信息查询
    @RequestMapping("queryBusItemInvestsByItemId")
    @ResponseBody
    public PageList queryBusItemInvestsByItemId(BusItemInvestQuery busItemInvestQuery){

        return busItemInvestService.queryBusItemInvestsByParams(busItemInvestQuery);
    }


    //立即投资
    @RequestMapping("userInvest")
    @ResponseBody
    public ResultInfo userInvest(Integer itemId, BigDecimal amount, String businessPassword, HttpSession session){
        ResultInfo resultInfo = new ResultInfo();

        try {
            BasUser basUser = (BasUser) session.getAttribute("userInfo");
            busItemInvestService.addBusItemInvest(basUser.getId(),itemId, amount, businessPassword );
        }catch (ParamsExcetion e) {
            e.printStackTrace();
            resultInfo.setMsg(e.getErrorMsg());
            resultInfo.setCode(e.getErrorCode());
        }catch (Exception e){
            e.printStackTrace();
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
        }

        return resultInfo;
    }


    /**
     * 投资趋势查询
     */
    @RequestMapping("queryInvestInfoByUserId")
    @ResponseBody
    public Map<String,Object[]> queryInvestInfoByUserId(HttpSession session) {
        BasUser basUser = (BasUser) session.getAttribute("userInfo");
        return busItemInvestService.queryInvestInfoByUserId(basUser.getId());
    }
}
