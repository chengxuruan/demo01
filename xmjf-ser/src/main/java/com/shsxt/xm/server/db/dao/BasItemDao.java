package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.server.base.BaseDao;
import org.apache.ibatis.annotations.Param;

/**
 * Created by Administrator on 2017/12/10.
 */
public interface BasItemDao extends BaseDao<BasItemDto>{

   public int updateBasItemStatusToOpen(@Param("itemId") Integer itemId);
}