package com.gzu.queswer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@ServletComponentScan
@SpringBootApplication
public class QueswerApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueswerApplication.class, args);
    }

}
