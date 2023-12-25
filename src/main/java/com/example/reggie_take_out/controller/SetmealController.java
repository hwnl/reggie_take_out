package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.dto.SetmealDto;
import com.example.reggie_take_out.entity.Setmeal;
import com.example.reggie_take_out.entity.SetmealDish;
import com.example.reggie_take_out.mapper.SetmealDishMapper;
import com.example.reggie_take_out.service.SetmealDishService;
import com.example.reggie_take_out.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @GetMapping("/page")
    public R<Page> page(Long page,Long pageSize,String name){
        Page<SetmealDto> list = setmealService.findSetmeal(page,pageSize,name);
        return R.success(list);
    }

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveSetmealAndDish(setmealDto);
        return R.success("保存成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> getSetmealDto(@PathVariable("id") Long id){
        SetmealDto setmealDto = setmealService.findSetmealDto(id);
        return R.success(setmealDto);
    }

    @PostMapping("/status/{state}")
    public R<String> changeStatus(@PathVariable("state")Integer state, @RequestParam("ids") Long id){
        // 获得套餐
        Setmeal setmeal = setmealService.getById(id);
        setmeal.setStatus(state);
        setmealService.updateById(setmeal);
        // 修改状态
        return R.success("状态修改成功");
    }

    @DeleteMapping
    public R<String> delete(@RequestParam("ids")List<Long> ids){
        return setmealService.deleteSetmealDish(ids);
    }


    @GetMapping("/list")
    public R<List<Setmeal>> list(@RequestParam Map<String,String> map){
        Long categoryId = Long.valueOf(map.get("categoryId"));
        Integer status = Integer.valueOf(map.get("status"));
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId!=null,Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(status!=null,Setmeal::getStatus,status);
        List<Setmeal> setmeals = setmealService.list(queryWrapper);
        return R.success(setmeals);
    }

    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> dish(@PathVariable("id")Long id){
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        return R.success(setmealDishes);
    }
}

// p78