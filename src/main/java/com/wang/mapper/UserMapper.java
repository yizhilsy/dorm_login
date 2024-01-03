package com.wang.mapper;

import com.wang.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    //根据用户名查询用户
    @Select("select * from user where username = #{username}")
    User findByUserName(String username);

    //添加
    @Insert("insert into user(username,password,create_time,update_time,role)" +
            "values(#{username},#{password},now(),now(),#{role})")
    void add(String username, String password, Integer role);

    //更新
    @Update("update user set nickname=#{nickname},email=#{email},update_time=#{updateTime} where id=#{id}")
    void update(User user);

    //更新头像
    @Update("update user set user_pic = #{avatarUrl},update_time=now() where id = #{id}")
    void updateAvatar(String avatarUrl,Integer id);

    //更新头像
    @Update("update user set password = #{md5String},update_time=now() where id = #{id}")
    void updatePwd(String md5String, Integer id);
}

