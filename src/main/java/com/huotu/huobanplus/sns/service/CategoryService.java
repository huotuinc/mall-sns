package com.huotu.huobanplus.sns.service;

import com.huotu.huobanplus.sns.model.AppCategoryModel;
import com.huotu.huobanplus.sns.model.admin.AdminBaseCategoryModel;
import com.huotu.huobanplus.sns.model.admin.AdminCategoryModel;
import com.huotu.huobanplus.sns.model.admin.AdminCategoryPageModel;

import java.util.List;

/**
 * 分类服务
 * Created by Administrator on 2016/10/12.
 */

public interface CategoryService {

    List<AppCategoryModel> getAppWikiCatalogList(Long customerId,Integer parentId);

    AdminCategoryPageModel getAdminCategoryList(Long customerId,Integer categoryType, String name, Integer pageNo, Integer pageSize);

    AdminCategoryModel getAdminCategory(Integer id);

    void save(Long customerId,Integer categoryType, Integer id, String name, Integer parent, Integer sort);

    List<AdminBaseCategoryModel> getAdminParentCategory(Long customerId,Integer categoryType);
}
