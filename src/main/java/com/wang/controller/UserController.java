package com.wang.controller;

import com.wang.pojo.Result;
import com.wang.pojo.User;
import com.wang.service.UserService;
import com.wang.utils.JwtUtil;
import com.wang.utils.Md5Util;
import com.wang.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //注册
    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password){

            //查询用户
            User u = userService.findByUserNmae(username);
            if(u == null){
                //没有占用
                //注册用户
                userService.register(username,password);
                return Result.success();
            }else{
                //占用
                return Result.error("用户名已被占用");
            }

    }

    //登录
    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password){
        //根据用户名查询用户
        User loginUser = userService.findByUserNmae(username);

        //判断该用户是否存在
        if(loginUser == null){
            return Result.error("用户名错误");
        }

        //判断密码是否正确 表中password是密文
        if(Md5Util.getMD5String(password).equals(loginUser.getPassword())){
            Map<String,Object> claims = new HashMap<>();
            claims.put("id",loginUser.getId());
            claims.put("username",loginUser.getUsername());
            claims.put("role",loginUser.getRole());
            //颁发令牌
            String token = JwtUtil.genToken(claims);
            //把token存储到redis中
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,1, TimeUnit.HOURS);

            return Result.success(token);
        }

        return Result.error("密码错误");
    }

    //退出登录
    // @PostMapping("/logout")
//    public Result logout(){
//        //清除redis中的令牌数据
//
//        //清空threadLocalUtil中的用户信息
//    }

    //查询用户信息
    @GetMapping("/userInfo")
    public Result<User> userInfo(){
        //根据用户名查询用户
        /*Map<String, Object> map = JwtUtil.parseToken(token);
        String username = (String) map.get("username");*/
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");

        User user = userService.findByUserNmae(username);
        return Result.success(user);
    }

    //更新用户基本信息
    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user){
        userService.update(user);
        return Result.success();
    }

    //更新用户头像
    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    //更新用户密码
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String,String> params,@RequestHeader("Authorization") String token){
        //1.校验参数
        String oldPwd = params.get("oldPwd");
        String newPwd = params.get("newPwd");
        String rePwd = params.get("rePwd");
        if(!StringUtils.hasLength(oldPwd)||!StringUtils.hasLength(newPwd)||!StringUtils.hasLength(rePwd))
            return Result.error("缺少必要的参数");

        //检查原密码是否正确
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User loginUser = userService.findByUserNmae(username);
        if(!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd)))
            return Result.error("原密码填写不正确");

        //校验newPwd和rePwd是否一样
        if(!rePwd.equals(newPwd)){
            return Result.error("两次填写的密码不一致");
        }

        //2.调用service完成密码更新
        userService.updatePwd(newPwd);
        //删除redis中对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }

    @PostMapping("/addUser")
    public Result addUser(@Pattern(regexp = "^\\S{5,16}$")String username, Integer role){
        //校验权限
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userrole = (Integer) map.get("role");
        if(userrole == 0){
            User u = userService.findByUserNmae(username);
            if(u == null){
                //没有占用
                //添加用户
                userService.addUser(username, role);
                return Result.success();
            }else{
                //占用
                return Result.error("用户名已被占用");
            }
        }
        return Result.error("权限不够");
    }
}
