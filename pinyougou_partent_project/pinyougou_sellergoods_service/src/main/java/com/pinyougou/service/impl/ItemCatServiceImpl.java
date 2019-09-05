package com.pinyougou.service.impl;

import java.util.List;

import com.pinyougou.page.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.pojo.TbItemCatExample.Criteria;
import com.pinyougou.service.ItemCatService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service

public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbItemCat> findAll() {
        return itemCatMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    @Transactional
    public void add(TbItemCat itemCat) {
        itemCatMapper.insert(itemCat);
    }


    /**
     * 修改
     */
    @Override
    @Transactional
    public void update(TbItemCat itemCat) {
        itemCatMapper.updateByPrimaryKey(itemCat);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbItemCat findOne(Long id) {
        return itemCatMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public void delete(Long[] ids) throws RuntimeException {
        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        Criteria criteria = tbItemCatExample.createCriteria();
        for (Long id : ids) {
            criteria.andParentIdEqualTo(id);
            List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(tbItemCatExample);
            if (tbItemCats == null) {
                itemCatMapper.deleteByPrimaryKey(id);
            } else {
                throw new RuntimeException();
            }

        }
    }


    @Override
    public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbItemCatExample example = new TbItemCatExample();
        Criteria criteria = example.createCriteria();

        if (itemCat != null) {
            if (itemCat.getName() != null && itemCat.getName().length() > 0) {
                criteria.andNameLike("%" + itemCat.getName() + "%");
            }

        }

        Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<TbItemCat> findItemCatByParentId(Long parentId) {
        TbItemCatExample tbItemCatExample = new TbItemCatExample();
        Criteria criteria = tbItemCatExample.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        //一次性读取所有商品分类信息，进行缓存
        List<TbItemCat> itemCats = findAll();
        for (TbItemCat itemCat : itemCats) {
            if (itemCat != null) {
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
            }
        }
        System.out.println("更新缓存：商品分类列表");
        return itemCatMapper.selectByExample(tbItemCatExample);
    }

}
