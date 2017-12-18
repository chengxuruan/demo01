package com.shsxt.xm.server.service;

import com.github.pagehelper.PageHelper;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.dto.InvestDto;
import com.shsxt.xm.api.po.*;
import com.shsxt.xm.api.query.BusItemInvestQuery;
import com.shsxt.xm.api.service.IBasUserSecurityService;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.service.IBusItemInvestService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.PageList;
import com.shsxt.xm.api.utils.RandomCodesUtils;
import com.shsxt.xm.server.constant.ItemStatus;
import com.shsxt.xm.server.db.dao.*;
import com.shsxt.xm.server.utils.Calculator;
import com.shsxt.xm.server.utils.MD5;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/11.
 */
@Service
public class BusItemInvestServiceImpl implements IBusItemInvestService {


    @Autowired(required = false)
    private BusItemInvestDao busItemInvestDao;

    @Resource
    private IBasUserService basUserService;

    @Resource
    private IBasUserSecurityService basUserSecurityService;

    @Resource
    private BasItemDao basItemDao;

    @Resource
    private BusAccountDao busAccountDao;

    @Resource
    private BusUserStatDao busUserStatDao;

    @Resource
    private BusAccountLogDao busAccountLogDao;


    @Resource
    private BusIncomeStatDao busIncomeStatDao;

    @Resource
    private BusUserIntegralDao busUserIntegralDao;

    @Resource
    private BusIntegralLogDao busIntegralLogDao;



    //贷款项目投资列表信息查询
    @Override
    public PageList queryBusItemInvestsByParams(BusItemInvestQuery busItemInvestQuery) {

        PageHelper.startPage(busItemInvestQuery.getPageNum(),busItemInvestQuery.getPageSize());

        List<BusItemInvest> busItemInvestList = busItemInvestDao.queryForPage(busItemInvestQuery);
        return new PageList(busItemInvestList);
    }

    //项目投标(立即投资)
    @Override
    public void addBusItemInvest(Integer userId, Integer itemId, BigDecimal amount, String businessPassword) {

        //.参数基本校验
        checkInvestParams(userId, itemId,amount, businessPassword);

       /* bas_item         项目表
        bus_item_invest  项目投资表
        bus_user_stat    用户统计表
        bus_account      用户账户表
        bus_account_log  用户账户操作日志表
        bus_income_stat   用户收益信息表
        bus_user_integral  用户积分表
        bus_integral_log  积分操作日志表*/
        //3.1 用户投资记录信息更新
        BusItemInvest busItemInvest = new BusItemInvest();
        busItemInvest.setActualCollectAmount(BigDecimal.ZERO);
        busItemInvest.setActualCollectInterest(BigDecimal.ZERO);
        busItemInvest.setActualCollectPrincipal(BigDecimal.ZERO);
        // 实际未收总额 本金+利息
        BasItem basItem = basItemDao.queryById(itemId);
        BigDecimal lx = Calculator.getInterest(amount,basItem);//利息
        busItemInvest.setActualUncollectAmount(amount.add(lx));//实际未收总额
        busItemInvest.setActualUncollectInterest(lx);//实际未收利息
        busItemInvest.setActualUncollectPrincipal(amount);//实际未收本金
        busItemInvest.setAdditionalRateAmount(BigDecimal.ZERO);
        busItemInvest.setAddtime(new Date());
        busItemInvest.setCollectAmount(amount.add(lx));//应收总额
        busItemInvest.setCollectInterest(lx);
        busItemInvest.setCollectPrincipal(amount);
        busItemInvest.setInvestAmount(amount);
        busItemInvest.setInvestCurrent(1);//1-定期 2-活期'
        //回调代码过滤
        busItemInvest.setInvestDealAmount(amount);
        String oderNo="SXT_TZ_"+ RandomCodesUtils.createRandom(false,11);//随机生成投资订单号
        busItemInvest.setInvestOrder(oderNo);//投资订单号
        busItemInvest.setInvestStatus(0);//投资状态 0-默认状态 1复审通过 2失败 3-投资已还款',
        busItemInvest.setInvestType(1);// pc 端投资
        busItemInvest.setItemId(itemId);
        busItemInvest.setUpdatetime(new Date());
        busItemInvest.setUserId(userId);
        //添加项目投资表记录
        AssertUtil.isTrue(busItemInvestDao.insert(busItemInvest)<1, P2PConstant.OPS_FAILED_MSG);

        //3.2 用户统计信息更新
        BusUserStat busUserStat = busUserStatDao.queryBusUserStatByUserId(userId);
        busUserStat.setInvestCount(busUserStat.getInvestCount()+1);//更新投资次数
        busUserStat.setInvestAmount(busUserStat.getInvestAmount().add(amount));//投资投资累计金额
        //更新用户统计表
        AssertUtil.isTrue(busUserStatDao.update(busUserStat)<1,P2PConstant.OPS_FAILED_MSG);

        //3.3 用户账户信息更新
        BusAccount busAccount = busAccountDao.queryBusAccountByUserId(userId);
        busAccount.setTotal(busAccount.getTotal().add(lx));//总金额
        busAccount.setUsable(busAccount.getUsable().add(amount.negate()));//可用金额
        busAccount.setCash(busAccount.getCash().add(amount.negate()));//可提现金额
        busAccount.setFrozen(busAccount.getFrozen().add(amount));// 冻结金额
        busAccount.setWait(busAccount.getWait().add(amount));//代收金额
        //更新账户记录
        AssertUtil.isTrue(busAccountDao.update(busAccount)<1,P2PConstant.OPS_FAILED_MSG);


        //3.4 用户账户操作日志表  更新金额字段信息
        BusAccountLog busAccountLog = new BusAccountLog();
        busAccountLog.setUserId(userId);
        busAccountLog.setOperType("用户投标");
        busAccountLog.setOperMoney(amount);//总收益
        busAccountLog.setBudgetType(2);//类型：1-收入，2-支出'
        busAccountLog.setTotal(busAccount.getTotal());//总金额
        busAccountLog.setUsable(busAccount.getUsable());//可用金额
        busAccountLog.setFrozen(busAccount.getFrozen());
        busAccountLog.setWait(busAccount.getWait());
        busAccountLog.setCash(busAccount.getCash());
        busAccountLog.setRepay(busAccount.getRepay());//待还金额
        busAccountLog.setRemark("用户投标成功!");
        busAccountLog.setAddtime(new Date());
        AssertUtil.isTrue(busAccountLogDao.insert(busAccountLog)<1,P2PConstant.OPS_FAILED_MSG);


        //3.5 用户收益信息表字段更新
        BusIncomeStat busIncomeStat = busIncomeStatDao.queryBusIncomeStatByUserId(userId);
        busIncomeStat.setWaitIncome(busIncomeStat.getWaitIncome().add(lx));//已赚收益
        busIncomeStat.setTotalIncome(busIncomeStat.getTotalIncome().add(lx));//代收收益
        AssertUtil.isTrue(busIncomeStatDao.update(busIncomeStat)<1,P2PConstant.OPS_FAILED_MSG);

        //3.6 用户积分表更新 增加用户100 积分
        BusUserIntegral busUserIntegral = busUserIntegralDao.queryBusUserIntegralByUserId(userId);
        busUserIntegral.setTotal(busUserIntegral.getTotal()+100);
        busUserIntegral.setUsable(busUserIntegral.getUsable()+100);
        // 用户积分操作表更新
        AssertUtil.isTrue(busUserIntegralDao.update(busUserIntegral)<1,P2PConstant.OPS_FAILED_MSG);


        //添加积分增加记录日志
        BusIntegralLog busIntegralLog=new BusIntegralLog();
        busIntegralLog.setWay("用户投标");
        busIntegralLog.setUserId(userId);
        busIntegralLog.setStatus(0);
        busIntegralLog.setAddtime(new Date());
        AssertUtil.isTrue(busIntegralLogDao.insert(busIntegralLog)<1,P2PConstant.OPS_FAILED_MSG);


        // 更新贷款项目信息   基本项目表信息更新
        basItem.setItemOngoingAccount(basItem.getItemOngoingAccount().add(amount));//项目进行中金额

        basItem.setInvestTimes(basItem.getInvestTimes()+1);//投标次数
        //如果借款金额==进行中的金额
        if (basItem.getItemAccount().compareTo(basItem.getItemOngoingAccount())==0){
            basItem.setItemStatus(ItemStatus.FULL_COMPLETE); //满标
        }

        //更新项目投资进度
        MathContext mc = new MathContext(2, RoundingMode.HALF_DOWN);
        //进行中的金额除借款金额乘100得到一个百分比
        BigDecimal a = basItem.getItemOngoingAccount().divide(basItem.getItemAccount(),mc).multiply(BigDecimal.valueOf(100));
        System.out.println(a);

        basItem.setItemScale(basItem.getItemOngoingAccount().divide(basItem.getItemAccount(),mc).multiply(BigDecimal.valueOf(100)));

        //BasItem basItems = (BasItemDto)basItem;//强转成DTO
        //更新项目表
        AssertUtil.isTrue(basItemDao.update((BasItemDto)basItem)<1,P2PConstant.OPS_FAILED_MSG );
    }

    /**
     *投资趋势查询
     */
    @Override
    public Map<String, Object[]> queryInvestInfoByUserId(Integer userId) {
        Map<String,Object[]> map = new HashMap<>();
        List<InvestDto> investDtos = busItemInvestDao.queryInvestInfoByUserId(userId);
        String[] months;//存放月份
        BigDecimal[] totals; //存放每个月的总金额

        if (!CollectionUtils.isEmpty(investDtos)){
            months = new String[investDtos.size()];
            totals = new BigDecimal[investDtos.size()];

            for (int i=0; i<investDtos.size(); i++){
                InvestDto investDto = investDtos.get(i);

                months[i] = investDto.getMonth();
                totals[i] = investDto.getTotal();
            }
            map.put("data1",months);
            map.put("data2",totals);
        }
        return map;
    }


    /**
     *  参数基本校验
     */
    private void checkInvestParams(Integer userId, Integer itemId, BigDecimal amount, String businessPassword) {
        //用户是否登录校验
        AssertUtil.isTrue(null==userId||userId==0||null==basUserService.queryBasUserById(userId),"用户未登录或不存在该用户!");
         //交易密码校验
        AssertUtil.isTrue(StringUtils.isBlank(businessPassword),"交易密码不能为空!");

        BasUserSecurity basUserSecurity = basUserSecurityService.queryBasUserSecurityByUserId(userId);
        businessPassword = MD5.toMD5(businessPassword);//密码加密后对比
        AssertUtil.isTrue(!businessPassword.equals(basUserSecurity.getPaymentPassword()),"交易密码不正确！！");

        //投资项目存在性校验
        AssertUtil.isTrue(null==amount||amount.compareTo(BigDecimal.ZERO)<=0,"投资金额非法!!!");

        BasItem basItem = basItemDao.queryById(itemId);//项目表
        AssertUtil.isTrue(null==itemId||itemId==0||null==basItem,"投资项目不存在!");

        //是否为移动端校验(仅限非移动端)
        AssertUtil.isTrue(basItem.getMoveVip().equals(1),"移动端专享项目，pc端不能进行投资操作!");
        //投资项目开放性校验
        AssertUtil.isTrue(!basItem.getItemStatus().equals(10),"该项目处理未开放状态，暂时不能执行投资操作!");
        //投资项目是否满标校验
        AssertUtil.isTrue(basItem.getItemStatus().equals(20),"项目已满标，不能再进行投资操作!");

        //账户金额合法性校验
        BusAccount busAccount =busAccountDao.queryBusAccountByUserId(userId);//账户表
        AssertUtil.isTrue(busAccount.getUsable().compareTo(BigDecimal.ZERO)<=0,"账户金额不存在，请先执行充值操作!");

        //投资金额合法性校验
        BigDecimal singleMinInvestmet = basItem.getItemSingleMinInvestment();
        if (singleMinInvestmet.compareTo(BigDecimal.ZERO)>0){//如果有最小余额；//小于账户可用余额
            AssertUtil.isTrue(busAccount.getUsable().compareTo(singleMinInvestmet)<0,"账户余额小于当前投资项目最小投资金额，请先执行充值操作!");
        }

        // 项目剩余金额合法性校验  // 余额=项目总金额-进行中金额
        BigDecimal syAmount = basItem.getItemAccount().add(basItem.getItemOngoingAccount().negate());
        if (singleMinInvestmet.compareTo(BigDecimal.ZERO)>0){
            AssertUtil.isTrue(syAmount.compareTo(singleMinInvestmet)<0,"项目已满标!");
            AssertUtil.isTrue(amount.compareTo(singleMinInvestmet)<0,"单笔投资不能小于最小投资金额");
        }

        //最大投资存在   投资金额<=最大投资金额
        BigDecimal singleMaxInvestment = basItem.getItemSingleMaxInvestment();
        if (singleMaxInvestment.compareTo(BigDecimal.ZERO)>0){
            AssertUtil.isTrue(amount.compareTo(singleMaxInvestment)>0,"投资金额不能大于单笔投资最大金额!");
        }
        //新手标重复投资记录校验
        AssertUtil.isTrue(busItemInvestDao.queryUserIsInvestIsNewItem(userId)>0,"新手标不能进行重复投资操作!");
    }
}
