package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.dto.DishDto;
import com.example.reggie_take_out.entity.Dish;

public interface DishService extends IService<Dish> {
    R<Page> listDish(Long page, Long pageSize, String name);

    DishDto getDish(Long id);

    void saveDishAndFlavor(DishDto dishDto);

    void updateDishAndFlavor(DishDto dishDto);
}
