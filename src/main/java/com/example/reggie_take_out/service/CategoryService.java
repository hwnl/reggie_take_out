package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.entity.Category;
import org.apache.ibatis.annotations.Mapper;

public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
