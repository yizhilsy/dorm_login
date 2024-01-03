package com.wang.controller;

import com.wang.pojo.Result;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class FileUploadController {


    public Result<String> upload(MultipartFile file) throws IOException {
        //把文件内容存储到本地磁盘
        String originalFilename = file.getOriginalFilename();
        file.transferTo(new File("D:\\系统文件夹\\桌面\\files\\"+originalFilename));
        return Result.success("url访问地址...");
    }
}
