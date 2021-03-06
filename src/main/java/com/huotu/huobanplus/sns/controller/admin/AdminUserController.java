/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.huobanplus.sns.controller.admin;

import com.huotu.huobanplus.sns.annotation.CustomerId;
import com.huotu.huobanplus.sns.entity.Level;
import com.huotu.huobanplus.sns.entity.Tag;
import com.huotu.huobanplus.sns.entity.User;
import com.huotu.huobanplus.sns.entity.UserSuggested;
import com.huotu.huobanplus.sns.entity.support.AuthenticationType;
import com.huotu.huobanplus.sns.model.AuthenticationTypeModel;
import com.huotu.huobanplus.sns.model.admin.AdminTagsModel;
import com.huotu.huobanplus.sns.repository.LevelRepository;
import com.huotu.huobanplus.sns.repository.UserRepository;
import com.huotu.huobanplus.sns.repository.UserSuggestedRepository;
import com.huotu.huobanplus.sns.service.CommonConfigService;
import com.huotu.huobanplus.sns.service.UserService;
import com.huotu.huobanplus.sns.utils.ContractHelper;
import com.huotu.huobanplus.sns.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2016/10/11.
 */
@SuppressWarnings("unused")
@Controller
@RequestMapping("/top/user")
public class AdminUserController {

    @Autowired
    private UserService userService;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private CommonConfigService commonConfigService;
    @Autowired
    private UserSuggestedRepository userSuggestedRepository;

//    private final static String lessThan = "<";

    /**
     * 用户列表页
     *
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(@CustomerId Long customerId, @RequestParam(required = false) String selectType, Model model) throws IOException {
//        model.addAttribute("lessThan",lessThan);
        List<Level> levels = levelRepository.findAllByCustomerId(customerId, new Sort(Sort.Direction.ASC, "experience"));
        model.addAttribute("levels", levels);
        List<AuthenticationTypeModel> types = AuthenticationType.allTypes();
        model.addAttribute("types", types);
        model.addAttribute("selectType", selectType);
        return "/admin/user/userList";
    }

    /**
     * 根据条件搜索相应的用户列表
     *
     * @param page             页码
     * @param pageSize         每页条数
     * @param nickName         用户昵称
     * @param authenticationId 身份
     * @param levelId          等级id
     * @param sortName         排序名字
     * @param sortType         排序方式：升序，降序
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public ModelMap list(@RequestParam(required = false) Integer page,
                         @RequestParam(required = false) Integer pageSize,
                         @CustomerId Long customerId,
                         @RequestParam(required = false) String nickName,
                         @RequestParam(required = false) Integer authenticationId,
                         @RequestParam(required = false) Long levelId, String sortName, String sortType) throws IOException {
        if (Objects.isNull(page)) page = ContractHelper.list_page;
        if (Objects.isNull(pageSize)) pageSize = ContractHelper.list_pageSize;
        Sort sort;
        if (sortType.equals("asc"))
            sort = new Sort(Sort.Direction.ASC, sortName);
        else
            sort = new Sort(Sort.Direction.DESC, sortName);
        Pageable pageable = new PageRequest(page - 1, pageSize, sort);
        Page<User> pages = userService.findByNickNameAndAuthenticationIdAndLevelIdAndCustomerId(nickName, authenticationId,
                levelId, customerId, pageable);
        ModelMap map = new ModelMap();
        map.addAttribute("list", pages.getContent());
        map.addAttribute("total", pages.getTotalElements());
        map.addAttribute("pageSize", pageSize);
        map.addAttribute("page", page);
        map.addAttribute("pageCount", pages.getTotalPages());
        return map;
    }

    /**
     * 更改用户等级
     *
     * @param userId  用户id
     * @param levelId 等级id
     * @return 更改结果
     * @throws IOException
     */
    @RequestMapping(value = "/updateLevel", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap updateLevel(Long userId, Long levelId) throws IOException {
        Level level = levelRepository.getOne(levelId);
        User user = userRepository.getOne(userId);
        user.setLevel(level);
        user.setExperience(level.getExperience());
        userRepository.save(user);
        return ResultUtil.success();
    }

    /**
     * 更改用户身份
     *
     * @param userId           用户id
     * @param authenticationId 身份
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updateAuthentication", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap updateAuthentication(Long userId, int authenticationId) throws IOException {
        User user = userRepository.getOne(userId);
        user.setAuthenticationType(authenticationId);
        userRepository.save(user);
        return ResultUtil.success();
    }

    /**
     * 用户详情页
     *
     * @param userId 用户id
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(Long userId, String selectType, Model model) throws IOException {
        User user = userRepository.getOne(userId);
        model.addAttribute("user", user);
//        BoundHashOperations<String, String, Long> userOperations = redisTemplate
//                .boundHashOps(ContractHelper.userFlag + userId);
//        Long userAmount = userOperations.get("userAmount");
//        Long fansAmount = userOperations.get("fansAmount");
//        Long articleAmount = userOperations.get("articleAmount");
        Set<Tag> set = user.getTags();
        List<AdminTagsModel> adminTagsModels = set.stream()
                .map(tag -> new AdminTagsModel(tag.getId(), tag.getName())).collect(Collectors.toList());
        model.addAttribute("tags", adminTagsModels);
        model.addAttribute("authenticationType", AuthenticationType.getDescription(user.getAuthenticationType()));
        model.addAttribute("imgURL", user.getImgURL() == null ? "../../img/user.png" : user.getImgURL());
        model.addAttribute("userAmount", user.getUserAmount());
        model.addAttribute("fansAmount", user.getFansAmount());
        model.addAttribute("articleAmount", user.getArticleAmount());
        model.addAttribute("selectType", selectType);
        return "/admin/user/userEdit";
    }

    /**
     * 更新权限
     *
     * @param userId 用户id
     * @param power  权限
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/updatePower", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap updatePower(Long userId, String power) throws IOException {
        User user = userRepository.getOne(userId);
        user.setPower(power);
        userRepository.save(user);
        return ResultUtil.success();
    }

    /**
     * 推荐关注
     *
     * @param page
     * @param pageSize
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/suggestedFollow", method = RequestMethod.GET)
    public String suggestedFollow(@RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer pageSize,
                                  @CustomerId Long customerId, Model model) throws IOException {
        if (Objects.isNull(page)) page = ContractHelper.list_page;
        if (Objects.isNull(pageSize)) pageSize = ContractHelper.list_pageSize;
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(page - 1, pageSize, sort);
        Page<UserSuggested> pages = userSuggestedRepository.findByCustomerId(customerId, pageable);
        Long count = pages.getTotalElements();
        List<Long> idList = pages.getContent().stream().map(userSuggested ->
                userSuggested.getUser().getId()).collect(Collectors.toList());
//        List<UserSuggested> list = pages.getContent();
//        for(UserSuggested suggested:list){
//            if(StringUtils.isEmpty(suggested.getUser().getImgURL())){
//                suggested.getUser().setImgURL("../../img");
//            }
//        }
        model.addAttribute("total", pages.getTotalElements());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("page", page);
        model.addAttribute("pageCount", pages.getTotalPages());
        model.addAttribute("list", pages.getContent());
        model.addAttribute("idList", idList);
        model.addAttribute("url", commonConfigService.getWebUrl() + "/top/user/suggestedFollow?page=");
        return "/admin/user/suggestedFollow";
    }

    @RequestMapping(value = "/suggested", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap suggested(@CustomerId Long customerId, Long[] ids) throws Exception {
        for (Long id : ids) {
            User user = userRepository.getOne(id);
            UserSuggested userSuggested = new UserSuggested();
            userSuggested.setUser(user);
            userSuggested.setCustomerId(customerId);
            userSuggestedRepository.save(userSuggested);
        }
        return ResultUtil.success();
    }

    /**
     * 删除推荐
     *
     * @param id 用户id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/deleteSuggested", method = RequestMethod.POST)
    @ResponseBody
    public ModelMap deleteSuggested(@RequestParam Long id) throws Exception {
        Optional<UserSuggested> optional = userSuggestedRepository.findByUserId(id);
        userSuggestedRepository.delete(optional.orElse(null));
        return ResultUtil.success();
    }
}
