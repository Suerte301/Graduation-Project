package com.suerte.lostandfound.controller;

import cn.hutool.core.util.ObjectUtil;
import com.suerte.lostandfound.config.minio.MinioItem;
import com.suerte.lostandfound.config.minio.MinioProperties;
import com.suerte.lostandfound.config.minio.MinioTemplate;
import com.suerte.lostandfound.constant.AvatarConstant;
import com.suerte.lostandfound.entity.Category;
import com.suerte.lostandfound.entity.Location;
import com.suerte.lostandfound.entity.User;
import com.suerte.lostandfound.service.CategoryService;
import com.suerte.lostandfound.service.LocationService;
import com.suerte.lostandfound.vo.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: Demon
 * @Date: 2022/4/17
 * @Description:
 */
@Controller
@RequestMapping("/admin")
@Slf4j
public class AdminController {


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private MinioTemplate minioTemplate;

    @Autowired
    private MinioProperties minioProperties;


    @GetMapping("management")
    public ModelAndView management(Authentication authentication){
        ModelAndView modelAndView = new ModelAndView("management");
        User user = (User) authentication.getPrincipal();

        try {
            String objectName = user.getAvatar();
            if (ObjectUtil.isEmpty(user.getAvatar())) {
                // 如果没有头像,将会随机从默认头像中选取一个

                List<MinioItem> allObjectsByPrefix = minioTemplate.getAllObjectsByPrefix(minioProperties.getBucketName(), AvatarConstant.DEFAULT_AVATAR_PATH, true);
                int randomAvatar = ThreadLocalRandom.current().nextInt(9) + 1;
                MinioItem minioItem = allObjectsByPrefix.get(randomAvatar);
                objectName =minioTemplate.objectUrl(minioProperties.getBucketName(), minioItem.getObjectName());

//                ThreadLocalRandom.current().nextInt(10)
            }
            user.setAvatar(objectName);

        } catch (Exception e) {
            log.error("访问MinIo失败 报错原因 {} 报错位置 {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }

        modelAndView.addObject("loginUser",user);


        List<Category> categoryList = categoryService.list();
        List<Location> locationList = locationService.list();

        // 位置列表
        modelAndView.addObject("locationList", locationList);
        // 物品统计信息
        modelAndView.addObject("categoryList", categoryList);

//        categoryService.list()
        return modelAndView;
    }

    @GetMapping("goodList")
    public HttpResult goodList(){
        // TODO: 2022/4/16 显示所有物品申请信息
        return HttpResult.ok();
    }

    @GetMapping("goodApplication")
    public HttpResult goodApplication(){
        // TODO: 2022/4/16 物品申请信息审核
        return HttpResult.ok();
    }


    @GetMapping("list")
    public HttpResult list(){
        // TODO: 2022/4/16 显示所有申诉信息
        return HttpResult.ok();
    }

    @DeleteMapping("del")
    public HttpResult del(){
        // TODO: 2022/4/16 删除申诉信息
        return HttpResult.ok();
    }

    @PostMapping("add")
    public HttpResult add(){
        // TODO: 2022/4/16 添加申诉信息
        return HttpResult.ok();
    }

    @PostMapping("update")
    public HttpResult update(){
        // TODO: 2022/4/16 更新申诉信息
        return HttpResult.ok();

    }


}
