package com.pinyougou.service.impl;

import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.page.PageResult;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.specifiction.Specifiction;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationExample;
import com.pinyougou.pojo.TbSpecificationExample.Criteria;
import com.pinyougou.service.SpecificationService;
import org.springframework.transaction.annotation.Transactional;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service

public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private TbSpecificationMapper specificationMapper;
    @Autowired
    private TbSpecificationOptionMapper tbSpecificationOptionMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSpecification> findAll() {
        return specificationMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    @Transactional
    public void add(Specifiction specification) {
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.insert(tbSpecification);
        Long id = tbSpecification.getId();
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
            tbSpecificationOption.setSpecId(id);
            tbSpecificationOptionMapper.insert(tbSpecificationOption);
        }
    }


    /**
     * 修改
     */
    @Override
    @Transactional
    public void update(Specifiction specification) {
        TbSpecification tbSpecification = specification.getSpecification();
        specificationMapper.updateByPrimaryKey(tbSpecification);
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(tbSpecification.getId());
        tbSpecificationOptionMapper.deleteByExample(example);
        List<TbSpecificationOption> specificationOptionList = specification.getSpecificationOptionList();
        Long id = tbSpecification.getId();
        for (TbSpecificationOption tbSpecificationOption : specificationOptionList) {
            tbSpecificationOption.setSpecId(id);
            tbSpecificationOptionMapper.insert(tbSpecificationOption);
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Specifiction findOne(Long id) {
        Specifiction specifiction = new Specifiction();
        TbSpecification tbSpecification = specificationMapper.selectByPrimaryKey(id);
        specifiction.setSpecification(tbSpecification);
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<TbSpecificationOption> tbSpecificationOptions = tbSpecificationOptionMapper.selectByExample(example);
        specifiction.setSpecificationOptionList(tbSpecificationOptions);
        return specifiction;
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(id);
            tbSpecificationOptionMapper.deleteByExample(example);
            specificationMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSpecificationExample example = new TbSpecificationExample();
        Criteria criteria = example.createCriteria();

        if (specification != null) {
            if (specification.getSpecName() != null && specification.getSpecName().length() > 0) {
                criteria.andSpecNameLike("%" + specification.getSpecName() + "%");
            }

        }

        Page<TbSpecification> page = (Page<TbSpecification>) specificationMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {

        return specificationMapper.selectOptionList();
    }

}
