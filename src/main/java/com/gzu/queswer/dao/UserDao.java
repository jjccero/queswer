package com.gzu.queswer.dao;

import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {

    int insertUser(UserLogin userLogin);

    Integer updateUser(User user);

    User selectUserByUId(Long uId);

    UserLogin selectUserLoginByUsername(String username);

    List<Long> selectUIdByFollowerId(Long followerId);

    List<Long> selectFollerUIdsByUId(Long uId);
}
