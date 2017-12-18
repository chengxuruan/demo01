package com.shsxt.xm.api.service;

import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.query.BasItemQuery;
import com.shsxt.xm.api.utils.PageList;

/**
 * Created by Administrator on 2017/12/10.
 */
public interface IBasItemService {

    //分页查询页面
    public PageList queryBasItemsByParams(BasItemQuery basItemQuery);

    //修改状态
   public void updateBasItemStatusToOpen(Integer itemId);


    public BasItemDto queryBasItemByItemId(Integer itemId);
}
