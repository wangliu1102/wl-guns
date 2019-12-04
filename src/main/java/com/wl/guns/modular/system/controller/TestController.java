package com.wl.guns.modular.system.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.wl.guns.modular.system.model.User;
import com.wl.guns.modular.system.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author 王柳
 * @date 2019/12/4 20:05
 */
@Controller
@RequestMapping("/test")
public class TestController {

    private static String PREFIX = "/system/test/";

    @Autowired
    private IUserService userService;

    /**
     * 测试页面入口
     *
     * @return
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "index.html";
    }

    /**
     * chosen.jquery.js测试页面
     *
     * @return
     */
    @RequestMapping("/chosen_test")
    public String chosen_test() {
        return PREFIX + "chosen_test.html";
    }

    /**
     * bootstrap_select测试页面
     *
     * @return
     */
    @RequestMapping("/bootstrap_select")
    public String bootstrap_select() {
        return PREFIX + "bootstrap_select.html";
    }

    /**
     * bootstrap_select测试：根据名称获取用户列表
     * @param username
     * @return
     */
    @RequestMapping("/getUserByName")
    @ResponseBody
    public Object getUserByName(@RequestParam(value = "username") String username) {
        List<User> userList = userService.selectList(new EntityWrapper<User>().like("name", username));
        return userList;
    }

}
