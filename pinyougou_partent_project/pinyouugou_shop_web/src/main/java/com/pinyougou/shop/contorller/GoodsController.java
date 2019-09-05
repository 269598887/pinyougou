package com.pinyougou.shop.contorller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.goods.Goods;
import com.pinyougou.messger.Result;
import com.pinyougou.page.PageResult;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page, int rows){
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			String name = SecurityContextHolder.getContext().getAuthentication().getName();
			TbGoods tbGoods = goods.getGoods();
			tbGoods.setSellerId(name);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		//效验商家id
		Goods goods1 = goodsService.findOne(goods.getGoods().getId());
		//判断当前登录商家的信息
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!goods1.getGoods().getSellerId().equals(name)||!goods.getGoods().getSellerId().equals(name)){
			return new Result(false,"非法操作");
		}
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}

	/**
	 * 分页+条件
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		//根据用户id查询用户商品列表
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(sellerId);
		return goodsService.findPage(goods, page, rows);		
	}

	/**
	 * 根据商品Id管理商品上下架
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateisMarketable")
	public Result updateisMarketable(Long[] ids,String status){
		try {
			goodsService.updateisMarketable(ids,status);
			return new Result(true,"添加上架成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(true,"添加上架失败");
		}
	}
	
}
