package com.suerte.lostandfound.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suerte.lostandfound.config.minio.MinioItem;
import com.suerte.lostandfound.config.minio.MinioProperties;
import com.suerte.lostandfound.config.minio.MinioTemplate;
import com.suerte.lostandfound.constant.AvatarConstant;
import com.suerte.lostandfound.constant.RedisConstant;
import com.suerte.lostandfound.entity.*;
import com.suerte.lostandfound.eum.ComplaintStatusEnum;
import com.suerte.lostandfound.eum.FormStatusEnum;
import com.suerte.lostandfound.eum.GoodsStatusEnum;
import com.suerte.lostandfound.eum.OperationEnum;
import com.suerte.lostandfound.service.*;
import com.suerte.lostandfound.util.CheckUtils;
import com.suerte.lostandfound.util.PageInfo;
import com.suerte.lostandfound.util.PassUtils;
import com.suerte.lostandfound.util.QueryPage;
import com.suerte.lostandfound.vo.HttpResult;
import com.suerte.lostandfound.vo.req.GoodsSearchReq;
import com.suerte.lostandfound.vo.req.ProfileReq;
import com.suerte.lostandfound.vo.res.ApplyFormRes;
import com.suerte.lostandfound.vo.res.CategoryRes;
import com.suerte.lostandfound.vo.res.GoodsRes;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @Author: Demon
 * @Date: 2022/2/4
 * @Description:
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private LocationService locationService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserService userService;
    @Autowired
    private MinioTemplate minioTemplate;
    @Autowired
    private MinioProperties minioProperties;
    @Autowired
    private ApplyFormService applyFormService;

    @GetMapping({"index", "/"})
    public ModelAndView index(HttpServletRequest request, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView("index");
        int i = 0;
        Iterator<String> iterator = redissonClient.getKeys().getKeysByPattern("*" + RedisConstant.USER_LOGIN_PREFIX + "*").iterator();
        while (iterator.hasNext()) {
            iterator.next();
            i++;
        }

        User user = (User) authentication.getPrincipal();
        List<CategoryRes> goodsInfo = categoryService.getGoodsInfo(GoodsStatusEnum.REVIEW_PASSED.getType(), user.getId());
        int totalGoods = goodsInfo.stream().mapToInt(CategoryRes::getNum).sum();

        List<User> users = userService.list();
        // ????????????????????????
        modelAndView.addObject("goodsInfos", goodsInfo);
        // ????????????????????????
        modelAndView.addObject("online", i);
        // ????????????????????????
        modelAndView.addObject("totalGoods", totalGoods);
        // ????????????????????????
        modelAndView.addObject("totalUsers", users.size());


        return modelAndView;
    }

    @GetMapping("category")
    public ModelAndView category(GoodsSearchReq goodsSearchReq, Authentication authentication) {


        ModelAndView modelAndView = new ModelAndView("category");

        Integer categoryType = goodsSearchReq.getCategoryType();
        Integer operation = goodsSearchReq.getOperation();
        String positionId = goodsSearchReq.getPositionId();
        String searchKey = goodsSearchReq.getSearchKey();

        OperationEnum operationByType = OperationEnum.getOperationByType(operation);

        Category defaultCategory = categoryType == -1 ? Category.DEFAULT : categoryService.getOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getType, categoryType), false);

        Location defaultLocation = positionId.equals("-1") ? Location.DEFAULT : locationService.getOne(new LambdaQueryWrapper<Location>()
                .eq(Location::getId, positionId), false);

        //
        // ??????????????????????????????
        User user = (User) authentication.getPrincipal();
        List<ApplyForm> userApply = applyFormService.list(new LambdaQueryWrapper<ApplyForm>().eq(ApplyForm::getUid, user.getId()));
        IPage<Goods> page = goodsService.page(new QueryPage<Goods>().getPage(goodsSearchReq),
                new LambdaQueryWrapper<Goods>()
                        .eq(!positionId.equals("-1"), Goods::getLocationId, positionId)
                        .eq(operation != -1, Goods::getType, operation)
                        .eq(categoryType != -1, Goods::getCategoryType, categoryType)
                        .eq(Goods::getStatus, GoodsStatusEnum.REVIEW_PASSED.getType())
                        .like(ObjectUtil.isNotEmpty(searchKey), Goods::getTitle, searchKey)
                        .notIn(!userApply.isEmpty(), Goods::getId, userApply.stream().map(ApplyForm::getGoodsId).collect(Collectors.toList())));

//        PageInfo<Goods> goodsPageInfo = new PageInfo<>(page);
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
            goodsRes.setImgSrcList(i.getImgSrcList());
            goodsRes.setImgSrc(i.getImgSrc());
            goodsRes.setDescription(i.getDescription());
            goodsRes.setLocation(i.getLocationId().equals("-1") ? Location.DEFAULT : locationService.getById(i.getLocationId()));
            goodsRes.setTitle(i.getTitle());
            goodsRes.setCategory(categoryService.getOne(new LambdaQueryWrapper<Category>().eq(Category::getType, i.getCategoryType())));
            goodsRes.setId(i.getId());
            goodsRes.setCreateDate(DateUtil.formatDateTime(i.getCreateDate()));
//            goodsRes.setStatus(GoodsStatusEnum.getStatusByType(i.getStatus()));
            return goodsRes;
        }).collect(Collectors.toList());

        goodsPageInfo.setList(list);
//        List<Goods> list = goodsPageInfo.getList();
//        list.forEach(i -> {
//            String[] split = i.getImgSrc().split(",");
////            if (split.length==1&&ObjectUtil.isEmpty(split[0])){
////                i.setImgSrc(ImgConstant.DEFAULT_IMG_PATH);
////                split[0]=ImgConstant.DEFAULT_IMG_PATH;
////            }
//            i.setImgSrc(split[0]);
//            i.setImgSrcList(Arrays.asList(split));
//        });
//        goodsPageInfo.setList(list);

//        List<Goods> resultList = goodsService.list(new LambdaQueryWrapper<Goods>()
//                .eq(!positionId.equals("-1"), Goods::getLocationId, positionId)
//                .eq(operation != -1, Goods::getType, operation)
//                .eq(categoryType == -1, Goods::getCategoryType, categoryType)
//                .like(ObjectUtil.isNotEmpty(searchKey), Goods::getTitle, searchKey));

        List<CategoryRes> goodsInfo = categoryService.getGoodsInfo(GoodsStatusEnum.REVIEW_PASSED.getType(),user.getId());
        List<Location> locationList = locationService.list();


        // ????????????
        modelAndView.addObject("locationList", locationList);
        // ??????????????????
        modelAndView.addObject("goodsInfos", goodsInfo);

        // ?????????Category
        modelAndView.addObject("selectedCategory", defaultCategory);
        // ?????????Position
        modelAndView.addObject("selectedPosition", defaultLocation);
        // ?????????Operation
        modelAndView.addObject("selectedOperation", operationByType.getType());
        // ?????????keyword
        modelAndView.addObject("searchKey", searchKey);

        // ????????????????????????
//        modelAndView.addObject("resultList", resultList);
        modelAndView.addObject("resultPageList", goodsPageInfo);


//        modelAndView.addObject("categoryList", categoryList);


        return modelAndView;
    }

    @GetMapping("detail")
    public ModelAndView detail(@RequestParam(value = "id", required = true) String id,
                               Integer status,
                               Authentication authentication, HttpServletRequest request) {
        Goods goods = goodsService.getById(id);
        ModelAndView modelAndView = new ModelAndView("detail");


        GoodsRes goodsRes = new GoodsRes();
        goodsRes.setId(id);
        goodsRes.setCategory(categoryService.getOne(new LambdaQueryWrapper<Category>().eq(Category::getType, goods.getCategoryType())));
        goodsRes.setTitle(goods.getTitle());
        goodsRes.setLocation(locationService.getById(goods.getLocationId()));
        goodsRes.setDescription(goods.getDescription());
        String imgSrc = goods.getImgSrc();
//        if (ObjectUtil.isEmpty(imgSrc)){
//            imgSrc=ImgConstant.DEFAULT_ITEM_PATH;
//        }
        goodsRes.setImgSrc(imgSrc);
        goodsRes.setImgSrcList(Arrays.asList(imgSrc.split(",")));
        Integer uid = goods.getUid();
        User user = userService.getById(uid);

        try {
            String objectName = user.getAvatar();
            if (ObjectUtil.isEmpty(user.getAvatar())) {
                // ??????????????????,??????????????????????????????????????????

                List<MinioItem> allObjectsByPrefix = minioTemplate.getAllObjectsByPrefix(minioProperties.getBucketName(), AvatarConstant.DEFAULT_AVATAR_PATH, true);
                int randomAvatar = ThreadLocalRandom.current().nextInt(9) + 1;
                MinioItem minioItem = allObjectsByPrefix.get(randomAvatar);
//                objectName = minioItem.getObjectName();
                objectName = minioTemplate.objectUrl(minioProperties.getBucketName(), minioItem.getObjectName());
//                ThreadLocalRandom.current().nextInt(10)
            }
//            user.setAvatar(minioTemplate.objectUrl(minioProperties.getBucketName(), objectName));
            user.setAvatar(objectName);

        } catch (Exception e) {
            log.error("??????MinIo?????? ???????????? {} ???????????? {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
        }

        goodsRes.setUser(user);
        goodsRes.setCreateDate(DateUtil.formatDate(goods.getCreateDate()));
        OperationEnum operationByType = OperationEnum.getOperationByType(goods.getType());
        goodsRes.setOperation(operationByType);
        goodsRes.setOperationName(operationByType.getName());
        goodsRes.setOperationType(operationByType.getType());

        GoodsStatusEnum statusByType = GoodsStatusEnum.getStatusByType(goods.getStatus());
        goodsRes.setStatus(statusByType);
        goodsRes.setStatusName(statusByType.getMsg());
        goodsRes.setStatusType(statusByType.getType());
//        goodsRes.setStatus(GoodsStatusEnum.getStatusByType(goods.getStatus()));
//        goodsRes.setOperation(OperationEnum.getOperationByType(goods.getType()));

        List<Goods> otherGoods = goodsService.list(new LambdaQueryWrapper<Goods>().eq(Goods::getUid, uid).ne(Goods::getId, id));

        List<GoodsRes> otherList = otherGoods.stream().map(i -> {
            GoodsRes other = new GoodsRes();
            other.setId(i.getId());
            other.setCategory(categoryService.getOne(new LambdaQueryWrapper<Category>().eq(Category::getType, i.getCategoryType())));
            other.setTitle(i.getTitle());
            other.setLocation(locationService.getById(i.getLocationId()));
            other.setDescription(i.getDescription());
            String otherImgSrc = i.getImgSrc();
//        if (ObjectUtil.isEmpty(imgSrc)){
//            imgSrc=ImgConstant.DEFAULT_ITEM_PATH;
//        }
            other.setImgSrc(otherImgSrc);
            other.setImgSrcList(Arrays.asList(otherImgSrc.split(",")));
            other.setUser(user);
            other.setCreateDate(DateUtil.formatDateTime(i.getCreateDate()));
//            other.setStatus(GoodsStatusEnum.getStatusByType(i.getStatus()));
//            other.setOperation(OperationEnum.getOperationByType(i.getType()));

            OperationEnum otherOperationByType = OperationEnum.getOperationByType(i.getType());
            other.setOperation(otherOperationByType);
            other.setOperationName(otherOperationByType.getName());
            other.setOperationType(otherOperationByType.getType());

            GoodsStatusEnum otherStatusByType = GoodsStatusEnum.getStatusByType(i.getStatus());
            other.setStatus(otherStatusByType);
            other.setStatusName(otherStatusByType.getMsg());
            other.setStatusType(otherStatusByType.getType());

            other.setImgSrcList(Arrays.asList(i.getImgSrc().split(",")));
            return other;
        }).collect(Collectors.toList());
        User loginUser = (User) authentication.getPrincipal();
        List<CategoryRes> goodsInfo = categoryService.getGoodsInfo(GoodsStatusEnum.REVIEW_PASSED.getType(), loginUser.getId());

        List<Location> locationList = locationService.list();

//        User loginUser = (User) request.getSession().getAttribute(LoginConstant.USER);
//        User loginUser = (User) authentication.getPrincipal();
        List<ApplyForm> list = applyFormService.list(new LambdaQueryWrapper<ApplyForm>().eq(ApplyForm::getGoodsId, goods.getId())
                .eq(ApplyForm::getUid, loginUser.getId())
        .eq(ApplyForm::getStatus,FormStatusEnum.APPLY_FAILED));


        modelAndView.addObject("isApply", !list.isEmpty());
//        if (list.size()>0) {
//            modelAndView.addObject("applyStatus", FormStatusEnum.getStatusByType(list.get(0).getStatus()));
//        }
        // ????????????
        modelAndView.addObject("locationList", locationList);
        // ????????????
//        modelAndView.addObject("currentUser", loginUser.getId());
        // ??????????????????
        modelAndView.addObject("goodsInfos", goodsInfo);
        // ??????????????????
        modelAndView.addObject("goods", goodsRes);
        // ?????????????????????????????????
        modelAndView.addObject("otherGoods", otherList.size() > 3 ? otherList.subList(0, 3) : otherList);

        return modelAndView;
    }

    @GetMapping("profile")
    public ModelAndView profile(Authentication authentication) {

        ModelAndView modelAndView = new ModelAndView("profile");
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

    @PostMapping("changeProfile")
    public ModelAndView changeProfile(MultipartFile newAvatar, @Validated ProfileReq profileReq, Authentication authentication, RedirectAttributes attributes) {
        String info = "????????????";
        ModelAndView modelAndView = new ModelAndView("redirect:/user/profile");
        String newPassword = profileReq.getNewPassword();
        String reNewPass = profileReq.getReNewPass();

        if (ObjectUtil.hasEmpty(newPassword, newPassword) || !newPassword.equals(reNewPass)) {
            modelAndView.addObject("info", "????????????,??????????????????????????????");
            return modelAndView;
        }

        System.out.println(profileReq);
        User user = (User) authentication.getPrincipal();
        User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getId, profileReq.getUid()));


        String suffix = FileUtil.getSuffix(newAvatar.getOriginalFilename());
        String newFileName = IdUtil.simpleUUID() + '.' + suffix;
        if (newAvatar.getSize() != 0) {
            try (InputStream inputStream = newAvatar.getInputStream()) {
                minioTemplate.putFile(minioProperties.getBucketName(), newFileName, inputStream, newAvatar.getContentType());
                String avatar = minioTemplate.objectUrl(minioProperties.getBucketName(), newFileName);
                user.setAvatar(avatar);
                one.setAvatar(avatar);
            } catch (Exception e) {
                log.error("????????????????????? ????????????{} ???????????? {}", e.getMessage(), Arrays.toString(e.getStackTrace()));
                info = "?????????????????????";
            }
        }

        user.setEmail(profileReq.getNewEmail());
        user.setAccount(profileReq.getNewAccount());
        user.setQq(profileReq.getNewQQ());
        user.setUncrypted(profileReq.getNewPassword());
        user.setPassword(PassUtils.bCryptEncode(profileReq.getNewPassword()));


        one.setEmail(profileReq.getNewEmail());
        one.setAccount(profileReq.getNewAccount());
        one.setQq(profileReq.getNewQQ());
        one.setUncrypted(profileReq.getNewPassword());
        one.setPassword(PassUtils.bCryptEncode(profileReq.getNewPassword()));


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

        userService.saveOrUpdate(one);

        // ???????????????
        attributes.addFlashAttribute("loginUser", user);
        attributes.addFlashAttribute("info", info);

//        modelAndView.addObject("loginUser",user);
//        modelAndView.addObject("info",info);
        return modelAndView;
    }

    @PostMapping("pointsExpired")
    public HttpResult pointsExpired() {
        // TODO: 2022/4/16 ??????????????????  ?????????????????????????????????????????????
        return HttpResult.ok();

    }

    @ApiOperation("???????????? ")
    @PostMapping("/submitGoods")
    public void submitGoods() {
        // TODO: 2022/4/16 ???????????? ????????????????????????????????????,???????????????,
        //  ????????????????????????????????? ?????????????????????????????????????????? ,?????????????????????????????????
        //  ????????????????????????????????????????????????
    }

    @ApiOperation("???????????? ")
    @PostMapping("/applyForMyGoods")
    @ResponseBody
    public HttpResult applyForMyGoods(@RequestBody ApplyForm applyReq, Authentication authentication) {
        System.out.println(applyReq);
//        String locationId = applyReq.getLocationId();
//        String locationDetail = applyReq.getLocationDetail();
//        String uid = applyReq.getUid();
//        String tel = applyReq.getTel();

        // TODO: 2022/4/16 ???????????? ??????????????????????????????????????????????????????????????????
        //  ??????????????????????????????????????????????????????????????? ???????????????  ????????????????????????????????????
        //  ???????????????????????????????????????????????????
        String goodsId = applyReq.getGoodsId();

//        if (!CheckUtils.isMobileNO(tel)){
//            return HttpResult.error("???????????????????????? ???????????????!");
//        }
        User user = (User) authentication.getPrincipal();

//        if (ObjectUtil.hasEmpty(locationI/d,user.getId(),goodsId,locationDetail)){
//            return HttpResult.error("???????????????????????? ???????????????");
//        }

        List<ApplyForm> list = applyFormService.list(new LambdaQueryWrapper<ApplyForm>().eq(ApplyForm::getGoodsId, goodsId).eq(ApplyForm::getUid, user.getId()));
//        if (list.size() > 0) {
//            return HttpResult.error("??????????????????????????? ?????????????????????");
//        }



        ApplyForm applyForm = new ApplyForm();
        applyForm.setId(IdUtil.simpleUUID());
        applyForm.setGoodsId(goodsId);
//        applyForm.setLocationId(locationId);
//        applyForm.setLocationDetail(locationDetail);
//        applyForm.setTel(tel);
        applyForm.setCreateDate(new Date());
        applyForm.setUid(user.getId());
        boolean flag = true;
        try {
            Goods byId = goodsService.getById(goodsId);
            flag = applyFormService.save(applyForm);
            byId.setStatus(GoodsStatusEnum.BE_APPLIED.getType());
            goodsService.saveOrUpdate(byId);
        } catch (Exception e) {
            log.error("?????????????????? ????????????{} ????????????{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            flag = false;
        }

        return flag ? HttpResult.ok("???????????? ???????????????????????????????????? ?????????????????????") : HttpResult.error("???????????? ?????????????????????");
    }

    @ApiOperation("????????????")
    @PostMapping("/getPrize")
    public void getPrize() {
        // TODO: 2022/4/16 ???????????????????????????????????????
    }

    @GetMapping("/getNormalUsers")
    @ResponseBody
    public HttpResult getNormalUsers() {
        return HttpResult.ok(userService.getNormalUser());
    }


    @Autowired
    private ComplaintFormService complaintFormService;


    @ApiOperation("????????????")
    @PostMapping("/complaint")
    @ResponseBody
    public Boolean complaint(@RequestParam("applyId") String applyId, Authentication authentication) {
        // TODO: 2022/4/16 ?????????????????????????????????????????????  ????????????????????????????????????????????????
        //  ???????????????????????????????????????   ???????????????????????????????????????????????? 3 ??? ?????????????????????
        //  ??????????????????????????? ???????????????
        final ComplaintForm complaintForm = new ComplaintForm();
        complaintForm.setUid(((User)authentication.getPrincipal()).getId());
        complaintForm.setApplyId(applyId);
        boolean save = true;
        try {
            save=complaintFormService.save(complaintForm);
        }catch (Exception e){
            save=false;
            log.error("??????????????????");
        }
        return save;
    }

    @GetMapping("list")
    @ResponseBody
    public HttpResult list() {
        List<User> list = userService.list();
        return HttpResult.ok(list);
    }

    @GetMapping("listByAccount")
    @ResponseBody
    public HttpResult listByAccount(String account, String oldAccount) {

        User oldUser = userService.getOne(new LambdaQueryWrapper<User>().eq(ObjectUtil.isNotEmpty(oldAccount), User::getAccount, oldAccount));
        User newUser = userService.getOne(new LambdaQueryWrapper<User>().eq(ObjectUtil.isNotEmpty(account), User::getAccount, account));
        return HttpResult.ok(ObjectUtil.isNotEmpty(newUser) && !oldUser.getId().equals(newUser.getId()));
    }



    @GetMapping("/toComplaint")
    public ModelAndView toComplaint(GoodsSearchReq goodsSearchReq, Authentication authentication) {

        ModelAndView modelAndView = new ModelAndView("complaint");

        Integer categoryType = goodsSearchReq.getCategoryType();
        Integer operation = goodsSearchReq.getOperation();
        String positionId = goodsSearchReq.getPositionId();
        String searchKey = goodsSearchReq.getSearchKey();

        OperationEnum operationByType = OperationEnum.getOperationByType(operation);

        Category defaultCategory = categoryType == -1 ? Category.DEFAULT : categoryService.getOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getType, categoryType), false);

        Location defaultLocation = positionId.equals("-1") ? Location.DEFAULT : locationService.getOne(new LambdaQueryWrapper<Location>()
                .eq(Location::getId, positionId), false);


        User user = (User) authentication.getPrincipal();

//
////        // ????????????
//        List<String> userApply = applyFormService.list(new LambdaQueryWrapper<ApplyForm>()
//                .eq(ApplyForm::getStatus, FormStatusEnum.APPLY_SUCCESS.getType())).stream().map(ApplyForm::getGoodsId).collect(Collectors.toList());
//
//        // ???????????????  size=0
//        // ?????????  size=0
//        List<ComplaintForm> complaintForms = complaintFormService.list(new LambdaQueryWrapper<ComplaintForm>()
////                .in(!userApply.isEmpty(), ComplaintForm::getApplyId, userApply.stream().map(ApplyForm::getId).collect(Collectors.toList()))
////                .ne(ComplaintForm::getStatus, ComplaintStatusEnum.IN_COMPLAINT.getType()));
//                .eq(ComplaintForm::getStatus, ComplaintStatusEnum.IN_COMPLAINT.getType()));
////                .notIn(!applyIds.isEmpty(),ApplyForm::getId,applyIds));
//
//        // ????????????????????????
//        // ??????????????????
//        List<String> collect = complaintForms.stream().map(i -> applyFormService.getById(i.getApplyId())).map(ApplyForm::getGoodsId).collect(Collectors.toList());
//
//        // ??????????????????????????????????????????????????????
////        final Set<String> strings = CollectionUtil.intersectionDistinct(collect.isEmpty()?userApply:collect, userApply);
//        // ?????????????????????????????????????????????--????????????????????????
//        final Collection<String> strings = CollectionUtil.disjunction(collect, userApply);

//        complaintFormService.list(new LambdaQueryWrapper<ComplaintForm>().in(ComplaintForm::getApplyId))


        final List<String> collect = goodsService.list(
                new LambdaQueryWrapper<Goods>()
                        .eq(!positionId.equals("-1"), Goods::getLocationId, positionId)
                        .eq(operation != -1, Goods::getType, operation)
                        .eq(categoryType != -1, Goods::getCategoryType, categoryType)
                        .eq(Goods::getStatus,GoodsStatusEnum.END.getType())
                        .like(ObjectUtil.isNotEmpty(searchKey), Goods::getTitle, searchKey)).stream().map(Goods::getId).collect(Collectors.toList());

        List<String> userApply1 =collect.isEmpty()?new ArrayList<>():applyFormService.list(new LambdaQueryWrapper<ApplyForm>()
                .eq(ApplyForm::getStatus, FormStatusEnum.APPLY_SUCCESS.getType())
                .in(ApplyForm::getGoodsId,collect)
        ).stream().map(ApplyForm::getId).collect(Collectors.toList());

        // ???????????????  size=0
        // ?????????  size=0
        List<ComplaintForm> complaintForms1 = complaintFormService.list(new LambdaQueryWrapper<ComplaintForm>()
//                .in(!userApply.isEmpty(), ComplaintForm::getApplyId, userApply.stream().map(ApplyForm::getId).collect(Collectors.toList()))
//                .ne(ComplaintForm::getStatus, ComplaintStatusEnum.IN_COMPLAINT.getType()));
                .eq(ComplaintForm::getStatus, ComplaintStatusEnum.IN_COMPLAINT.getType()));
//                .notIn(!applyIds.isEmpty(),ApplyForm::getId,applyIds));

        // ????????????????????????
        // ??????????????????
        List<String> collect1 = complaintForms1.stream().map(i -> applyFormService.getById(i.getApplyId())).map(ApplyForm::getId).collect(Collectors.toList());

        // ?????????????????????????????????????????????--????????????????????????
//        final Collection<String> strings = CollectionUtil.disjunction(collect1, userApply1);
        // ??????????????????????????????????????????????????????????????????
        final Collection<String> strings = CollectionUtil.disjunction(CollectionUtil.union(collect1, userApply1), collect1);
        // ??????????????????????????????????????????,

//                        .in(Goods::getId, strings));

        IPage<ApplyForm> page =strings.isEmpty()?new Page<>():applyFormService.page(
                new QueryPage<ApplyForm>().getPage(goodsSearchReq),
                new LambdaQueryWrapper<ApplyForm>().in(ApplyForm::getId,strings));
//        PageInfo<Goods> goodsPageInfo = new PageInfo<>(page);
        PageInfo goodsPageInfo = new PageInfo<>(page);

        List<ApplyFormRes> list = page.getRecords().stream().map(i -> {
            final Goods goods = goodsService.getById(i.getGoodsId());
            String[] split = goods.getImgSrc().split(",");
//            if (split.length==1&&ObjectUtil.isEmpty(split[0])){
//                i.setImgSrc(ImgConstant.DEFAULT_IMG_PATH);
//                split[0]=ImgConstant.DEFAULT_IMG_PATH;
//            }
            goods.setImgSrc(split[0]);
            goods.setImgSrcList(Arrays.asList(split));

            ApplyFormRes applyFormRes = new ApplyFormRes();

            final GoodsRes goodsRes = new GoodsRes();
//            goodsRes.setOperation(OperationEnum.getOperationByType(i.getType()));
            OperationEnum iOperationByType = OperationEnum.getOperationByType(goods.getType());
            goodsRes.setOperation(iOperationByType);
            goodsRes.setOperationName(iOperationByType.getName());
            goodsRes.setOperationType(iOperationByType.getType());

            GoodsStatusEnum statusByType = GoodsStatusEnum.getStatusByType(i.getStatus());
            goodsRes.setStatus(statusByType);
            goodsRes.setStatusName(statusByType.getMsg());
            goodsRes.setStatusType(statusByType.getType());

            goodsRes.setUser(userService.getById(i.getUid()));
            goodsRes.setImgSrcList(goods.getImgSrcList());
            goodsRes.setImgSrc(goods.getImgSrc());
            goodsRes.setDescription(goods.getDescription());
            goodsRes.setLocation(goods.getLocationId().equals("-1") ? Location.DEFAULT : locationService.getById(goods.getLocationId()));
            goodsRes.setTitle(goods.getTitle());
            goodsRes.setCategory(categoryService.getOne(new LambdaQueryWrapper<Category>().eq(Category::getType, goods.getCategoryType())));
            goodsRes.setId(i.getGoodsId());
            goodsRes.setCreateDate(DateUtil.formatDateTime(i.getCreateDate()));
            applyFormRes.setId(i.getId());
            applyFormRes.setGoodsRes(goodsRes);
            final ApplyForm applyForm = applyFormService.list(new LambdaQueryWrapper<ApplyForm>().eq(ApplyForm::getId, i.getId())).get(0);
            final User byId = userService.getById(applyForm.getUid());
            applyFormRes.setApplyUser(byId);
            applyFormRes.setStatus(FormStatusEnum.getStatusByType(i.getStatus()));
            String imgSrcStr=ObjectUtil.isNotEmpty(goodsRes.getImgSrc())?goodsRes.getImgSrc():"/icons/emptyImg.png";
            applyFormRes.setPopUpDetail("??????: <span>" + goodsRes.getTitle() + "</span>" +"<br>"+
                    "????????????: <span>" + goodsRes.getLocation().getPosition() + "</span>" +"<br>"+
                    "??????: <span>" + goodsRes.getDescription() + "</span>" +"<br>"+
                    "??????: <img src='" + imgSrcStr + "'/>" +"<br>"+
                    "????????????: <span>" + goodsRes.getUser().getName() + "</span>" +"<br>"+
                    "??????: <span>" + goodsRes.getOperationName() + "</span>" +"<br>"+
                    "????????????: <span>" + goodsRes.getCreateDate() + "</span>" +"<br>"+
                    "?????????: <span>" + applyFormRes.getApplyUser().getName() + "</span>");
//            goodsRes.setStatus(GoodsStatusEnum.getStatusByType(i.getStatus()));
            return applyFormRes;
        }).collect(Collectors.toList());

        goodsPageInfo.setList(list);
//        List<Goods> list = goodsPageInfo.getList();
//        list.forEach(i -> {
//            String[] split = i.getImgSrc().split(",");
////            if (split.length==1&&ObjectUtil.isEmpty(split[0])){
////                i.setImgSrc(ImgConstant.DEFAULT_IMG_PATH);
////                split[0]=ImgConstant.DEFAULT_IMG_PATH;
////            }
//            i.setImgSrc(split[0]);
//            i.setImgSrcList(Arrays.asList(split));
//        });
//        goodsPageInfo.setList(list);

//        List<Goods> resultList = goodsService.list(new LambdaQueryWrapper<Goods>()
//                .eq(!positionId.equals("-1"), Goods::getLocationId, positionId)
//                .eq(operation != -1, Goods::getType, operation)
//                .eq(categoryType == -1, Goods::getCategoryType, categoryType)
//                .like(ObjectUtil.isNotEmpty(searchKey), Goods::getTitle, searchKey));

        List<CategoryRes> goodsInfo = categoryService.getGoodsInfo(GoodsStatusEnum.REVIEW_PASSED.getType(), user.getId());
        List<Location> locationList = locationService.list();


        // ????????????
        modelAndView.addObject("locationList", locationList);
        // ??????????????????
        modelAndView.addObject("goodsInfos", goodsInfo);

        // ?????????Category
        modelAndView.addObject("selectedCategory", defaultCategory);
        // ?????????Position
        modelAndView.addObject("selectedPosition", defaultLocation);
        // ?????????Operation
        modelAndView.addObject("selectedOperation", operationByType.getType());
        // ?????????keyword
        modelAndView.addObject("searchKey", searchKey);


        // ????????????????????????
//        modelAndView.addObject("resultList", resultList);
        modelAndView.addObject("resultPageList", goodsPageInfo);
        modelAndView.addObject("loginUser", authentication.getPrincipal());


//        modelAndView.addObject("categoryList", categoryList);


        return modelAndView;
    }

    @DeleteMapping("del")
    public HttpResult del() {
        // TODO: 2022/4/16 ????????????
        return HttpResult.ok();
    }

    @PostMapping("add")
    public HttpResult add() {
        // TODO: 2022/4/16 ????????????
        return HttpResult.ok();

    }

    @PostMapping("update")
    public HttpResult update() {
        // TODO: 2022/4/16 ??????????????????   ----   ban?????????
        return HttpResult.ok();

    }

}


