package com.xfy.nabu.common.result;

import java.io.Serializable;

/**
 * 通用分页查询参数。
 */
public class PageQuery implements Serializable {

    private long pageNum = 1;
    private long pageSize = 20;

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum <= 0 ? 1 : pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = (pageSize <= 0 || pageSize > 100) ? 20 : pageSize;
    }

    public long getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
