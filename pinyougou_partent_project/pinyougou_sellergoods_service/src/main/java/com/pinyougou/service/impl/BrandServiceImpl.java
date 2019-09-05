package com.pinyougou.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.page.PageResult;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service

public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper tbBrandMapper;

    /**
     * 查找所有
     *
     * @return
     */
    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    /**
     * 分页查询
     *
     * @param currPage
     * @param pageSize
     * @return
     */
    @Override
    public PageResult<TbBrand> findByPage(Integer currPage, Integer pageSize) {
        PageHelper.startPage(currPage, pageSize);
        Page page = (Page) tbBrandMapper.selectByExample(null);
        long total = page.getTotal();
        List result = page.getResult();
        PageResult<TbBrand> pageResult = new PageResult<>(total, result);
        return pageResult;
    }

    /**
     * 品牌添加
     *
     * @param tbBrand
     */
    @Override
    @Transactional
    public void add(TbBrand tbBrand) {
        tbBrandMapper.insert(tbBrand);
    }

    /**
     * id查找
     *
     * @param id
     * @return
     */
    @Override
    public TbBrand findOne(Long id) {
        TbBrand tbBrand = tbBrandMapper.selectByPrimaryKey(id);
        return tbBrand;
    }

    /**
     * 品牌修改
     *
     * @param tbBrand
     */
    @Override
    @Transactional
    public void updateBrand(TbBrand tbBrand) {
        tbBrandMapper.updateByPrimaryKey(tbBrand);
    }

    /**
     * 品牌删除
     *
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBrand(Long[] ids) {
        for (Long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }
    }

    /**
     * 条件查找
     * @param tbBrand
     * @param currPage
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findBrandByCondition(TbBrand tbBrand,Integer currPage,Integer pageSize) {
        PageHelper.startPage(currPage,pageSize);
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if (tbBrand.getName()!=null && tbBrand.getName().length()>0){
            criteria.andNameLike("%"+tbBrand.getName()+"%");
        }
        if (tbBrand.getFirstChar()!=null && tbBrand.getFirstChar().length()>0){
            criteria.andFirstCharLike("%"+tbBrand.getFirstChar()+"%");
        }
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(example);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 查询品牌列表集合
     * @return
     */
    @Override
    public List<Map> selectOptionList() {

        return tbBrandMapper.selectOptionList();
    }
}
