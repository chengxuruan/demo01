package com.shsxt.xm.server.service;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.constant.YunTongFuConstant;
import com.shsxt.xm.api.dto.AccountDto;
import com.shsxt.xm.api.dto.PayDto;
import com.shsxt.xm.api.po.BasUserSecurity;
import com.shsxt.xm.api.po.BusAccount;
import com.shsxt.xm.api.po.BusAccountLog;
import com.shsxt.xm.api.po.BusAccountRecharge;
import com.shsxt.xm.api.service.IBasUserSecurityService;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.service.IBusAccountService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.server.db.dao.BusAccountDao;
import com.shsxt.xm.server.db.dao.BusAccountLogDao;
import com.shsxt.xm.server.db.dao.BusAccountRechargeDao;
import com.shsxt.xm.server.utils.MD5;
import com.shsxt.xm.server.utils.Md5Util;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2017/12/11.
 */
@Service
public class BusAccountServiceImpl implements IBusAccountService{

    @Autowired(required = false)
    private BusAccountDao busAccountDao;


    @Autowired(required = false)
    private BusAccountRechargeDao busAccountRechargeDao;


    @Resource
    private IBasUserSecurityService basUserSecurityService;


    @Resource
    private IBasUserService basUserService;


    @Autowired(required = false)
    private BusAccountLogDao busAccountLogDao;

    /**
     * 账户查询
     * @param id
     * @return
     */
    @Override
    public BusAccount queryBusAccountByUserId(Integer id) {
        return busAccountDao.queryBusAccountByUserId(id);
    }


    /**
     * 构建支付请求参数信息
     */
    @Override
    public PayDto addRechargeRequestInfo(BigDecimal amount, String bussinessPassword, Integer userId) {

        //参数校验
        checkParams(amount,bussinessPassword,userId);

        //构建支付请求参数信息
        BusAccountRecharge busAccountRecharge = new BusAccountRecharge();
        busAccountRecharge.setAddtime(new Date());
        busAccountRecharge.setFeeAmount(BigDecimal.ZERO);
        String orderNo = com.shsxt.xm.server.utils.StringUtils.getOrderNo();
        busAccountRecharge.setOrderNo(orderNo);
        busAccountRecharge.setFeeRate(BigDecimal.ZERO);
        busAccountRecharge.setRechargeAmount(amount);
        busAccountRecharge.setRemark("PC端用户充值");
        busAccountRecharge.setResource("PC端用户充值");
        busAccountRecharge.setStatus(2);
        busAccountRecharge.setType(3);
        busAccountRecharge.setUserId(userId);
        AssertUtil.isTrue(busAccountRechargeDao.insert(busAccountRecharge)<1, P2PConstant.OPS_FAILED_MSG);

        PayDto payDto=new PayDto();

        payDto.setBody("PC端用户充值操作");
        payDto.setOrderNo(orderNo);
        payDto.setSubject("PC端用户充值操作");
        payDto.setTotalFee(amount);
        //数据加密
        String md5Sign=buildMd5Sign(payDto);
        payDto.setSign(md5Sign);
        return payDto;
    }


    //加密
    private String buildMd5Sign(PayDto payDto) {
        StringBuffer arg = new StringBuffer();
        if(!StringUtils.isBlank(payDto.getBody())){
            arg.append("body="+payDto.getBody()+"&");
        }
        arg.append("notify_url="+payDto.getNotifyUrl()+"&");
        arg.append("out_order_no="+payDto.getOrderNo()+"&");
        arg.append("partner="+payDto.getPartner()+"&");
        arg.append("return_url="+payDto.getReturnUrl()+"&");
        arg.append("subject="+payDto.getSubject()+"&");
        arg.append("total_fee="+payDto.getTotalFee().toString()+"&");
        arg.append("user_seller="+payDto.getUserSeller());
        String tempSign= StringEscapeUtils.unescapeJava(arg.toString());
        Md5Util md5Util=new Md5Util();
        return md5Util.encode(tempSign+payDto.getKey(),"");
    }

    /**
     * 基本参数校验
     * @param amount
     * @param bussinessPassword
     * @param userId
     */
    private void checkParams(BigDecimal amount, String bussinessPassword, Integer userId) {
        //金额不能为负数且只能输入数字
        AssertUtil.isTrue(amount.compareTo(BigDecimal.ZERO)<=0,"充值金额非法!");

        BasUserSecurity basUserSecurity = basUserSecurityService.queryBasUserSecurityByUserId(userId);
        AssertUtil.isTrue(basUserSecurity==null,"用户2登录");

        AssertUtil.isTrue(StringUtils.isBlank(bussinessPassword),"交易密码不能为空!");

        //密码加密
        bussinessPassword = MD5.toMD5(bussinessPassword);

        AssertUtil.isTrue(!bussinessPassword.equals(basUserSecurity.getPaymentPassword()),"交易密码错误!");
    }


    /**
     * 订单支付回调
     * @param userId
     * @param totalFee
     * @param outOrderNo
     * @param sign
     * @param tradeNo
     * @param tradeStatus
     */
    @Override
    public void updateAccountRecharge(Integer userId, BigDecimal totalFee, String outOrderNo, String sign, String tradeNo, String tradeStatus) {
        AssertUtil.isTrue(null==userId||null==basUserService.queryBasUserById(userId),"用户未登录!");

        AssertUtil.isTrue(null==totalFee||StringUtils.isBlank(outOrderNo)||StringUtils.isBlank(sign)
                ||StringUtils.isBlank(tradeNo)||StringUtils.isBlank(tradeStatus),"回调参数异常!");

        //加密后对比
        Md5Util md5Util=new Md5Util();
        String  tempStr= md5Util.encode(outOrderNo+totalFee.toString()+tradeStatus+ YunTongFuConstant.PARTNER+YunTongFuConstant.KEY,null);
        AssertUtil.isTrue(!tempStr.equals(sign),"订单信息异常,请联系客服!");

        AssertUtil.isTrue(!tradeStatus.equals(YunTongFuConstant.TRADE_STATUS_SUCCESS),"订单支付失败!");

        //根据订单编号查询订单记录
        BusAccountRecharge busAccountRecharge = busAccountRechargeDao.queryBusAccountRechargeByOrderNo(outOrderNo);

        AssertUtil.isTrue(null==busAccountRecharge,"订单记录不存在，请联系管理员!");

        AssertUtil.isTrue(busAccountRecharge.getStatus().equals(1),"该订单已支付!");
        AssertUtil.isTrue(busAccountRecharge.getStatus().equals(0),"订单异常，请联系客服!");
        //判断充值金额与付款金额是否相等
        AssertUtil.isTrue(busAccountRecharge.getRechargeAmount().compareTo(totalFee)!=0,"订单异常，请联系客服!");

        busAccountRecharge.setStatus(1);//修改状态
        busAccountRecharge.setActualAmount(totalFee);//实际到账金额
        busAccountRecharge.setAuditTime(new Date());

        //更新订单记录
        AssertUtil.isTrue(busAccountRechargeDao.update(busAccountRecharge)<1,P2PConstant.OPS_FAILED_MSG);//操作失败

        //修改账户记录
        BusAccount busAccount=busAccountDao.queryBusAccountByUserId(userId);
        busAccount.setCash(busAccount.getCash().add(totalFee));// 设置可提现金额
        busAccount.setTotal(busAccount.getTotal().add(totalFee));// 设置总金额
        busAccount.setUsable(busAccount.getUsable().add(totalFee));
        AssertUtil.isTrue(busAccountDao.update(busAccount)<1,P2PConstant.OPS_FAILED_MSG);

        // 添加账户信息变动操作日志
        BusAccountLog busAccountLog=new BusAccountLog();
        busAccountLog.setAddtime(new Date());
        busAccountLog.setBudgetType(1);// 收入日志
        busAccountLog.setCash(busAccount.getCash());// 设置可提现金额
        busAccountLog.setFrozen(busAccount.getFrozen());
        busAccountLog.setOperMoney(totalFee);// 设置操作金额
        busAccountLog.setOperType("user_recharge_success");
        busAccountLog.setRemark("用户充值");
        busAccountLog.setRepay(busAccount.getRepay());
        busAccountLog.setTotal(busAccount.getTotal());
        busAccountLog.setUsable(busAccount.getUsable());
        busAccountLog.setUserId(userId);
        busAccountLog.setWait(busAccount.getWait());
        //添加日志表记录
        AssertUtil.isTrue(busAccountLogDao.insert(busAccountLog)<1,P2PConstant.OPS_FAILED_MSG);
    }

    /**
     * 用户投资详情展示
     */
    @Override
    public Map<String, Object> queryAccountInfoByUserId(Integer userId) {
        //查询返回的数据
        Map<String,BigDecimal> map = busAccountDao.queryAccountInfoByUserId(userId);

        List<AccountDto> list = new ArrayList<>();
        Map<String,Object> target = new HashMap<>();
        //遍历  组装返回结果
        if (null != map && !map.isEmpty()){
            for (Map.Entry<String,BigDecimal> entry : map.entrySet()){

                if (entry.getKey().equals("总金额")){
                    target.put("data2",entry.getValue());// 总资产
                }
                AccountDto accountDto = new AccountDto();
                accountDto.setName(entry.getKey());
                accountDto.setY(entry.getValue());
                list.add(accountDto);
            }
        }
        target.put("data1",list);// 资金类型value

        return target;
    }
}
