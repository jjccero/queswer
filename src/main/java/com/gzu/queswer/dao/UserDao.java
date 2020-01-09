package com.gzu.queswer.dao;

import com.gzu.queswer.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer signup(String username, String password, Date date) {
        String sql = "INSERT INTO t_user VALUES(?,?,?)";
        return jdbcTemplate.update(sql, username, password, date);
    }

    public Integer login(String username, String password) {
        String sql = "SELECT COUNT(*) FROM t_user WHERE username=? AND password=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{username, password}, Integer.class);
    }

    public String getPassword(String username) {
        String sql = "SELECT password FROM t_user WHERE username=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{username}, String.class);
    }
}
