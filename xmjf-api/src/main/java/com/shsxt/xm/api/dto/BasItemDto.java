package com.shsxt.xm.api.dto;

import com.shsxt.xm.api.po.BasItem;

import java.io.Serializable;

/**
 *
 */
public class BasItemDto extends BasItem implements Serializable {
    private static final long serialVersionUID = -1338472780985117857L;

    private Integer total ;

    public Long getSyTime() {
        return syTime;
    }

    public void setSyTime(Long syTime) {
        this.syTime = syTime;
    }

    //剩余时间
    private Long syTime;
    @Override
    public String toString() {
        return "BasItemDto{" +
                "total=" + total +
                '}';
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
