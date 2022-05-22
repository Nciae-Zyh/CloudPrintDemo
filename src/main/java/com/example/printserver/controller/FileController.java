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
    @Autowired
    FileService fileService;

    @PostMapping(value = "/upload", produces = "application/json;charset=UTF-8")
    public CommonResult upload(@RequestParam("file") MultipartFile file, @RequestParam("uid") Integer uid) {
        return fileService.upload(file, uid);
    }

    @GetMapping("getFile/{filePath}/{fileName}")
    public ResponseEntity<FileSystemResource> getFile(@PathVariable String filePath, @PathVariable String fileName) {
        File file = new File("/usr/local/webData/" + filePath + '/' + fileName);
        return fileService.getFile(file);
    }
    @PostMapping("sendFileToPrint")
    public CommonResult sendFileToPrint(@RequestBody SendOrder sendOrder) throws ExecutionException, InterruptedException {
        return fileService.sendFileToPrint(sendOrder.getUid(), sendOrder.getAddress(), sendOrder.getFilename(), sendOrder.getPrintNum(), sendOrder.getColor(), sendOrder.getDuplex());
    }

}
