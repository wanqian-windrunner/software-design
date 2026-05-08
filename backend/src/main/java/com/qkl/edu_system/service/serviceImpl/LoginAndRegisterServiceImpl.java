package com.qkl.edu_system.service.serviceImpl;

import com.qkl.edu_system.mapper.UserMapper;
import com.qkl.edu_system.pojo.User;
import com.qkl.edu_system.service.LoginAndRegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginAndRegisterServiceImpl implements LoginAndRegisterService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void register(User user) {
        if (user.getIdentity() == 0) {
            // 学生：班级可为空
            user.setClazzId(0);
        } else {
            // 老师：必须有班级
            if (user.getClazzId() == null) {
                throw new RuntimeException("教师必须选择班级");
            }
        }
        userMapper.insertUser(user);
    }

    @Override
    public User login(User user) {
        User dbUser = userMapper.selectByUsername(user.getUsername());

        if (dbUser == null) {
            throw new RuntimeException("用户名不存在");
        }

        if (!dbUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        return dbUser;
    }

}