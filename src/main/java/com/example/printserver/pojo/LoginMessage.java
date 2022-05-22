package com.example.printserver.pojo;

import lombok.Data;

@Data
public class LoginMessage {
    private String phoneNumber;
    private String password;
}
