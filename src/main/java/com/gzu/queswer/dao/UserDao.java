package com.gzu.queswer.dao;

import com.gzu.queswer.model.User;
import com.gzu.queswer.model.UserLogin;
import org.springframework.stereotype.Repository;

import javax.websocket.server.PathParam;
import java.util.List;

@Repository
public interface UserDao {

    int insertUser(UserLogin userLogin);

    Integer updateUser(User user);

    User selectUserByUId(Long uId);

    UserLogin selectUserLoginByUsername(String username);

    int saveFollow(@PathParam("uId") Long uId, @PathParam("followerId") Long followerId);

    int deleteFollow(@PathParam("uId") Long uId, @PathParam("followerId") Long followerId);

    List<Long> selectUIdsByFollowerId(Long followerId);

    List<Long> selectFollowerIdsByUId(Long uId);
}
