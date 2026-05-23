package com.ai.learning.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    
    private Long total;
    private Long pageNum;
    private Long pageSize;
    private Long pages;
    private List<T> records;

    public PageResult() {
    }

    public PageResult(Long total, Long pageNum, Long pageSize, Long pages, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pages;
        this.records = records;
    }

    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(
            page.getTotal(),
            page.getCurrent(),
            page.getSize(),
            page.getPages(),
            page.getRecords()
        );
    }

    public static <T> PageResult<T> of(Long total, List<T> records) {
        return new PageResult<>(total, 1L, (long) records.size(), 1L, records);
    }
}
