package com.shsxt.xm.server.service;

import com.github.pagehelper.PageHelper;
import com.shsxt.xm.api.constant.P2PConstant;
import com.shsxt.xm.api.dto.BasItemDto;
import com.shsxt.xm.api.query.BasItemQuery;
import com.shsxt.xm.api.service.IBasItemService;
import com.shsxt.xm.api.utils.AssertUtil;
import com.shsxt.xm.api.utils.PageList;
import com.shsxt.xm.server.db.dao.BasItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/12/10.
 */
@Service
public class BasItemServiceImpl implements IBasItemService {


    @Resource
    private BasItemDao basItemDao;

    @Override
    public PageList queryBasItemsByParams(BasItemQuery basItemQuery) {

        //调用分页对象
        PageHelper.startPage(basItemQuery.getPageNum(),basItemQuery.getPageSize());

        List<BasItemDto> list = basItemDao.queryForPage(basItemQuery);

        if (!CollectionUtils.isEmpty(list)){

            for (BasItemDto basItemDto :list){
                // 如果记录处于待开放状态  计算记录剩余时间 秒数
                if (basItemDto.getItemStatus().equals(1)){
                    Date relaseTime = basItemDto.getReleaseTime();
                    Long syTime = (relaseTime.getTime() - new Date().getTime())/1000;
                    //设置剩余时间
                    basItemDto.setSyTime(syTime);
                }
            }
        }
        PageList pageList = new PageList(list);

        return pageList;
    }

    @Override
    //修改状态 开放/已结束
    public void updateBasItemStatusToOpen(Integer itemId) {

        AssertUtil.isTrue( null ==basItemDao.queryById(itemId),"待更新记录不存在!" );
        AssertUtil.isTrue(basItemDao.updateBasItemStatusToOpen(itemId)<1, P2PConstant.OPS_FAILED_MSG);
    }

    @Override
    public BasItemDto queryBasItemByItemId(Integer itemId) {
        return basItemDao.queryById(itemId);
    }
}
