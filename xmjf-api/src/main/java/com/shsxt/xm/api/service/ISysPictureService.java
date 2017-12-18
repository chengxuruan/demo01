package com.shsxt.xm.api.service;

import com.shsxt.xm.api.po.SysPicture;

import java.util.List;

/**
 * Created by Administrator on 2017/12/11.
 */
public interface ISysPictureService {
    List<SysPicture> querySysPicturesByItemId(Integer itemId);
}
