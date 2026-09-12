package io.github.codeyunze.web;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

/**
 * 后端返回给前端的列表数据对象。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultTable<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据总数
     */
    private Long total;

    /**
     * 数据列表
     */
    private List<T> items;

    public ResultTable() {
    }

    public ResultTable(List<T> items, Long total) {
        this.total = total;
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
