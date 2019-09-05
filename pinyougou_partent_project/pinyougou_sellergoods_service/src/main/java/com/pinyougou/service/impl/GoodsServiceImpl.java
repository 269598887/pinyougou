package com.pinyougou.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.goods.Goods;
import com.pinyougou.mapper.*;
import com.pinyougou.page.PageResult;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.service.GoodsService;
import org.springframework.transaction.annotation.Transactional;


/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service

public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    @Transactional
    public void add(Goods goods) {
        TbGoods tbGoods = goods.getGoods();
        tbGoods.setAuditStatus("0");
        goodsMapper.insert(tbGoods);
        //获取插入数据的主键
        Long id = tbGoods.getId();
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(id);
        goodsDescMapper.insert(goodsDesc);
        saveItemList(goods);
    }

    //封装添加规格列表方法

    private void saveItemList(Goods goods) {
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            for (TbItem item : goods.getItemList()) {
                String name = goods.getGoods().getGoodsName();
                Map<String, Object> specMap = JSON.parseObject(item.getSpec());
                for (String key : specMap.keySet()) {
                    name += " " + specMap.get(key);
                }
                item.setTitle(name);
                setItemValus(goods, item);
                itemMapper.insert(item);
            }
        } else {
            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());//商品kpu+规格描述作为sku名称
            item.setPrice(goods.getGoods().getPrice());//价格
            item.setStatus("1");//状态
            item.setIsDefault("1");//是否默认
            item.setNum(999999);//库存数量
            item.setSpec("{}");
            setItemValus(goods, item);
            itemMapper.insert(item);
        }
    }

    private void setItemValus(Goods goods, TbItem item) {
        item.setGoodsId(goods.getGoods().getId());//修改spu编号
        item.setSellerId(goods.getGoods().getSellerId());//修改商家编号
        item.setCategoryid(goods.getGoods().getCategory3Id());//修改三级商品分类
        item.setCreateTime(new Date());//创建日期
        item.setUpdateTime(new Date());//修改日期
        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //商家名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getNickName());
        //图片地址（取spu第一个图片）
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList.size() > 0) {
            item.setImage((String) imageList.get(0).get("url"));
        }
    }


    /**
     * 修改
     */
    @Override
    @Transactional
    public void update(Goods goods) {
        //设置申请状态，如果是经过修改的商品状态都是0
        goods.getGoods().setAuditStatus("0");
        //保存商品表
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        //保存扩展表
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        //删除原有sku列表数据
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(example);
        //添加新的sku列表数据
        saveItemList(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
        //查询sku列表数据
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> items = itemMapper.selectByExample(example);
        goods.setItemList(items);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //修改删除状态为1为逻辑删除
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    /**
     * 分页条件查询
     *
     * @param goods
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        criteria.andIsDeleteIsNull();
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }

        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id进行状态修改
     *
     * @param ids
     * @param status
     */
    @Override
    @Transactional
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    /**
     * 根据商品Id管理商品上下架
     * @param ids
     * @param status
     */
    @Override
    @Transactional
    public void updateisMarketable(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            tbGoods.setIsMarketable(status);
            goodsMapper.updateByPrimaryKey(tbGoods);
        }
    }

    /**
     * 根据商品id和状态查询Item表信息
     * @param goodsIds
     * @param status
     * @return
     */
    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        criteria.andStatusEqualTo(status);
        return itemMapper.selectByExample(example);
    }

}
