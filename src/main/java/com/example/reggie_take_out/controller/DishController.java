package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.dto.DishDto;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.DishFlavor;
import com.example.reggie_take_out.service.CategoryService;
import com.example.reggie_take_out.service.DishFlavorService;
import com.example.reggie_take_out.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService service;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

   @GetMapping("/page")
   public R<Page> listDish(Long page,Long pageSize,String name){
       return service.listDish(page,pageSize,name);
   }

   @Transactional
   @PostMapping()
   public R<String> addDish(@RequestBody DishDto dishDto){
       service.saveDishAndFlavor(dishDto);
       return R.success("保存成功");
   }

   @GetMapping("/{id}")
   public R<DishDto> getDish(@PathVariable("id") Long id){
       DishDto dish = service.getDish(id);
       return R.success(dish);
   }

   @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
       service.updateDishAndFlavor(dishDto);
       return R.success("保存成功");
   }

//   @GetMapping("/list")
//   public R<List<Dish>> listDish(Long categoryId){
//       LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//       queryWrapper.eq(Dish::getCategoryId,categoryId);
//       // 查询状态为1的，因为1是在售
//       queryWrapper.eq(Dish::getStatus,1);
//       List<Dish> list = service.list(queryWrapper);
//       return R.success(list);
//   }

    @GetMapping("/list")
    public R<List<DishDto>> listDish(Long categoryId){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId);
        // 查询状态为1的，因为1是在售
        queryWrapper.eq(Dish::getStatus,1);
        List<Dish> list = service.list(queryWrapper);
        // 将Dish的信息复制给DishDTO

        // 查询出每一个dish的口味信息
        List<DishDto> collect = list.stream().map(item -> {
            // 查询每一个所对应的口味信息
            DishDto dishDto = new DishDto();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());
            BeanUtils.copyProperties(item,dishDto);
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, item.getId());
            List<DishFlavor> dishFloavor = dishFlavorService.list(wrapper);
            dishDto.setFlavors(dishFloavor);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(collect);
    }


}
