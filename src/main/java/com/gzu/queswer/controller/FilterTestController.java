package com.gzu.queswer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filter")
public class FilterTestController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/add")
    public Integer add(Integer a,Integer b) {
        return a+b;
    }

}
