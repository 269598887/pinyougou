package com.pinyougou.cart.contorller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.messger.Result;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.utlis.IdWorker;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService weixinPayService;

    @RequestMapping("/createNative")
    public Map createnative(){
        IdWorker idWorker = new IdWorker();
        return weixinPayService.createNative(idWorker.nextId()+"","1");
    }

    /**
     * 查询支付状态
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result=null;
        int x=0;
        while(true){
            //调用查询接口
            Map<String,String> map=weixinPayService.queryPayStatus(out_trade_no);
            if(map==null){
                result=new Result(false,"支付出错");
                break;
            }
            if(map.get("trade_state").equals("success")){//如果成功
                result=new Result(true,"支付成功");
                break;
            }
            try {
                Thread.sleep(3000);//间隔三秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //为了不让循环无休止的运行,我们定义一个循环变量,如果这个变脸高超过了这个值则退出循环,设置时间为5分钟
            x++;
            if(x>=100){
                result=new Result(false,"二维码超时");
                break;
            }
        }
        return result;
    }
}
