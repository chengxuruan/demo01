package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusAccountRecharge;
import com.shsxt.xm.server.base.BaseDao;

/**
 * Created by Administrator on 2017/12/13.
 */
public interface BusAccountRechargeDao extends BaseDao<BusAccountRecharge> {

    public BusAccountRecharge queryBusAccountRechargeByOrderNo(String outOrderNo);
}
