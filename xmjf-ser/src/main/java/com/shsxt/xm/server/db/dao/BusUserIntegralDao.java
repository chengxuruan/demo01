package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusIncomeStat;
import com.shsxt.xm.api.po.BusUserIntegral;
import com.shsxt.xm.server.base.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface BusUserIntegralDao extends BaseDao<BusUserIntegral> {

    public BusUserIntegral queryBusUserIntegralByUserId(@Param("userId") Integer userId);
}