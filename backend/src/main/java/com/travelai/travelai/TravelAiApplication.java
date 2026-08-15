package com.travelai.travelai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.travelai.travelai.mapper")
public class TravelAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelAiApplication.class, args);
    }

}
