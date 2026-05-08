package com.qkl.edu_system.service.serviceImpl;

import com.qkl.edu_system.mapper.UserMapper;
import com.qkl.edu_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Integer> getDistinctClazzIds() {
        return userMapper.selectDistinctClazzIds();
    }
}