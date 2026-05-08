package com.qkl.edu_system.service;

import com.qkl.edu_system.pojo.User;

public interface LoginAndRegisterService {
    void register(User user);

    User login(User user);
}