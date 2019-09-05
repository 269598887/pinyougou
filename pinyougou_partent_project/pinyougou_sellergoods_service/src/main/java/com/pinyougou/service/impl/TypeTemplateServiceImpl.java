package com.pinyougou.service.impl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.page.PageResult;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.pojo.TbTypeTemplateExample.Criteria;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service

public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    @Transactional
    public void add(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.insert(typeTemplate);
    }


    /**
     * 修改
     */
    @Override
    @Transactional
    public void update(TbTypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKey(typeTemplate);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbTypeTemplate findOne(Long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbTypeTemplateExample example = new TbTypeTemplateExample();
        Criteria criteria = example.createCriteria();

        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
            if (typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0) {
                criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
            }
            if (typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0) {
                criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
            }
            if (typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0) {
                criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
            }

        }

        Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(example);
        saveToRedis();//将数据缓存到redis
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据模板id查询规格列表
     *
     * @param id
     * @return
     */
    @Override

    public List<Map> findSpecList(Long id) {
        TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
        List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
        for (Map map : list) {
            TbSpecificationOptionExample example = new TbSpecificationOptionExample();
            TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
            criteria.andSpecIdEqualTo(new Long((Integer) map.get("id")));
            List<TbSpecificationOption> specificationOptions = specificationOptionMapper.selectByExample(example);
            map.put("options", specificationOptions);
        }
        return list;
    }

    //缓存品牌列表，规格列表
    private void saveToRedis() {
        //获取模板数据
        List<TbTypeTemplate> typeTemplateList = findAll();
        //遍历模板
        for (TbTypeTemplate typeTemplate : typeTemplateList) {
            //储存品牌列表
            List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
            if (brandList != null || brandList.size() > 0) {
                redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
            }
            //储存规格列表
            List<Map> specList = findSpecList(typeTemplate.getId());
            if (specList != null || specList.size() > 0) {
                redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
            }
        }
    }
}
