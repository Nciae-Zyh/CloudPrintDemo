package com.example.printserver.result;

import lombok.Data;

@Data
public class UploadResult {
    private String status;
    private String fileName;
    private Integer orderPage;
}
