package com.suerte.lostandfound.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.util.CollectionUtils;

/**
 * 分页数据组装
 * @Author: gaoyongfei
 * @Date:  2021/12/5 18:26
 */
public class QueryPage<T> {
    public IPage<T> getPage(PageReq pageParams) {
        long currPage = 0;
        long pageSize = 10;

        // 组装参数
        if (pageParams != null) {
            if (pageParams.getCurrPage() != null) {
                currPage = pageParams.getCurrPage() ;
            }
            if (pageParams.getPageSize() !=null ){
                pageSize = pageParams.getPageSize() ;
            }
        }

        //分页对象
        Page<T> page = new Page<>(currPage, pageSize);
        if(!CollectionUtils.isEmpty(pageParams.getOrderByList())){
            for(OrderByInfo order : pageParams.getOrderByList()){
                if("asc".equals(order.getOrder())){
                    page.addOrder(OrderItem.asc(order.getField()));
                }
                if("desc".equals(order.getOrder())){
                    page.addOrder(OrderItem.desc(order.getField()));
                }
            }
        }
        return page;
    }

}
