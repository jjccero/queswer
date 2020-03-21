package com.gzu.queswer.dao;

import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    void insertUser(UserLogin userLogin);

    Integer updateUser(User user);

    Integer insertSupport(@Param("uid") Long uid, @Param("support_uid") Long support_uid);

    List selectSupportsByUid(@Param("uid") Long uid);

    List selectSupportersByUid(@Param("support_uid") Long support_uid);

    User selectUserByUid(@Param("uid") Long uid);

    UserLogin selectUserLoginByUsername(String username);
}
