package com.gzu.queswer.dao;

import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {

    void insertUser(UserLogin userLogin);

    Integer updateUser(User user);

    User selectUserByUid(Long uId);

    UserLogin selectUserLoginByUsername(String username);
}
