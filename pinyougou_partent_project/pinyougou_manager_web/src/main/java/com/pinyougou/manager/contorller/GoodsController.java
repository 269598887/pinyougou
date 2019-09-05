package com.pinyougou.manager.contorller;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.goods.Goods;
import com.pinyougou.messger.Result;
import com.pinyougou.page.PageResult;

import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.service.GoodsService;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;



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
	/*@Reference
	private ItemSearchService itemSearchService;*/
	/*@Reference(timeout = 40000)
	private ItemPageService itemPageService;*/
	@Autowired
	private Destination queueSolrDestination;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination queueSolrDeleteDestination;
	@Autowired
	private Destination topicPageDestination;
	@Autowired
	private Destination topicPageDeleteDestination;
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
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
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
			//itemSearchService.deleteByGoodsIds(Arrays.asList(ids));
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
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
		return goodsService.findPage(goods, page, rows);		
	}

	/**
	 *
	 * 根据商品id修改商品审核状态
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status){
		try {
			goodsService.updateStatus(ids,status);
			if (status.equals("1")){//审核通过
				List<TbItem> itemList = goodsService.findItemListByGoodsIdandStatus(ids, status);
				//调用搜索接口实现数据批量导入
				if (itemList.size()>0){
					//itemSearchService.importList(itemList);
					final String jsonString = JSON.toJSONString(itemList);
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							System.out.println("------------------------------》》》 。。。。");
							return session.createTextMessage(jsonString);
						}
					});
					/*//修改状态生成静态页
				for (Long goodsid : ids) {
					itemPageService.genItemHtml(goodsid);
				}*/
					for (final Long goodsid : ids) {
						jmsTemplate.send(topicPageDestination, new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								return session.createTextMessage(goodsid+"");
							}
						});
					}
				}else {
					System.out.println("没有明细数据");
				}

			}
		return 	new Result(true,"审核成功");
		} catch (Exception e) {
			e.printStackTrace();
			return 	new Result(false,"审核失败");
		}
	}
	/*@RequestMapping("/genHtml")
	public void genHtml(Long goodsId){
		//itemPageService.genItemHtml(goodsId);
	}*/
}
