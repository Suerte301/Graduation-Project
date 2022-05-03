package com.suerte.lostandfound.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: gaoyongfei
 * @Date:  2021/12/3 22:49
 */
@Data
@ApiModel(value = "返回分页基本信息")
public class PageInfo<T> implements Serializable {

    private static final long serialVersionUID = 8316167886091025771L;

    @ApiModelProperty("总记录数")
    private int totalCount;

    @ApiModelProperty("每页记录数")
    private int pageSize;

    @ApiModelProperty("当前页的数量")
    private int size;

    @ApiModelProperty("总页数")
    private int totalPage;

    @ApiModelProperty("当前页数")
    private int currPage;

    @ApiModelProperty("列表数据")
    private List<T> list;

    /**
     * 分页
     * @param list        列表数据
     * @param totalCount  总记录数
     * @param pageSize    每页记录数
     * @param currPage    当前页数
     */
    public PageInfo(List<T> list, int totalCount, int pageSize, int currPage) {
        this.list = list;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        this.currPage = currPage;
        this.totalPage = (int)Math.ceil((double)totalCount/pageSize);
    }

    /**
     * 分页  IPage
     */
    public PageInfo(IPage<T> page) {
        this.list = page.getRecords();
        this.totalCount = (int)page.getTotal();
        this.pageSize = (int)page.getSize();
        this.size = (int)page.getSize();
        this.currPage = (int)page.getCurrent();
        this.totalPage = (int)page.getPages();
    }

    /**
     * 分页  IPage
     */
    public PageInfo(List<T> list, IPage page) {
        this.list = list;
        this.totalCount = (int)page.getTotal();
        this.pageSize = (int)page.getSize();
        this.size = (int)page.getSize();
        this.currPage = (int)page.getCurrent();
        this.totalPage = (int)page.getPages();
    }

    /**
     * 分页 page helper
     */
    public PageInfo(List<T> list) {
        if (list instanceof Page) {
            Page<T> page = (Page<T>) list;
            this.currPage = page.getPageNum();
            this.pageSize = page.getPageSize();
            this.totalPage = page.getPages();
            this.size = page.size();
            this.list =page;
            this.totalCount = (int)page.getTotal();
        }

    }

    /**
     * 为了解决feign调用出现的序列化问题
     */
    public PageInfo() {

    }

}
