package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.dto.SetmealDto;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Setmeal;
import com.example.reggie_take_out.entity.SetmealDish;
import com.example.reggie_take_out.mapper.SetmealMapper;
import com.example.reggie_take_out.service.CategoryService;
import com.example.reggie_take_out.service.SetmealDishService;
import com.example.reggie_take_out.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    @Override
    public void saveSetmealAndDish(SetmealDto setmealDto) {
        // 保存套餐
        this.save(setmealDto);
        // 保存套餐的菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> collect = setmealDishes.stream().map(item -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(collect);
    }

    @Override
    public SetmealDto findSetmealDto(Long id) {
        // 查询出套餐信息
        SetmealDto setmealDto = new SetmealDto();
        Setmeal setemeal = this.getById(id);
        BeanUtils.copyProperties(setemeal,setmealDto);
        // 查询出套餐所对应的菜
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getDishId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    @Override
    public Page<SetmealDto> findSetmeal(Long page, Long pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!= null,Setmeal::getName,name);
        Page<Setmeal> page1 = this.page(pageInfo,queryWrapper);
        Page<SetmealDto> page2 = new Page<>();
        BeanUtils.copyProperties(page1,page2);
        List<Setmeal> records = page1.getRecords();
        List<SetmealDto> collect = records.stream().map(item -> {
            SetmealDto setmealDto = new SetmealDto();
            Category byId = categoryService.getById(item.getCategoryId());
            BeanUtils.copyProperties(item,setmealDto);
            setmealDto.setCategoryName(byId.getName());
            return setmealDto;
        }).collect(Collectors.toList());
        page2.setRecords(collect);
        return page2;
    }

    @Transactional
    @Override
    public R<String> deleteSetmealDish(List<Long> ids) {
        // 查询状态
        LambdaQueryWrapper<Setmeal> statuQueryWrapper = new LambdaQueryWrapper();
        statuQueryWrapper.in(Setmeal::getId,ids);
        statuQueryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(statuQueryWrapper);
        if(count > 0){
            return R.error("该商品未停售，无法删除");
        }

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SetmealDish::getDishId, ids);
        setmealDishService.remove(queryWrapper);
        this.removeByIds(ids);
        return R.success("删除成功");
    }
}
