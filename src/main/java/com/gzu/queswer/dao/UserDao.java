package com.gzu.queswer.dao;

import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import com.gzu.queswer.model.vo.PasswordForm;
import com.gzu.queswer.model.vo.UserForm;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    void insertUser(UserLogin userLogin);

    int updateUser(UserForm userForm);

    int updateAuthority(User user);

    int deleteQuestion(Long questionId);

    List<Long> queryAdminIds();

    Integer updatePassword(PasswordForm passwordForm);

    String selectPassword(Long userId);

    User selectUser(Long userId);

    UserLogin selectUserLoginByUsername(String username);
}
