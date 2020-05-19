package com.gzu.queswer.dao;

import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.vo.PasswordForm;
import com.gzu.queswer.model.vo.UserForm;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao {

    int insertUser(UserLogin userLogin);

    Integer updateUser(UserForm userForm);

    Integer updatePassword(PasswordForm passwordForm);

    String selectPassword(Long userId);

    User selectUser(Long userId);

    UserLogin selectUserLoginByUsername(String username);
}
