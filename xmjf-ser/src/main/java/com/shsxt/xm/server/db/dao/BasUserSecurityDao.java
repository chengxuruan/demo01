package com.shsxt.xm.server.db.dao;

import com.shsxt.xm.api.po.BasUserSecurity;
import com.shsxt.xm.server.base.BaseDao;
import org.apache.ibatis.annotations.Param;

public interface BasUserSecurityDao extends BaseDao<BasUserSecurity> {

    public BasUserSecurity queryBasUserSecurityByUserId(@Param("userId") Integer userId);

    public BasUserSecurity queryBasUserSecurityByIdCard(@Param("idCard") String idCard);
}