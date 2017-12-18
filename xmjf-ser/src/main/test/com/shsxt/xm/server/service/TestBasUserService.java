package com.shsxt.xm.server.service;

import com.shsxt.xm.api.service.IBasUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by Administrator on 2017/12/7.
 */

@ContextConfiguration(locations = {"classpath:spring.xml"})
public class TestBasUserService {

    @Autowired
    private IBasUserService basUserService;

    public void test(){

    }
}
