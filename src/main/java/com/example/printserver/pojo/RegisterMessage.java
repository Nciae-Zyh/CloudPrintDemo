package com.example.printserver.pojo;

import lombok.Data;

@Data
public class RegisterMessage {
    private String phoneNumber;
    private String name;
    private String password;
    private Boolean isColor;
    private Boolean isDuplex;
}
