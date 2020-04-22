package com.gzu.queswer.dao;

import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {

    int insertUser(UserLogin userLogin);

    Integer updateUser(User user);

    User selectUser(Long userId);

    UserLogin selectUserLoginByUsername(String username);
}
