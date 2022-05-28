package com.suerte.lostandfound.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.suerte.lostandfound.config.minio.MinioProperties;
import com.suerte.lostandfound.config.minio.MinioTemplate;
import com.suerte.lostandfound.entity.*;
import com.suerte.lostandfound.eum.FormStatusEnum;
import com.suerte.lostandfound.eum.GoodsStatusEnum;
import com.suerte.lostandfound.eum.OperationEnum;
import com.suerte.lostandfound.service.*;
import com.suerte.lostandfound.util.PageInfo;
import com.suerte.lostandfound.util.QueryPage;
import com.suerte.lostandfound.vo.HttpResult;
import com.suerte.lostandfound.vo.req.AddGoodsReq;
import com.suerte.lostandfound.vo.req.GoodsSearchReq;
import com.suerte.lostandfound.vo.req.GoodsStatusReq;
import com.suerte.lostandfound.vo.res.ApplyFormRes;
import com.suerte.lostandfound.vo.res.CategoryRes;
import com.suerte.lostandfound.vo.res.GoodsRes;
import com.suerte.lostandfound.vo.res.UserGoodsRes;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.constraints.NotNull;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Demon
 * @Date: 2022/4/16
 * @Description:
 */
@Api("物品")
@RequestMapping("goods")
@Controller
@Slf4j
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("list")
    @ResponseBody
    public HttpResult list(){
        // TODO: 2022/4/16 显示所有物品
        List<Goods> list = goodsService.list();
        return HttpResult.ok(list);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/getByStatus")
    @ResponseBody
    public HttpResult getByStatus(Authentication authentication, GoodsStatusReq goodsStatusReq){
        if (ObjectUtil.isNull(goodsStatusReq.getStatus())||ObjectUtil.isNull(GoodsStatusEnum.getStatusByType(goodsStatusReq.getStatus()))){
            return HttpResult.error("传入的类型错误");
        }
         User principal = (User) authentication.getPrincipal();
        IPage<Goods> page = goodsService.page(new QueryPage<Goods>().getPage(goodsStatusReq), new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, goodsStatusReq.getStatus())
                .like(ObjectUtil.isNotEmpty(goodsStatusReq.getTitle()),Goods::getTitle ,goodsStatusReq.getTitle())
                .eq(Goods::getUid, principal.getId()));
        PageInfo goodsPageInfo = new PageInfo<>(page);

        List<GoodsRes> list = page.getRecords().stream().map(i -> {

            String[] split = i.getImgSrc().split(",");
//            if (split.length==1&&ObjectUtil.isEmpty(split[0])){
//                i.setImgSrc(ImgConstant.DEFAULT_IMG_PATH);
//                split[0]=ImgConstant.DEFAULT_IMG_PATH;
//            }
            i.setImgSrc(split[0]);
            i.setImgSrcList(Arrays.asList(split));

            GoodsRes goodsRes = new GoodsRes();

            OperationEnum operationByType = OperationEnum.getOperationByType(i.getType());
            goodsRes.setOperation(operationByType);
            goodsRes.setOperationName(operationByType.getName());
            goodsRes.setOperationType(operationByType.getType());

            GoodsStatusEnum statusByType = GoodsStatusEnum.getStatusByType(i.getStatus());
            goodsRes.setStatus(statusByType);
            goodsRes.setStatusName(statusByType.getMsg());
            goodsRes.setStatusType(statusByType.getType());

            goodsRes.setUser(userService.getById(i.getUid()));
            goodsRes.setImgSrcList(i.getImgSrcList());
            goodsRes.setImgSrc(i.getImgSrc());
            goodsRes.setDescription(i.getDescription());
            goodsRes.setLocation(locationService.getById(i.getLocationId()));
            goodsRes.setTitle(i.getTitle());
            goodsRes.setCategory(categoryService.getOne(new LambdaQueryWrapper<Category>().eq(Category::getType,i.getCategoryType())));
            goodsRes.setId(i.getId());
            goodsRes.setCreateDate(DateUtil.formatDateTime(i.getCreateDate()));

//            goodsRes.setStatus(GoodsStatusEnum.getStatusByType(i.getStatus()));
            return goodsRes;
        }).collect(Collectors.toList());

        goodsPageInfo.setList(list);

        HashMap<String, Object> map = new HashMap<>();
        map.put("result",goodsPageInfo);
        map.put("searchKey", Optional.ofNullable(goodsStatusReq.getTitle()).orElse(""));

        return HttpResult.ok(map);
    }


    @GetMapping("getGoodsByUid/{id}")
    @ResponseBody
    public HttpResult getGoodsByUid(@PathVariable("id")Integer id){
        List<UserGoodsRes> goodsByUid = goodsService.getGoodsByUid(id);

        Map<String,Integer> map = new HashMap<>();
        List<Map<String,Integer>> collect = goodsByUid.stream().map(i -> {
            map.put("审核成功", i==null?0:i.getReviewedPass());
            map.put("审核中", i==null?0:i.getReviewing());
            map.put("审核失败", i==null?0:i.getReviewedFail());
//            if (i!=null){
//                map.put("审核成功", i.getReviewedPass());
//                map.put("审核中", i.getReviewing());
//                map.put("审核失败", i.getReviewedFail());
//            }
            return map;
        }).collect(Collectors.toList());
        return HttpResult.ok(collect);
    }


    @DeleteMapping("/del/{id}")
    @ResponseBody
    public HttpResult del(@PathVariable("id")String id){
        boolean flag=true;
        try {
             flag = goodsService.removeById(id);
        }catch (Exception e){
            log.error("撤销物品失败 报错原因 {} 报错位置 {}",e.getMessage(),Arrays.toString(e.getStackTrace()));
            flag=false;
        }
        return flag?HttpResult.ok("撤销物品成功"):HttpResult.error("撤销物品失败");
    }



    @Autowired
    private ApplyFormService applyFormService;

    @PostMapping("/toApplyFailed/{id}")
    @ResponseBody
    public HttpResult toApplyFailed(@PathVariable("id")String id){
        boolean flag=true;
        try {
            final Goods byId = goodsService.getById(id);
            byId.setStatus(GoodsStatusEnum.REVIEW_PASSED.getType());
            flag = goodsService.saveOrUpdate(byId);
            // 一个表单只能同时被一个人申请
            ApplyForm one = applyFormService.getOne(new LambdaQueryWrapper<ApplyForm>().eq(ApplyForm::getGoodsId, id).eq(ApplyForm::getStatus, FormStatusEnum.IN_APPLY.getType()));
            one.setStatus(FormStatusEnum.APPLY_FAILED.getType());
            applyFormService.saveOrUpdate(one);
        }catch (Exception e){
            log.error("撤销物品失败 报错原因 {} 报错位置 {}",e.getMessage(),Arrays.toString(e.getStackTrace()));
            flag=false;
        }
        return flag?HttpResult.ok("成功"):HttpResult.error("失败");
    }
    @PostMapping("/toApplySuccess/{id}")
    @ResponseBody
    public HttpResult toApplySuccess(@PathVariable("id")String id){
        boolean flag=true;
        try {
            final Goods byId = goodsService.getById(id);
            byId.setStatus(GoodsStatusEnum.END.getType());
            flag = goodsService.saveOrUpdate(byId);
            // 一个表单只能同时被一个人申请
            ApplyForm one = applyFormService.getOne(new LambdaQueryWrapper<ApplyForm>().eq(ApplyForm::getGoodsId, id).eq(ApplyForm::getStatus, FormStatusEnum.IN_APPLY.getType()));
            one.setStatus(FormStatusEnum.APPLY_SUCCESS.getType());
            applyFormService.saveOrUpdate(one);
        }catch (Exception e){
            log.error("撤销物品失败 报错原因 {} 报错位置 {}",e.getMessage(),Arrays.toString(e.getStackTrace()));
            flag=false;
        }
        return flag?HttpResult.ok("成功"):HttpResult.error("失败");
    }

    @Autowired
    private MinioTemplate minioTemplate;

    @Autowired
    private MinioProperties minioProperties;

    @PostMapping("add")
    @ResponseBody
    public HttpResult add(AddGoodsReq req,MultipartFile[] files){

        if (ObjectUtil.hasEmpty(req.getTitle(),req.getLocation(),req.getCategory(),req.getOperation())){
            return HttpResult.error("必填参数部分为空 请重新输入");
        }

        System.out.println(req);
        boolean flag=true;
        try {
            Goods goods = new Goods();
            goods.setId(IdUtil.simpleUUID());
            goods.setTitle(req.getTitle());
            goods.setLocationId(req.getLocation());
            goods.setDescription(Optional.of(req.getDescription()).orElse(""));
            goods.setImgSrc("");
            if (ObjectUtil.isNotEmpty(files)){
            StringBuilder imgSrc= new StringBuilder("");
                for (MultipartFile file : files) {
                    try ( InputStream inputStream = file.getInputStream()){
                        String imgName = IdUtil.simpleUUID();
                        String suffix = FileUtil.getSuffix(file.getOriginalFilename());
                        String imgFullName = imgName + "." + suffix;
                        minioTemplate.putFile(minioProperties.getBucketName(), imgFullName,inputStream,file.getContentType());
                        imgSrc.append(minioTemplate.objectUrl(minioProperties.getBucketName(), imgFullName)).append(",");
                    }catch (Exception e){
                        log.error("访问minIo失败 报错信息 {} 报错位置 {}",e.getMessage(),Arrays.toString(e.getStackTrace()));
                    }
                }
                goods.setImgSrc(imgSrc.toString().equals("")?"":imgSrc.substring(0,imgSrc.length()-1));
            }

//        goods.setImgSrcList(IdUtil.simpleUUID());
            goods.setUid(req.getUid());
            goods.setCreateDate(new Date());
            goods.setStatus(GoodsStatusEnum.REVIEWING.getType());
            goods.setType(req.getOperation());
            goods.setCategoryType(req.getCategory());
            goodsService.save(goods);
        }catch (Exception e){
            log.error("添加物品失败 报错信息 {} 报错位置 {}",e.getMessage(),Arrays.toString(e.getStackTrace()));
            flag=false;
        }



        // TODO: 2022/4/16 添加物品
        return flag?HttpResult.ok():HttpResult.error("添加物品失败 请稍后再试");

    }
    @PostMapping("update")
    @ResponseBody
    public HttpResult update(){
        // TODO: 2022/4/16 更新物品状态
        return HttpResult.ok();

    }


}
