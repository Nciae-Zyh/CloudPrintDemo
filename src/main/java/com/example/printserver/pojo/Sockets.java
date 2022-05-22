package com.example.printserver.pojo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
@Data
@Component
public class Sockets {
    HashMap<String, ArrayList<Socket>> sockets=new HashMap<>();
    HashMap<String, Integer> workedSocket = new HashMap<>();
}