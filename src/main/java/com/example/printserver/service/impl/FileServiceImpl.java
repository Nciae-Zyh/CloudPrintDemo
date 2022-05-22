package com.example.printserver.service.impl;

import com.example.printserver.result.CommonResult;
import com.example.printserver.result.UploadResult;
import com.example.printserver.service.FileService;
import com.example.printserver.socket.SendFileServe;
import com.example.printserver.util.CommandUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service("fileService")
@Slf4j
public class FileServiceImpl implements FileService {
    UploadResult uploadResult;

    private static final String api = "/usr/local/webData";

    private static Integer min(Integer[] array) {
        int min = -1;
        int i = 0;
        for (; i < array.length; i++) {
            if (i >= 0) {
                min = array[i];
                break;
            }
        }
        for (; i < array.length; i++) {
            if (array[i] < min && array[i] > 0) {
                min = array[i];
            }
        }
        return min;
    }

    private final ArrayList<String> fileTypeList = new ArrayList<String>(Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"));

    /**
     * 上传文件
     * @param file  从前端传输过来的MultipartFile格式的文件
     * @param uid   用户的id，用于分文件夹存储文件
     * @return
     */
    public CommonResult upload(MultipartFile file, Integer uid) {
        String prePath = api + '/' + uid.toString() + '/';
        if (file.isEmpty()) {
            return CommonResult.failed("上传失败，请重新选择文件");
        }
        String filename = file.getOriginalFilename();
        assert filename != null;
        String lastName = filename.substring(1 + filename.lastIndexOf('.'));
        if (!fileTypeList.contains(lastName)) {
            return CommonResult.failed("上传文件类型错误！");
        }
        Integer[] ku = {filename.lastIndexOf("("),
                filename.lastIndexOf(" "),
                filename.lastIndexOf(" "),
                filename.lastIndexOf(")"),
                filename.lastIndexOf("("),
                filename.lastIndexOf("（"),
                filename.lastIndexOf("("),
                filename.lastIndexOf("）")};
        Integer min = min(ku);
        String filepath;
        Integer last = filename.lastIndexOf('.');
        if (min != -1) {
            if (min == 0) {
                filename = uid + filename.substring(last);
            } else {
                filename = filename.substring(0, min) + filename.substring(last);
            }
        }
        filename = filename.replaceAll(" ", "");    //清除文件名中的空格，防止因系统差异出现bug
        filepath = prePath;
        File reDest = new File(filepath);
        if (!reDest.exists()) {
            reDest.mkdirs();
        }
        filepath += '/';
        File dest = new File(filepath + filename);
        String url = "libreoffice --invisible --convert-to pdf:writer_pdf_Export %s%s --outdir %s";         //利用libreoffice将word转为pdf方便统计页数
        try {
            file.transferTo(dest);
            uploadResult = new UploadResult();
            uploadResult.setStatus("上传成功");
            uploadResult.setFileName(filename);
            url = String.format(url, prePath, filename, prePath);
            String result = CommandUtil.run(url);
            File pdfFile = new File(filepath + filename.substring(0, filename.lastIndexOf('.')) + ".pdf");
            PDDocument pdDocument = PDDocument.load(pdfFile);
            Integer integer = pdDocument.getNumberOfPages();
            pdDocument.close();
            uploadResult.setOrderPage(integer);
            return CommonResult.success(uploadResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CommonResult.failed("上传失败，请重新选择文件");
    }

    public ResponseEntity<FileSystemResource> getFile(File file) {
        if (file.exists()) {
            return export(file);
        } else {
            return null;
        }
    }

    public CommonResult sendFileToPrint(Integer uid, String address, String filename, Integer printNum, Integer isColor, Integer isDuplex) {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Future future = pool.submit(new SendFileServe(uid, address, api + '/' + uid.toString() + '/' + filename, printNum, isColor, isDuplex));
        pool.shutdown();
        try {
            CommonResult result = (CommonResult) future.get(15000, TimeUnit.MILLISECONDS);
            log.info(filename + "打印成功！");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failed();
        }
    }

    public CommonResult sendFileToPrint(Integer oid) {
        return null;
    }

    private ResponseEntity<FileSystemResource> export(File file) {
        if (file == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType("application/octet-stream")).body(new FileSystemResource(file));
    }
}
