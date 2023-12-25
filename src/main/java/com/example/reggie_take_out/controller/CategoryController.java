package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @PostMapping()
    public R addCategory(@RequestBody Category category){
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> listCategory(Long page, Long pageSize){
        Page pageinfo = new Page(page,pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageinfo,queryWrapper);
        return R.success(pageinfo);
    }

    @PutMapping()
    public R<String> updateCategory(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    @DeleteMapping()
    public R<String> deleteCategory(@RequestParam("ids") Long id){
        categoryService.remove(id);
        return R.success("删除成功");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        List<Category> categories = categoryService.list(queryWrapper);
        return R.success(categories);
    }

}
