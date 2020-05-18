package com.gzu.queswer.service.impl;

import com.gzu.queswer.dao.MessageDao;
import com.gzu.queswer.model.Message;
import com.gzu.queswer.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageDao messageDao;

    @Override
    public boolean saveMessage(Message message) {
        return messageDao.insertMessage(message) == 1;
    }

    @Override
    public int readMessages(Long dstId) {
        return messageDao.updateUnreadMessages(dstId);
    }

    @Override
    public List<Message> queryMessages(Long dstId) {
        return messageDao.selectUnreadMessages(dstId);
    }
}
