package com.qkl.edu_system.controller;

import com.qkl.edu_system.mapper.UserMapper;
import com.qkl.edu_system.pojo.Result;
import com.qkl.edu_system.pojo.User;
import com.qkl.edu_system.service.LoginAndRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class LoginAndRegisterController {

    @Autowired
    private LoginAndRegisterService loginAndRegisterService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        log.info("用户注册：{}", user);
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            return Result.error("用户已存在");
        }
        loginAndRegisterService.register(user);
        return Result.success();
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        log.info("用户登录：{}", user);

        // 1. 用户是否存在
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser == null) {
            return Result.error("用户不存在");
        }

        // 2. 密码是否正确
        if (!existUser.getPassword().equals(user.getPassword())) {
            return Result.error("密码错误");
        }

        // 3. 登录成功（返回用户信息，不包含密码更安全）
        existUser.setPassword(null);
        return Result.success(existUser);
    }
}