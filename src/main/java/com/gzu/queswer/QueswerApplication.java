package com.gzu.queswer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;


@MapperScan("com.gzu.queswer.dao")
@ServletComponentScan
@SpringBootApplication
@EnableCaching
public class QueswerApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueswerApplication.class, args);
    }

}
