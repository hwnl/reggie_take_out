package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.dto.SetmealDto;
import com.example.reggie_take_out.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    void saveSetmealAndDish(SetmealDto setmealDto);

    SetmealDto findSetmealDto(Long id);

    Page<SetmealDto> findSetmeal(Long page, Long pageSize, String name);

    R<String> deleteSetmealDish(List<Long> ids);
}
