package com.shsxt.xm.server.service;

import com.github.pagehelper.PageHelper;
import com.shsxt.xm.api.po.BusAccountRecharge;
import com.shsxt.xm.api.query.AccountRechargeQuery;
import com.shsxt.xm.api.service.IBusAccountRechargeService;
import com.shsxt.xm.api.utils.PageList;
import com.shsxt.xm.server.db.dao.BusAccountRechargeDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Administrator on 2017/12/14.
 */
@Service
public class BusAccountRechargeServiceImpl implements IBusAccountRechargeService {


    @Resource
    private BusAccountRechargeDao busAccountRechargeDao;

    /**
     * 查询订单分页记录
     * @param accountRechargeQuery
     * @return
     */
    @Override
    public PageList queryRechargeRecodesByParams(AccountRechargeQuery accountRechargeQuery) {

        PageHelper.startPage(accountRechargeQuery.getPageNum(),accountRechargeQuery.getPageSize());

        List<BusAccountRecharge> busAccountRecharges = busAccountRechargeDao.queryForPage(accountRechargeQuery);

        PageList pageList = new PageList(busAccountRecharges);

        return pageList;
    }
}
