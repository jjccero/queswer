package com.gzu.queswer.dao;

import com.gzu.queswer.model.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageDao {
    int insertMessage(Message message);

    int updateUnreadMessages(Long dstId);

    List<Message> selectUnreadMessages(Long dstId);
}
