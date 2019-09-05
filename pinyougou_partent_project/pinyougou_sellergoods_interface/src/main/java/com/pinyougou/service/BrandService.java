package com.pinyougou.service;

import com.pinyougou.page.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    /**
     * 查询所有
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 分页查询
     * @param currPage
     * @param pageSize
     * @return
     */
    PageResult<TbBrand> findByPage(Integer currPage, Integer pageSize);

    /**
     * 品牌添加
     * @param tbBrand
     */
    void add(TbBrand tbBrand);

    /**
     * id品牌查找
     * @param id
     * @return
     */
    TbBrand findOne(Long id);

    /**
     * 品牌修改
     * @param tbBrand
     */
    void updateBrand(TbBrand tbBrand);

    /**
     * 品牌删除
     * @param ids
     */
    void deleteBrand(Long[] ids);

    /**
     * 条件查找
     * @param tbBrand
     * @return
     */
    PageResult findBrandByCondition(TbBrand tbBrand,Integer currPage,Integer pageSize);

    /**
     * 查询品牌列表集合
     * @return
     */
    List<Map> selectOptionList();
}
