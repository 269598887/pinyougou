package com.pinyougou.cart.contorller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.messger.Result;
import com.pinyougou.pojogroup.Cart;
import com.pinyougou.utlis.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 6000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    /**
     * 购物车列表
     *
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录人:" + username);
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (cartListString == null || cartListString.equals("")) {
            cartListString = "[]";
        }
        List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
        if (username.equals("anonymousUser")) {//如果未登录
            return cartList_cookie;

        } else {
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
            if (cartList_cookie.size() > 0) {//如果本地存在购物车
                //合并购物车
                cartList_redis = cartService.mergeCartList(cartList_redis, cartList_cookie);
                //清除本地cookie的数据
                CookieUtil.deleteCookie(request, response, "cartList");
                //将合并后的数据存入redis
                cartService.saveCartListToRedis(username, cartList_redis);
            }
            return cartList_redis;
        }
    }

    /**
     * 添加商品到购物车cookie
     * @param itemId
     * @param num
     * @return
     *
     * springMVC的版本在4.2或以上版本,可以使用注解实现跨域
     * 在方法上添加注解@CrossOrigin即可
     */
   /* @CrossOrigin(origins = "http:localhost:9105",allowCredentials = "true")*/
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,Integer num){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");//可以访问的域(此方法不需要操作Cookie)
        response.setHeader("Access-Control-Allow-Credentials", "true");//如果操作cookie. 必须加上这句话
       try {
            List<Cart> cartList =findCartList();//获取购物车列表
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            CookieUtil.setCookie(request, response, "cartList",
                    JSON.toJSONString(cartList),3600*24,"UTF-8");
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
}

