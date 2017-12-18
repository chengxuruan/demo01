package com.shsxt.xm.web.controller;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.exceptions.ParamsExcetion;
import com.shsxt.xm.api.model.ResultInfo;
import com.shsxt.xm.api.po.*;
import com.shsxt.xm.api.query.BasItemQuery;
import com.shsxt.xm.api.service.*;
import com.shsxt.xm.api.utils.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Administrator on 2017/12/10.
 */
@Controller
@RequestMapping("basItem")
public class BasItemController {

    @Autowired(required = false)
    private IBasItemService basItemService;

    @Resource
    private IBusAccountService busAccountService;

    @Resource
    private IBasUserSecurityService basUserSecurityService;

    @Resource
    private IBusItemLoanService busItemLoanService;

    @Resource
    private ISysPictureService sysPictureService;

    /**
     * @param request
     * @return
     * 视图跳转
     */
    @RequestMapping("list")
    public String toBasItemListPage(HttpServletRequest request){
        request.setAttribute("ctx",request.getContextPath());
        return "item/invest_list";
    }

    //分页查询列表数据
    @RequestMapping("queryBasItemsByParams")
    @ResponseBody
    public PageList queryBasItemsByParams(BasItemQuery basItemQuery){

        return basItemService.queryBasItemsByParams(basItemQuery);
    }


    /**
     * 修改 投标状态
     * @param itemId
     * @return
     */
    @RequestMapping("updateBasItemStatusToOpen")
    @ResponseBody
    public ResultInfo updateBasItemStatusToOpen(Integer itemId){
        ResultInfo resultInfo = new ResultInfo();

        try {
            basItemService.updateBasItemStatusToOpen(itemId);
        }catch (ParamsExcetion e){
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(e.getErrorMsg());
        }catch (Exception e){
            e.printStackTrace();
            resultInfo.setCode(P2PConstant.OPS_FAILED_CODE);
            resultInfo.setMsg(P2PConstant.OPS_FAILED_MSG);
        }
        return resultInfo;
    }

    /**
     * 视图跳转详情页分页展示
     */
    @RequestMapping("itemDetailPage")
    public String itemDetailPage(Integer itemId, ModelMap modelMap, HttpServletRequest request){

        //投资项目基本信息查询
       BasItemDto basItemDto = basItemService.queryBasItemByItemId(itemId);
       //取出session中的用户对象
       BasUser basUser = (BasUser) request.getSession().getAttribute("userInfo");

       if (basUser != null){
        //登录用户账户基本 账户可用余额查询显示
          BusAccount busAccount = busAccountService.queryBusAccountByUserId(basUser.getId());
          //存model视图
           modelMap.addAttribute("busAccount",busAccount);
       }
       //获取用户安全审核表信息
        BasUserSecurity basUserSecurity= basUserSecurityService.queryBasUserSecurityByUserId(basItemDto.getItemUserId());

        //获取项目借款信息表信息
        BusItemLoan busItemLoan=busItemLoanService.queryBusItemLoanByItemId(itemId);


        List<SysPicture> sysPictures=sysPictureService.querySysPicturesByItemId(itemId);

        //存model
        modelMap.addAttribute("loanUser",basUserSecurity);
        modelMap.addAttribute("busItemLoan",busItemLoan);
        modelMap.addAttribute("ctx",request.getContextPath());
        modelMap.addAttribute("item",basItemDto);
        modelMap.addAttribute("pics",sysPictures);

        //跳转页面
        return "item/details";
    }
}
