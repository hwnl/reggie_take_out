package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.dto.DishDto;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.DishFlavor;
import com.example.reggie_take_out.mapper.CategoryMapper;
import com.example.reggie_take_out.mapper.DishFlavorMapper;
import com.example.reggie_take_out.mapper.DishMapper;
import com.example.reggie_take_out.service.DishFlavorService;
import com.example.reggie_take_out.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Transactional
    @Override
    public R<Page> listDish(Long page, Long pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(name!=null,Dish::getName,name);
        this.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> collect = records.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryMapper.selectById(categoryId);
            String cateName = category.getName();
            if(cateName != null){
                dishDto.setCategoryName(cateName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(collect);
        return R.success(dishDtoPage);
    }

    @Override
    public DishDto getDish(Long id) {
        Dish dish = dishMapper.selectById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectList(queryWrapper);
        dishDto.setFlavors(dishFlavors);
        return dishDto;
    }

    @Override
    public void saveDishAndFlavor(DishDto dishDto) {
        this.save(dishDto);
        List<DishFlavor> list = dishDto.getFlavors();
        List<DishFlavor> collect = list.stream().map(item -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        if(collect != null){
            dishFlavorService.saveBatch(collect);
        }
    }

    @Override
    public void updateDishAndFlavor(DishDto dishDto) {
        // 保存基本dish
        this.updateById(dishDto);
        // 删除dish对应的dishflavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        // 添加新的dishflavor
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> collect = flavors.stream().map(item -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(collect);
    }
}
