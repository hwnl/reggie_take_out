package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.Employee;
import com.example.reggie_take_out.service.EmployeeService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R login(HttpServletRequest httpRequest, @RequestBody Employee employee){
       R<Employee> r =  employeeService.login(employee);
       httpRequest.getSession().setAttribute("employee",r.getData().getId());
       return R.success(employee);
    }

    @PostMapping("/logout")
    public R logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @PostMapping
    public R<String> addEmployee(HttpServletRequest request,@RequestBody Employee employee){
        String bytes = DigestUtils.md5DigestAsHex("12345".getBytes());
        employee.setPassword(bytes);
        employeeService.save(employee);
        return R.success("新增成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
//        构造分页构造器
        Page pageInfo = new Page(page,pageSize);
//        设定判定条件
        LambdaQueryWrapper<Employee> queryChainWrapper = new LambdaQueryWrapper<>();
        queryChainWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
//        设定排序
        queryChainWrapper.orderByAsc(Employee::getUpdateTime);
//        将条件查询结果放到pageInfo里面
        employeeService.page(pageInfo,queryChainWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> updateStatus(HttpServletRequest request,@RequestBody Employee employee){
//         employee.setUpdateTime(LocalDateTime.now());
//         employee.setUpdateUser((Long)request.getSession().getAttribute("employee"));
         employeeService.updateById(employee);
         return R.success("更新状态成功");
    }

    @GetMapping("/{id}")
    public R<Employee> findEmployee(@PathVariable("id") Long id){
        Employee employeeInfo = employeeService.getById(id);
        return R.success(employeeInfo);
    }

    // p40

}
