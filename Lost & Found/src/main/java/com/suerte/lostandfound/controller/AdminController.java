package com.suerte.lostandfound.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.suerte.lostandfound.config.minio.MinioItem;
import com.suerte.lostandfound.config.minio.MinioProperties;
import com.suerte.lostandfound.config.minio.MinioTemplate;
import com.suerte.lostandfound.constant.AvatarConstant;
import com.suerte.lostandfound.constant.ImgConstant;
import com.suerte.lostandfound.entity.*;
import com.suerte.lostandfound.eum.*;
import com.suerte.lostandfound.mapper.UserRoleMapper;
import com.suerte.lostandfound.service.*;
import com.suerte.lostandfound.util.PageInfo;
import com.suerte.lostandfound.util.QueryPage;
import com.suerte.lostandfound.vo.HttpResult;
import com.suerte.lostandfound.vo.req.*;
import com.suerte.lostandfound.vo.res.ApplyFormRes;
import com.suerte.lostandfound.vo.res.CategoryRes;
import com.suerte.lostandfound.vo.res.ComplaintRes;
import com.suerte.lostandfound.vo.res.GoodsRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private ApplyFormService applyFormService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ComplaintFormService complaintFormService;
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private AdminService adminService;

    @GetMapping("management")
    public ModelAndView management(Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView("management");
        User user = (User) authentication.getPrincipal();

        try {
            String objectName = user.getAvatar();
            if (ObjectUtil.isEmpty(user.getAvatar())) {
                // ??????????????????,??????????????????????????????????????????

                List<MinioItem> allObjectsByPrefix = minioTemplate.getAllObjectsByPrefix(minioProperties.getBucketName(), AvatarConstant.DEFAULT_AVATAR_PATH, true);
                int randomAvatar = ThreadLocalRandom.current().nextInt(9) + 1;
                MinioItem minioItem = allObjectsByPrefix.get(randomAvatar);
                objectName = minioTemplate.objectUrl(minioProperties.getBucketName(), minioItem.getObjectName());

//                ThreadLocalRandom.current().nextInt(10)
            }
            user.setAvatar(objectName);

        } catch (Exception e) {
            log.error("??????MinIo?????? ???????????? {} ???????????? {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }

        modelAndView.addObject("loginUser", user);


        List<Category> categoryList = categoryService.list();
        List<Location> locationList = locationService.list();

        // ????????????
        modelAndView.addObject("locationList", locationList);
        // ??????????????????
        modelAndView.addObject("categoryList", categoryList);

//        categoryService.list()
        return modelAndView;
    }

    @GetMapping("user")
    public ModelAndView user(UserSearchReq userSearchReq, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        final ModelAndView modelAndView = new ModelAndView("adminUser");
        final Integer type = userSearchReq.getType();
        final String searchKey = userSearchReq.getSearchKey();

        final Set<Integer> adminUser = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRid, RoleEnum.USER.getType())).stream().map(UserRole::getUid).collect(Collectors.toSet());

        final UserSearchEnum userSearchTypeByType = UserSearchEnum.getUserSearchTypeByType(type);
        IPage<User> page = null;
        if (userSearchTypeByType == null) {
            page = userService.page(new QueryPage<User>().getPage(userSearchReq));
        } else {
            switch (userSearchTypeByType) {
                case ACCOUNT:
                    page = userService.page(new QueryPage<User>().getPage(userSearchReq), new LambdaQueryWrapper<User>()
                            .like(ObjectUtil.isNotEmpty(searchKey), User::getAccount, searchKey)
                            .in(User::getId, adminUser));
                    break;
                case SID:
                    page = userService.page(new QueryPage<User>().getPage(userSearchReq), new LambdaQueryWrapper<User>()
                            .like(ObjectUtil.isNotEmpty(searchKey), User::getSid, searchKey)
                            .notIn(User::getId, adminUser));
                    break;
                case NAME:
                    page = userService.page(new QueryPage<User>().getPage(userSearchReq), new LambdaQueryWrapper<User>()
                            .like(ObjectUtil.isNotEmpty(searchKey), User::getName, searchKey)
                            .in(User::getId, adminUser));
                    break;
            }
        }
        PageInfo goodsPageInfo = new PageInfo<>(page);
        List<String> names = new ArrayList<>();
        names.add("??????");
        names.add("??????");
        names.add("??????");
        names.add("??????");
        names.add("??????");
        names.add("qq");
        names.add("??????");
        names.add("??????");

        modelAndView.addObject("resultPageList", goodsPageInfo);
        modelAndView.addObject("names", names);
        modelAndView.addObject("searchKey", searchKey);
        modelAndView.addObject("selectType", userSearchTypeByType == null ? -1 : userSearchTypeByType.getType());
//
        modelAndView.addObject("loginUser", user);
        // TODO: 2022/4/16 ??????????????????????????????
        return modelAndView;
    }

    @GetMapping("applyForm")
    public ModelAndView applyForm(ApplyFormReq applyFormReq, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        final ModelAndView modelAndView = new ModelAndView("adminApplyForm");
        final Integer type = applyFormReq.getStatus();


        final FormStatusEnum userSearchTypeByType = FormStatusEnum.getStatusByType(type);
        final String searchKey = applyFormReq.getTitle();
        List<String> goodsList = goodsService.list(new LambdaQueryWrapper<Goods>().like(ObjectUtil.isNotEmpty(searchKey), Goods::getTitle, searchKey)).stream().map(Goods::getId).collect(Collectors.toList());
        IPage<ApplyForm> page = goodsList.isEmpty() ? new QueryPage<ApplyForm>().getPage(applyFormReq) : applyFormService.page(new QueryPage<ApplyForm>().getPage(applyFormReq), new LambdaQueryWrapper<ApplyForm>()
                .eq(userSearchTypeByType != null, ApplyForm::getStatus, applyFormReq.getStatus())
                .in(ApplyForm::getGoodsId, goodsList));


        final List<ApplyFormRes> collect = page.getRecords().stream().map(i -> {
            final ApplyFormRes applyFormRes = new ApplyFormRes();
            final ApplyForm byId = applyFormService.getById(i.getId());
            applyFormRes.setId(byId.getId());


            applyFormRes.setApplyUser(userService.getById(byId.getUid()));

            final Goods goods = goodsService.getById(byId.getGoodsId());


            final GoodsRes goodsRes = new GoodsRes();
//            goodsRes.setOperation(OperationEnum.getOperationByType(i.getType()));
            OperationEnum iOperationByType = OperationEnum.getOperationByType(goods.getType());
            goodsRes.setOperation(iOperationByType);
            goodsRes.setOperationName(iOperationByType.getName());
            goodsRes.setOperationType(iOperationByType.getType());

            GoodsStatusEnum statusByType = GoodsStatusEnum.getStatusByType(goods.getStatus());
            goodsRes.setStatus(statusByType);
            goodsRes.setStatusName(statusByType.getMsg());
            goodsRes.setStatusType(statusByType.getType());

            goodsRes.setUser(userService.getById(goods.getUid()));
            String[] split = goods.getImgSrc().split(",");
//            if (split.length==1&&ObjectUtil.isEmpty(split[0])){
//                i.setImgSrc(ImgConstant.DEFAULT_IMG_PATH);
//                split[0]=ImgConstant.DEFAULT_IMG_PATH;
//            }
            goods.setImgSrc(split[0]);
            goods.setImgSrcList(Arrays.asList(split));
            goodsRes.setImgSrcList(goods.getImgSrcList());
            goodsRes.setImgSrc(ObjectUtil.isEmpty(goods.getImgSrc()) ? ImgConstant.DEFAULT_ITEM_PATH : goods.getImgSrc());
            goodsRes.setDescription(goods.getDescription());
            goodsRes.setLocation(goods.getLocationId().equals("-1") ? Location.DEFAULT : locationService.getById(goods.getLocationId()));
            goodsRes.setTitle(goods.getTitle());
            goodsRes.setCategory(categoryService.getOne(new LambdaQueryWrapper<Category>().eq(Category::getType, goods.getCategoryType())));
            goodsRes.setId(goods.getId());
            goodsRes.setCreateDate(DateUtil.formatDateTime(goods.getCreateDate()));

            applyFormRes.setStatus(FormStatusEnum.getStatusByType(byId.getStatus()));

            applyFormRes.setGoodsRes(goodsRes);
            String imgSrcStr = ObjectUtil.isNotEmpty(goodsRes.getImgSrc()) ? goodsRes.getImgSrc() : "/icons/emptyImg.png";
            applyFormRes.setPopUpDetail("??????: <span>" + goodsRes.getTitle() + "</span>" + "<br>" +
                    "????????????: <span>" + goodsRes.getLocation().getPosition() + "</span>" + "<br>" +
                    "??????: <span>" + goodsRes.getDescription() + "</span>" + "<br>" +
                    "??????: <img src='" + imgSrcStr + "'/>" + "<br>" +
                    "????????????: <span>" + goodsRes.getUser().getName() + "</span>" + "<br>" +
                    "??????: <span>" + goodsRes.getOperationName() + "</span>" + "<br>" +
                    "????????????: <span>" + goodsRes.getCreateDate() + "</span>" + "<br>" +
                    "?????????: <span>" + applyFormRes.getApplyUser().getName() + "</span>");

            return applyFormRes;
        }).collect(Collectors.toList());

        PageInfo goodsPageInfo = new PageInfo<>(page);

        goodsPageInfo.setList(collect);

        List<String> names = new ArrayList<>();
        names.add("??????");
        names.add("????????????");
        names.add("??????????????????");
        names.add("????????????");
        names.add("????????????");
        names.add("??????");
        names.add("??????");
        names.add("??????");
        names.add("?????????");
        names.add("??????");

        modelAndView.addObject("resultPageList", goodsPageInfo);
        modelAndView.addObject("names", names);
        modelAndView.addObject("selectType", userSearchTypeByType == null ? -1 : userSearchTypeByType.getType());
        modelAndView.addObject("searchKey", searchKey);
//
        final List<Location> list = locationService.list();
        final List<Category> categories = categoryService.list();
        final List<User> userList = userService.list();
        modelAndView.addObject("locationList", list);
        modelAndView.addObject("userList", userList);
        modelAndView.addObject("categories", categories);
        modelAndView.addObject("loginUser", user);
        // TODO: 2022/4/16 ??????????????????????????????
        return modelAndView;
    }

    @GetMapping("adminGoods")
    public ModelAndView adminGoods(GoodsSearchReq goodsSearchReq, Authentication authentication) {


        ModelAndView modelAndView = new ModelAndView("adminGoods");
        User user = (User) authentication.getPrincipal();


        Integer categoryType = goodsSearchReq.getCategoryType();
        Integer operation = goodsSearchReq.getOperation();
        String positionId = goodsSearchReq.getPositionId();
        String searchKey = goodsSearchReq.getSearchKey();
        Integer status = goodsSearchReq.getGoodStatus();

        OperationEnum operationByType = OperationEnum.getOperationByType(operation);

        Category defaultCategory = categoryType == -1 ? Category.DEFAULT : categoryService.getOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getType, categoryType), false);

        Location defaultLocation = positionId.equals("-1") ? Location.DEFAULT : locationService.getOne(new LambdaQueryWrapper<Location>()
                .eq(Location::getId, positionId), false);


        IPage<Goods> page = goodsService.page(new QueryPage<Goods>().getPage(goodsSearchReq),
                new LambdaQueryWrapper<Goods>()
                        .eq(!positionId.equals("-1"), Goods::getLocationId, positionId)
                        .eq(operation != -1, Goods::getType, operation)
                        .eq(categoryType != -1, Goods::getCategoryType, categoryType)
                        .eq(status != -2, Goods::getStatus, status)
                        .like(ObjectUtil.isNotEmpty(searchKey), Goods::getTitle, searchKey));

//        PageInfo<Goods> goodsPageInfo = new PageInfo<>(page);
        PageInfo goodsPageInfo = new PageInfo<>(page);

//        final List<GoodsRes> collect = goodsService.list().stream().map(i -> {
        List<GoodsRes> collect = page.getRecords().stream().map(i -> {

            GoodsRes goodsRes = new GoodsRes();

//            goodsRes.setOperation(OperationEnum.getOperationByType(i.getType()));
            OperationEnum iOperationByType = OperationEnum.getOperationByType(i.getType());
            goodsRes.setOperation(iOperationByType);
            goodsRes.setOperationName(iOperationByType.getName());
            goodsRes.setOperationType(iOperationByType.getType());

            GoodsStatusEnum statusByType = GoodsStatusEnum.getStatusByType(i.getStatus());
            goodsRes.setStatus(statusByType);
            goodsRes.setStatusName(statusByType.getMsg());
            goodsRes.setStatusType(statusByType.getType());

            goodsRes.setUser(userService.getById(i.getUid()));
            String[] split = i.getImgSrc().split(",");
//            if (split.length==1&&ObjectUtil.isEmpty(split[0])){
//                i.setImgSrc(ImgConstant.DEFAULT_IMG_PATH);
//                split[0]=ImgConstant.DEFAULT_IMG_PATH;
//            }
            i.setImgSrc(split[0]);
            i.setImgSrcList(Arrays.asList(split));

            goodsRes.setDescription(i.getDescription());
            goodsRes.setLocation(i.getLocationId().equals("-1") ? Location.DEFAULT : locationService.getById(i.getLocationId()));
            goodsRes.setTitle(i.getTitle());
            goodsRes.setCategory(categoryService.getOne(new LambdaQueryWrapper<Category>().eq(Category::getType, i.getCategoryType())));
            goodsRes.setId(i.getId());
            goodsRes.setCreateDate(DateUtil.formatDateTime(i.getCreateDate()));
            return goodsRes;
        }).collect(Collectors.toList());

        goodsPageInfo.setList(collect);

        List<String> names = new ArrayList<>();
        names.add("??????");
        names.add("????????????");
        names.add("????????????");
        names.add("??????????????????");
        names.add("????????????");
        names.add("????????????");
        names.add("????????????");
        names.add("??????");
        names.add("??????");
        names.add("??????");

        modelAndView.addObject("resultPageList", goodsPageInfo);
        modelAndView.addObject("names", names);
        List<CategoryRes> goodsInfo = categoryService.getGoodsInfo(GoodsStatusEnum.REVIEW_PASSED.getType(), user.getId());
        List<Location> locationList = locationService.list();


        // ????????????
        modelAndView.addObject("locationList", locationList);
        // ??????????????????
        modelAndView.addObject("goodsInfos", goodsInfo);

        // ????????????
        modelAndView.addObject("locationList", locationList);
        // ??????????????????
//        modelAndView.addObject("goodsInfos", goodsInfo);

        // ?????????Category
        modelAndView.addObject("selectedCategory", defaultCategory);
        // ?????????Position
        modelAndView.addObject("selectedPosition", defaultLocation);
        // ?????????Operation
        modelAndView.addObject("selectedOperation", operationByType.getType());
        modelAndView.addObject("selectGoodStatus", status);
        // ?????????keyword
        modelAndView.addObject("searchKey", searchKey);

        final List<User> userList = userService.list();

        modelAndView.addObject("userList", userList);

        modelAndView.addObject("loginUser", user);


        return modelAndView;
    }

    @PostMapping("resetUserPass")
    @ResponseBody
    public HttpResult resetUserPass(@RequestParam("id") Integer id) {
        final User byId = userService.getById(id);
        final String encode = bCryptPasswordEncoder.encode(byId.getDefaultpass());

        byId.setPassword(encode);
        byId.setUncrypted(byId.getDefaultpass());
        boolean b = true;
        try {
            b = userService.saveOrUpdate(byId);
        } catch (Exception e) {
            log.error("?????????????????? ????????????{} ????????????{}", e.getMessage(), Arrays.toString(e.getStackTrace()));

            b = false;
        }
        return b ? HttpResult.ok("????????????") : HttpResult.error("????????????");
    }

    @DeleteMapping("delUser/{id}")
    @ResponseBody
    public HttpResult delUser(@PathVariable("id") Integer id) {
        boolean b = true;
        try {
            adminService.delUser(id);
        } catch (Exception e) {
            log.error("?????????????????? ????????????{} ????????????{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            b = false;
        }
        return b ? HttpResult.ok("????????????") : HttpResult.error("????????????");
    }


    @DeleteMapping("delGoods/{id}")
    @ResponseBody
    public HttpResult delGoods(@PathVariable("id") String id) {
        boolean b = true;
        try {
            adminService.delGoods(id);
        } catch (Exception e) {
            log.error("?????????????????? ????????????{} ????????????{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            b = false;
        }
        return b ? HttpResult.ok("????????????") : HttpResult.error("????????????");
    }


    @DeleteMapping("delComplaint/{id}")
    @ResponseBody
    public HttpResult delComplaint(@PathVariable("id") String id) {
        boolean b = true;
        try {
            adminService.delComplaint(id);
        } catch (Exception e) {
            log.error("?????????????????? ????????????{} ????????????{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            b = false;
        }
        return b ? HttpResult.ok("????????????") : HttpResult.error("????????????");
    }


    @DeleteMapping("delForm/{id}")
    @ResponseBody
    public HttpResult delForm(@PathVariable("id") String id) {
        boolean b = true;
        try {
            b=applyFormService.removeById(id);
        } catch (Exception e) {
            log.error("?????????????????? ????????????{} ????????????{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            b = false;
        }
        return b ? HttpResult.ok("????????????") : HttpResult.error("????????????");
    }

    @PostMapping("updateForm")
    public String updateForm(ApplyFormUpdateReq applyFormUpdateReq) {

        System.out.println(applyFormUpdateReq);

        try {
            adminService.updateForm(applyFormUpdateReq);

        } catch (Exception e) {
            log.error("???????????? ????????????{} ????????????{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
        return "redirect:/admin/applyForm";
    }

    @PostMapping("updateComplaintForm")
    public String updateComplaintForm(ComplaintUpdateReq complaintUpdateReq) {

        System.out.println(complaintUpdateReq);

        try {
            adminService.updateComplaintForm(complaintUpdateReq);
        } catch (Exception e) {
            log.error("???????????? ????????????{} ????????????{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
        return "redirect:/admin/complaintForm";
    }


    @PostMapping("updateUser")
    public String updateUser(UserUpdateReq userUpdateReq) {
        System.out.println(userUpdateReq);
        final User byId = userService.getById(userUpdateReq.getChangeId());
        byId.setEmail(userUpdateReq.getChangeEmail());
        byId.setQq(userUpdateReq.getChangeQq());
        byId.setTel(userUpdateReq.getChangeTel());

        try {
            userService.saveOrUpdate(byId);
        } catch (Exception e) {
            log.error("???????????? ????????????{} ????????????{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }

        return "redirect:/admin/user";

    }

    @GetMapping("complaintForm")
    public ModelAndView complaintForm(ComplaintFormReq complaintFormReq, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        final ModelAndView modelAndView = new ModelAndView("adminComplaintForm");
        final Integer type = complaintFormReq.getStatus();


        final FormStatusEnum userSearchTypeByType = FormStatusEnum.getStatusByType(type);
        final String searchKey = complaintFormReq.getName();
        // ??????
        List<Integer> userList = userService.list(new LambdaQueryWrapper<User>().like(ObjectUtil.isNotEmpty(searchKey), User::getName, searchKey)).stream().map(User::getId).collect(Collectors.toList());


        IPage<ComplaintForm> page = userList.isEmpty() ? new QueryPage<ComplaintForm>().getPage(complaintFormReq) :
                complaintFormService.page(new QueryPage<ComplaintForm>().getPage(complaintFormReq), new LambdaQueryWrapper<ComplaintForm>()
                        .eq(userSearchTypeByType != null, ComplaintForm::getStatus, complaintFormReq.getStatus())
                        .in(ComplaintForm::getUid, userList));
//                .eq(ComplaintForm::getUid, principal.getId()));
//        PageInfo<ApplyForm> applyPageInfo = new PageInfo<>(page);
        PageInfo applyPageInfo = new PageInfo<>(page);

        List<ComplaintRes> list = page.getRecords().stream().map(i -> {
            ComplaintRes complaintRes = new ComplaintRes();

            complaintRes.setUser(userService.getById(i.getUid()));
            final ApplyFormRes applyFormRes = new ApplyFormRes();
            final ApplyForm byId = applyFormService.getById(i.getApplyId());
            applyFormRes.setId(byId.getId());

            applyFormRes.setApplyUser(userService.getById(byId.getUid()));

            final Goods goods = goodsService.getById(byId.getGoodsId());


            final GoodsRes goodsRes = new GoodsRes();
//            goodsRes.setOperation(OperationEnum.getOperationByType(i.getType()));
            OperationEnum iOperationByType = OperationEnum.getOperationByType(goods.getType());
            goodsRes.setOperation(iOperationByType);
            goodsRes.setOperationName(iOperationByType.getName());
            goodsRes.setOperationType(iOperationByType.getType());

            GoodsStatusEnum statusByType = GoodsStatusEnum.getStatusByType(goods.getStatus());
            goodsRes.setStatus(statusByType);
            goodsRes.setStatusName(statusByType.getMsg());
            goodsRes.setStatusType(statusByType.getType());

            goodsRes.setUser(userService.getById(goods.getUid()));
            goodsRes.setImgSrcList(goods.getImgSrcList());
            goodsRes.setImgSrc(goods.getImgSrc());
            goodsRes.setDescription(goods.getDescription());
            goodsRes.setLocation(goods.getLocationId().equals("-1") ? Location.DEFAULT : locationService.getById(goods.getLocationId()));
            goodsRes.setTitle(goods.getTitle());
            goodsRes.setCategory(categoryService.getOne(new LambdaQueryWrapper<Category>().eq(Category::getType, goods.getCategoryType())));
            goodsRes.setId(goods.getId());
            goodsRes.setCreateDate(DateUtil.formatDateTime(goods.getCreateDate()));


            applyFormRes.setStatus(FormStatusEnum.getStatusByType(byId.getStatus()));

            applyFormRes.setGoodsRes(goodsRes);
            String imgSrcStr = ObjectUtil.isNotEmpty(goodsRes.getImgSrc()) ? goodsRes.getImgSrc() : "/icons/emptyImg.png";
            applyFormRes.setPopUpDetail("??????: <span>" + goodsRes.getTitle() + "</span>" + "<br>" +
                    "????????????: <span>" + goodsRes.getLocation().getPosition() + "</span>" + "<br>" +
                    "??????: <span>" + goodsRes.getDescription() + "</span>" + "<br>" +
                    "??????: <img src='" + imgSrcStr + "'/>" + "<br>" +
                    "????????????: <span>" + goodsRes.getUser().getName() + "</span>" + "<br>" +
                    "??????: <span>" + goodsRes.getOperationName() + "</span>" + "<br>" +
                    "????????????: <span>" + goodsRes.getCreateDate() + "</span>" + "<br>" +
                    "?????????: <span>" + applyFormRes.getApplyUser().getName() + "</span>");


            complaintRes.setApplyForm(applyFormRes);
            complaintRes.setStatus(i.getStatus());
            complaintRes.setStatusEnum(ComplaintStatusEnum.getStatusByType(i.getStatus()));
            complaintRes.setId(i.getId());

            return complaintRes;
        }).collect(Collectors.toList());

        applyPageInfo.setList(list);

        List<String> names = new ArrayList<>();
        names.add("??????");
        names.add("????????????");
        names.add("??????????????????");
        names.add("????????????");
        names.add("????????????");
        names.add("??????");
        names.add("??????");
        names.add("??????");
        names.add("?????????");
        names.add("?????????");
        names.add("??????");

        modelAndView.addObject("resultPageList", applyPageInfo);
        modelAndView.addObject("names", names);
        modelAndView.addObject("selectType", userSearchTypeByType == null ? -1 : userSearchTypeByType.getType());
        modelAndView.addObject("searchKey", searchKey);

        List<Category> categoryList = categoryService.list();
        List<Location> locationList = locationService.list();

        // ????????????
        modelAndView.addObject("locationList", locationList);
        // ??????????????????
        modelAndView.addObject("categories", categoryList);

        final List<User> userAllList = userService.list();

        modelAndView.addObject("userList", userAllList);


//
        modelAndView.addObject("loginUser", user);
        // TODO: 2022/4/16 ??????????????????????????????
        return modelAndView;
    }


    @GetMapping("goodList")
    public HttpResult goodList() {
        // TODO: 2022/4/16 ??????????????????????????????
        return HttpResult.ok();
    }

    @GetMapping("goodApplication")
    public HttpResult goodApplication() {
        // TODO: 2022/4/16 ????????????????????????
        return HttpResult.ok();
    }


    @GetMapping("list")
    public HttpResult list() {
        // TODO: 2022/4/16 ????????????????????????
        return HttpResult.ok();
    }

    @DeleteMapping("del")
    public HttpResult del() {
        // TODO: 2022/4/16 ??????????????????
        return HttpResult.ok();
    }

    @PostMapping("add")
    public HttpResult add() {
        // TODO: 2022/4/16 ??????????????????
        return HttpResult.ok();
    }

    @PostMapping("update")
    public HttpResult update() {
        // TODO: 2022/4/16 ??????????????????
        return HttpResult.ok();

    }


}
