package com.travelai.travelai.common.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 统一分页返回封装
 *
 * @param <T> 列表元素类型
 * @author TravelAI Team
 */
@Getter
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页码 */
    private long page;

    /** 每页大小 */
    private long size;

    /** 总记录数 */
    private long total;

    /** 总页数 */
    private long pages;

    /** 数据列表 */
    private List<T> records;

    private PageResult(long page, long size, long total, List<T> records) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.pages = (total + size - 1) / size;
        this.records = records;
    }

    /**
     * 构建分页结果
     *
     * @param page    当前页码
     * @param size    每页大小
     * @param total   总记录数
     * @param records 数据列表
     * @param <T>     数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(long page, long size, long total, List<T> records) {
        return new PageResult<>(page, size, total, records != null ? records : Collections.emptyList());
    }

    /**
     * 空分页结果
     */
    public static <T> PageResult<T> empty(long page, long size) {
        return new PageResult<>(page, size, 0, Collections.emptyList());
    }

    /**
     * 是否有下一页
     */
    public boolean hasNext() {
        return page < pages;
    }

    /**
     * 是否有上一页
     */
    public boolean hasPrevious() {
        return page > 1;
    }
}
