package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.Employee;
import com.example.reggie_take_out.mapper.EmployeeMapper;
import com.example.reggie_take_out.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService{

    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public R login(Employee employee) {
        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        String bytes = DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据页面提交的用户名username查询数据库
        QueryWrapper<Employee> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", employee.getUsername());
        Employee employee1 = employeeMapper.selectOne(queryWrapper);
        //3、如果没有查询到则返回登录失败结果
        if(employee1 == null){
            return R.error("没有此用户");
        }
        //4、密码比对，如果不一致则返回登录失败结果
        if(!bytes.equals(employee1.getPassword())){
            return R.error("密码错误");
        }
        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(employee1.getStatus() == 0){
            return R.error("此用户已被禁用");
        }
        //6、登录成功，将员工id存入Session并返回登录成功结果
        return R.success(employee1);
    }
}
