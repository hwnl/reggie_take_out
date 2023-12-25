package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.User;
import com.example.reggie_take_out.service.UserService;
import com.example.reggie_take_out.util.Sample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(HttpServletRequest request, @RequestBody User user) throws Exception {
        // 给该手机发送验证码
        Sample.createClient("","");
        // 随机验证码
        Random random = new Random();
        int codeNum = random.nextInt( 8998)+1001;
        //String code =""+codeNum;
        String code = "1234";
        // 将验证码存储在session中
        //Sample.sendCode("","",user.getPhone(),code);
        request.getSession().setAttribute(user.getPhone(),code);
        return R.success("验证码发送成功");
    }

    @PostMapping("/login")
    public R<User> login(HttpServletRequest request,@RequestBody Map<String,String> map){
        String phone = map.get("phone");
        String code = map.get("code");

        // 判断验证码和手机号是否正确
        String oldCode = (String) request.getSession().getAttribute(phone);
        if(!oldCode.equals(code)){
            return R.error("验证码错误");
        }

        // 判断是否注册过
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User user = userService.getOne(queryWrapper);
        if(user!= null){
            request.getSession().setAttribute("user",user.getId());
            return R.success(user);
        }
        // 注册手机号
        User newUser = new User();
        newUser.setPhone(phone);
        userService.save(newUser);

        // 将数据放在session里面，避免重复登录
        request.getSession().setAttribute("user",newUser.getId());

        return R.success(newUser);
    }



}
