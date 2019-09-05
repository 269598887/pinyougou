package cn.itcast.sms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class QueueController {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @RequestMapping("/send")
    public void send(String text){
        jmsMessagingTemplate.convertAndSend("itcast",text);
    }
    @RequestMapping("/sendsms")
    public void sendSms(){
        Map map=new HashMap();
        map.put("mobile","13700276844");
        map.put("template_code","SMS_169899167");
        map.put("sign_name","百莲凯项目");
        map.put("param","{\"code\":\"520131\"}");
        jmsMessagingTemplate.convertAndSend("sms",map);
    }
}
