package com.shsxt.xm.api.query;

import java.io.Serializable;

/**
 * 当前页码与每页的数量
 */
public class BaseQuery implements Serializable {
    private static final long serialVersionUID = 3209946156249306220L;

    private Integer pageNum = 1;
    private Integer pageSize = 10;


    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }
}
