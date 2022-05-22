package com.example.printserver.service;

import com.example.printserver.result.CommonResult;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FileService {

    public CommonResult upload(MultipartFile file, Integer uid);
    public ResponseEntity<FileSystemResource> getFile(File file);
    public CommonResult sendFileToPrint(Integer uid, String address, String filename, Integer printNum, Integer isColor, Integer isDuplex);
    public CommonResult sendFileToPrint(Integer oid);
}
