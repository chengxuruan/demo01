package com.shsxt.xm.server.service;

import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.po.*;
import com.shsxt.xm.api.service.IBasUserService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.RandomCodesUtils;
import com.shsxt.xm.server.db.dao.*;
import com.shsxt.xm.server.utils.DateUtils;
import com.shsxt.xm.server.utils.MD5;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/12/7.
 */
@Service
public class BasUserServiceImpl implements IBasUserService {

    @Autowired(required = false)
    private BasUserDao basUserDao;


   private BasUserInfoDao basUserInfoDao ;

    @Autowired(required = false)
    public BasUserServiceImpl(BasUserInfoDao basUserInfoDao) {
        this.basUserInfoDao = basUserInfoDao;
    }


   @Autowired(required = false)
  // @Resource(name = "basUserSecurityDao")
    private BasUserSecurityDao basUserSecurityDao;

    @Autowired(required = false)
    private BusAccountDao busAccountDao;

    @Autowired(required = false)
    private BusUserIntegralDao busUserIntegralDao;

    @Autowired(required = false)
    private BusIncomeStatDao busIncomeStatDao;

    /**
     * bus_user_stat
     * bas_experienced_gold
     * sys_app_settings
     * sys_log
     */

    @Autowired(required = false)
    private BusUserStatDao busUserStatDao;

    @Autowired(required = false)
    private  BasExperiencedGoldDao basExperiencedGoldDao;

    @Autowired(required = false)
    private SysLogDao sysLogDao;

    @Override
    public BasUser queryBasUserById(Integer id) {
        return basUserDao.queryBasUserById(id);
    }

    @Override
    public BasUser queryBasUserByPhone(String phone) {
        return basUserDao.queryBasUserByPhone(phone);
    }

    //注册所有关联用户表的bean
    @Override
    public void saveBasUser(String phone, String password) {
        Integer userId = initBasUser(phone,password);

        //用户信息扩展表记录添加
        initBasUserInfo(userId);

        //用户安全信息表
        initBasUserSecurity(userId);

        //用户账户表记录信息
        initBusAccount(userId);

        //用户积分记录
        initBusUserIntegral(userId);

        //用户收益表记录
        initBusIncomeStat(userId);

        //用户统计表
        initBusUserStat(userId);
       
        //注册体验金表
        initBasExperiencedGold(userId);
       
        //系统日志
        initSysLog(userId);
    }

    //用户putong登录
    @Override
    public BasUser userLogin(String phone, String password) {
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(password),"密码不能为空!");

        BasUser basUser = basUserDao.queryBasUserByPhone(phone);
        AssertUtil.isTrue(null==basUser,"该用户不存在!");
        String salt = basUser.getSalt();

        password = MD5.toMD5(password+salt);
        AssertUtil.isTrue(!password.equals(basUser.getPassword()),"密码不正确!");

        return basUser;
    }



    //用户快捷登录
    @Override
    public BasUser quickLogin(String phone) {
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空");

        BasUser basUser = basUserDao.queryBasUserByPhone(phone);
        AssertUtil.isTrue(basUser == null ,"该用户不存在!");
        return basUser;

    }





    private void initSysLog(Integer userId) {
        SysLog sysLog=new SysLog();
        sysLog.setAddtime(new Date());
        sysLog.setCode("REGISTER");
        sysLog.setOperating("用户注册");
        sysLog.setResult(1);
        sysLog.setUserId(userId);
        sysLog.setType(4);
        AssertUtil.isTrue(sysLogDao.insert(sysLog)<1,P2PConstant.OPS_FAILED_MSG);
    }

    private void initBasExperiencedGold(Integer userId) {
        BasExperiencedGold basExperiencedGold=new BasExperiencedGold();
        basExperiencedGold.setAddtime(new Date());
        basExperiencedGold.setAmount(BigDecimal.valueOf(2888L));
        basExperiencedGold.setGoldName("注册体验金");
        basExperiencedGold.setRate(BigDecimal.valueOf(10));
        basExperiencedGold.setStatus(2);
        basExperiencedGold.setUsefulLife(3);
        basExperiencedGold.setUserId(userId);
        basExperiencedGold.setWay("register");
        basExperiencedGold.setExpiredTime(DateUtils.addTime(1,new Date(),30));
        AssertUtil.isTrue(basExperiencedGoldDao.insert(basExperiencedGold)<1, P2PConstant.OPS_FAILED_MSG);
    }

    private void initBusUserStat(Integer userId) {
        BusUserStat busUserStat=new BusUserStat();
        busUserStat.setUserId(userId);
        AssertUtil.isTrue(busUserStatDao.insert(busUserStat)<1,P2PConstant.OPS_FAILED_MSG);
    }

    private void initBusIncomeStat(Integer userId) {
        BusIncomeStat busIncomeStat=new BusIncomeStat();
        busIncomeStat.setUserId(userId);
        AssertUtil.isTrue(busIncomeStatDao.insert(busIncomeStat)<1,P2PConstant.OPS_FAILED_MSG);
    }

    private void initBusUserIntegral(Integer userId) {
        BusUserIntegral busUserIntegral=new BusUserIntegral();
        busUserIntegral.setUserId(userId);
        busUserIntegral.setTotal(0);
        busUserIntegral.setUsable(0);
        AssertUtil.isTrue(busUserIntegralDao.insert(busUserIntegral)<1,P2PConstant.OPS_FAILED_MSG);
    }

    private void initBusAccount(Integer userId) {
        BusAccount busAccount=new BusAccount();
        busAccount.setUserId(userId);
        busAccount.setCash(BigDecimal.ZERO);
        AssertUtil.isTrue(busAccountDao.insert(busAccount)<1,P2PConstant.OPS_FAILED_MSG);
    }

    private void initBasUserSecurity(Integer userId) {
        BasUserSecurity basUserSecurity=new BasUserSecurity();
        basUserSecurity.setUserId(userId);
        basUserSecurity.setPhoneStatus(1);
        AssertUtil.isTrue(basUserSecurityDao.insert(basUserSecurity)<1,P2PConstant.OPS_FAILED_MSG);
    }


    private void initBasUserInfo(Integer userId) {
        BasUserInfo basUserInfo=new BasUserInfo();
        basUserInfo.setUserId(userId);
        String investCode=RandomCodesUtils.createRandom(false,6);

        basUserInfo.setInviteCode(investCode);
        AssertUtil.isTrue(basUserInfoDao.insert(basUserInfo)<1,P2PConstant.OPS_FAILED_MSG);
    }


    //注册用户并返回主键

    private Integer initBasUser(String phone, String password){
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空!");
        AssertUtil.isTrue(StringUtils.isBlank(password),"密码不能为空!");
        AssertUtil.isTrue(null!= queryBasUserByPhone(phone),"该手机号已注册!");

        BasUser basUser = new BasUser();
        basUser.setAddtime(new Date());
        basUser.setMobile(phone);
        basUser.setReferer("pc");
        basUser.setStatus(1);
        basUser.setType(1);

        //获取4位的随机数
        String salt =RandomCodesUtils.createRandom(false,4);
        // 设置盐值
        basUser.setSalt(salt);
        // 密码 加盐加密
        password = MD5.toMD5(password+salt);
        basUser.setPassword(password);

        // 获取主键
        Integer sal = basUserDao.insert(basUser);
        AssertUtil.isTrue(sal<1, P2PConstant.OPS_FAILED_MSG);

        Integer userId = basUser.getId();
        //得到当前时间的字符串
        String year = new SimpleDateFormat("yyyy").format(new Date());

        //设置用户名
        basUser.setUsername("SXT_P2P"+year+userId);
        //修改用户名称
        AssertUtil.isTrue(basUserDao.update(basUser)<1,P2PConstant.OPS_FAILED_MSG);

        return userId;
  }
}
