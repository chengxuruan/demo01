package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusAccount;
import com.shsxt.xm.server.base.BaseDao;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Map;

public interface BusAccountDao extends BaseDao<BusAccount> {

    //查询登录用户账户金额信息
    @Select("select id, user_id as userId, total, usable, cash, frozen, wait, repay"+
    " from bus_account where user_id = #{userId}")
    public BusAccount queryBusAccountByUserId(@Param("userId") Integer userId);

    //用户投资详情展示
    public Map<String,BigDecimal> queryAccountInfoByUserId(@Param("userId") Integer userId);
}