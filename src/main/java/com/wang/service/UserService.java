package com.wang.service;

import com.wang.pojo.User;

public interface UserService {
    //根据用户名查询用户
    User findByUserNmae(String username);

    //注册
    void register(String username, String password);

    //更新基本信息
    void update(User user);

    //更新头像
    void updateAvatar(String avatarUrl);

    //更新密码
    void updatePwd(String newPwd);

    //添加用户
    void addUser(String username,Integer role);
}
