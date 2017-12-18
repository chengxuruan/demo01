package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.SysPicture;
import com.shsxt.xm.server.base.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2017/12/11.
 */
public interface SysPictureDao extends BaseDao<SysPicture>{
    List<SysPicture> querySysPicturesByItemId(@Param("itemId") Integer itemId);
}
