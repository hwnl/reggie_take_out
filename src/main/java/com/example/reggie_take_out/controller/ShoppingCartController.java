package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie_take_out.common.R;
import com.example.reggie_take_out.entity.ShoppingCart;
import com.example.reggie_take_out.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService cartService;

    @PostMapping("/add")
    public R<String> addCart(HttpServletRequest request, @RequestBody ShoppingCart shoppingCart){
        // 判断该菜品是否在购物车里面有
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        queryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart shopping = cartService.getOne(queryWrapper);
        if(shopping ==null){
            // 获取用户id
            Long userId = (Long) request.getSession().getAttribute("user");
            shoppingCart.setUserId(userId);
            cartService.save(shoppingCart);
        }else{
            shopping.setNumber(shopping.getNumber()+1);
            cartService.updateById(shopping);
        }
        return R.success("添加成功");
    }

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        List<ShoppingCart> carts = cartService.list();
        return R.success(carts);
    }

    @PostMapping("/sub")
    public R<String> subCart(@RequestBody ShoppingCart shoppingCart){
       LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
       queryWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
       queryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart shop = cartService.getOne(queryWrapper);
        if(shop.getNumber()>1){
            shop.setNumber(shop.getNumber()-1);
            cartService.updateById(shop);
        }else{
            cartService.removeById(shop);
        }

       return R.success("删除成功");
    }

    @DeleteMapping("/clean")
    public R<String> clean(){
        cartService.remove(null);
        return R.success("清空成功");
    }

}
