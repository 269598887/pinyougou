package com.pinyougou.manager.contorller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @RequestMapping("/name")
    public Map name(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
       Map map = new HashMap<>();
       map.put("loginName",name);
       return map;
    }
}
