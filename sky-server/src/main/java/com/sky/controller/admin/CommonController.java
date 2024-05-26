package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */

// 请求处理类
@RestController
@RequestMapping("admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;
    // 接口返回得数据是String类型得
    // file要跟发送参数名一致
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){
        log.info("文件上传:{}", file);

        try {
            // getOriginalFilename()获取原始文件名
            String originalFilename = file.getOriginalFilename();
            // 截取原始文件名的后缀png, Jpg 从最后一个点截取
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 生成随机文件名
            String objectName = UUID.randomUUID().toString() + extension;

            // 文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败:{}", e);
        }

        // 常量
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
