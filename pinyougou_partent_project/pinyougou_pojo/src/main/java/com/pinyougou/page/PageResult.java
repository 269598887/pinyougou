package com.pinyougou.page;

import java.io.Serializable;
import java.util.List;

/**
 * 分页查询返回前端的数据
 * @param <T>
 */
public class PageResult<T> implements Serializable {
    private Long total;
    private List<T> rows;

    public PageResult(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> roes) {
        this.rows = roes;
    }
}
