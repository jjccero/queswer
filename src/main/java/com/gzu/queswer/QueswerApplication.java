package com.gzu.queswer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@MapperScan("com.gzu.queswer.dao")
@ServletComponentScan
@SpringBootApplication
public class QueswerApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueswerApplication.class, args);
    }

}
