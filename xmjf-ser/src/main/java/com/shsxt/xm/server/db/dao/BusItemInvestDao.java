package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.dto.InvestDto;
import com.shsxt.xm.api.po.BusItemInvest;
import com.shsxt.xm.server.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/12/11.
 */
public interface BusItemInvestDao extends BaseDao<BusItemInvest>{

   public int queryUserIsInvestIsNewItem(@Param("userId") Integer userId);

    public List<InvestDto> queryInvestInfoByUserId(@Param("userId") Integer userId);
}
