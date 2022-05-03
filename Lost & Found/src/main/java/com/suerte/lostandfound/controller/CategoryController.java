package com.suerte.lostandfound.controller;

import cn.hutool.core.util.ObjectUtil;
import com.suerte.lostandfound.entity.Category;
import com.suerte.lostandfound.eum.CategoryEnum;
import com.suerte.lostandfound.service.CategoryService;
import com.suerte.lostandfound.vo.HttpResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author: Demon
 * @Date: 2022/4/20
 * @Description:
 */
@Controller
@RequestMapping("category")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;


    @DeleteMapping("delete")
    @ResponseBody
    public HttpResult delete(ModelAndView modelAndView,String id){
        Category byId = categoryService.getById(id);
        if (ObjectUtil.isNotNull(byId)&&byId.getType().equals(CategoryEnum.DEFAULT.getType())){
            return HttpResult.error("删除失败,无法删除 '其他' 类型");

        }

        if (categoryService.list().size()==1){
            return HttpResult.error("删除失败,至少保证有一个类型");
        }
        return HttpResult.ok();
    }

}
