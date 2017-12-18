package com.shsxt.xm.api.query;

import java.io.Serializable;

/**
 * Created by lp on 2017/12/11.
 */
public class BusItemInvestQuery  extends  BaseQuery implements Serializable{
    private static final long serialVersionUID = -6369076184157842614L;

    private Integer itemId;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
