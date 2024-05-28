package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

// 设置别名，不然和客户端查询起冲入
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    private static final String KEY = "SHOP STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /*
     * 获取店铺的营收状态
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺的营业状态")
    public Result<Integer> getStatus(){
        // 常量
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("获取到店铺的额营业状态为:{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }


}
