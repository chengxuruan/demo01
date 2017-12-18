package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BusItemLoan;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2017/12/11.
 */
public interface BusItemLoanDao {

    public BusItemLoan queryBusItemLoanByItemId(@Param("itemId") Integer itemId);

}
