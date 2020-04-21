package com.gzu.queswer.dao;

import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    int insertUser(UserLogin userLogin);

    Integer updateUser(User user);

    User selectUser(Long userId);

    UserLogin selectUserLoginByUsername(String username);

    int saveFollow(@Param("userId") Long userId, @Param("followerId") Long followerId, @Param("gmtCreate") Long gmtCreate);

    int deleteFollow(@Param("userId") Long userId, @Param("followerId") Long followerId);

    List<Long> selectUserIdsByFollowerId(Long followerId);

    List<Long> selectFollowerIdsByUserId(Long userId);
}
