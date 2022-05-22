package com.example.printserver.controller;

import com.example.printserver.pojo.SendOrder;
import com.example.printserver.result.CommonResult;
import com.example.printserver.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.concurrent.ExecutionException;

@RestController
public class FileController {

    FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 上传文件的控制器
     *
     * @param file 从前端传来的文件
     * @param uid  当前用户的uid
     * @return 文件上传后的信息，有文件名，页数
     */
    @PostMapping(value = "/upload", produces = "application/json;charset=UTF-8")
    public CommonResult upload(@RequestParam("file") MultipartFile file, @RequestParam("uid") Integer uid) {
        return fileService.upload(file, uid);
    }

    /**
     * 下载文件的功能
     *
     * @param filePath 文件前缀路径即订单顾客uid
     * @param fileName 文件名
     * @return FileSystemResource用于下载
     */
    @GetMapping("getFile/{filePath}/{fileName}")
    public ResponseEntity<FileSystemResource> getFile(@PathVariable String filePath, @PathVariable String fileName) {
        File file = new File("/usr/local/webData/" + filePath + '/' + fileName);
        return fileService.getFile(file);
    }

    /**
     * 已弃用接口，用于原模式下启动socket传输功能。
     *
     * @param sendOrder 传输的数据
     * @return 订单状态
     * @throws ExecutionException   阻塞时间超时
     * @throws InterruptedException 阻塞方法收到中断请求的时候就会抛出InterruptedException异常
     */
    @PostMapping("sendFileToPrint")
    public CommonResult sendFileToPrint(@RequestBody SendOrder sendOrder) throws ExecutionException, InterruptedException {
        return fileService.sendFileToPrint(sendOrder.getUid(), sendOrder.getAddress(), sendOrder.getFilename(), sendOrder.getPrintNum(), sendOrder.getColor(), sendOrder.getDuplex());
    }

}
