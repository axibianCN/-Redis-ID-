package com.axi.redissnowflake.Controller;

import com.axi.redissnowflake.Service.CreateId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.SocketException;

@RestController

public class OrderController {
    @Autowired
    public CreateId createId;
    @GetMapping("/createOrderNo")
    public String createOrderNo() throws SocketException {
        String id = createId.createId();
        return id;
    }
}
