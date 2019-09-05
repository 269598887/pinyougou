package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ItemDeleteListener implements MessageListener {
   @Autowired
   private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        try {
           ObjectMessage objectMessage= (ObjectMessage) message;
           Long[] goodsIds = (Long[]) objectMessage.getObject();
            System.out.println("ItemDeleteListener监听收到消息...."+goodsIds);
           itemSearchService.deleteByGoodsIds(Arrays.asList(goodsIds));
            System.out.println("成功删除索引库中的记录");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
