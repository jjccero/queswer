package com.gzu.queswer.dao;

import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    void insertUser(User user);

    User selectUserByUsername(@Param("username") String username);

    Integer updateUser(User user);

    Integer insertSupport(@Param("uid") Long uid, Long support_uid);

    List selectSupportsByUid(@Param("uid") Long uid);

    List selectSupportersByUid(@Param("support_uid") Long support_uid);

    UserInfo selectUserInfoByUid(@Param("uid") Long uid);
}
