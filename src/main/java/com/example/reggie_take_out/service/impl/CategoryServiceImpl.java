package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.common.CustomException;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.Setmeal;
import com.example.reggie_take_out.mapper.CategoryMapper;
import com.example.reggie_take_out.mapper.DishMapper;
import com.example.reggie_take_out.mapper.SetmealMapper;
import com.example.reggie_take_out.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void remove(Long id) {
        // 判断菜品是否有关联
        LambdaQueryWrapper<Dish> disQuery = new LambdaQueryWrapper();
        disQuery.eq(Dish::getCategoryId,id);
        Integer dishNum = dishMapper.selectCount(disQuery);
        if(dishNum > 0){
            // 不能删除
            throw new CustomException("分类下有菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealQuery = new LambdaQueryWrapper<>();
        setmealQuery.eq(Setmeal::getCategoryId,id);
        Integer setmealCount = setmealMapper.selectCount(setmealQuery);
        if(setmealCount > 0){
            // 不能删除
            throw new CustomException("菜品不能删除");
        }
        baseMapper.deleteById(id);
    }
}
