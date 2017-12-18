package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusIncomeStat;
import com.shsxt.xm.server.base.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface BusIncomeStatDao extends BaseDao<BusIncomeStat> {

    public BusIncomeStat queryBusIncomeStatByUserId(@Param("userId") Integer userId);
}