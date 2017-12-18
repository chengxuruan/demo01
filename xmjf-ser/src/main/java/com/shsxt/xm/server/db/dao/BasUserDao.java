package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BasUser;
import com.shsxt.xm.server.base.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface BasUserDao extends BaseDao<BasUser> {
    public BasUser queryBasUserById(@Param("id") Integer id);

    public BasUser queryBasUserByPhone(@Param("phone") String phone);

}