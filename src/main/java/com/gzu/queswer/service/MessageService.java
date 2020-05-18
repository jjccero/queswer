package com.gzu.queswer.service;

import com.gzu.queswer.model.Message;

import java.util.List;

public interface MessageService {
    boolean saveMessage(Message message);

    int readMessages(Long dstId);

    List<Message> queryMessages(Long dstId);
}
