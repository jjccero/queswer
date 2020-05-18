package com.gzu.queswer.websocket;

import com.alibaba.fastjson.JSON;
import com.gzu.queswer.model.Message;
import com.gzu.queswer.model.User;
import com.gzu.queswer.service.MessageService;
import com.gzu.queswer.service.UserService;
import com.gzu.queswer.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/chat/{token}")
public class ChatServer {
    private static ConcurrentHashMap<Long, List<ChatServer>> map = new ConcurrentHashMap<>();
    private Session session;
    private Long userId;
    private static UserService userService;
    private static MessageService messageService;

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        this.session = session;
        User user = userService.getUserByToken(token);
        if (user == null) return;
        userId = user.getUserId();
        List<ChatServer> chatServers = map.computeIfAbsent(userId, k -> new Vector<>(1));
        chatServers.add(this);
        try {
            List<Message> messages = messageService.queryMessages(userId);
            messageService.readMessages(userId);
            String dstMessagesString = JSON.toJSONString(messages);
            session.getBasicRemote().sendText(dstMessagesString);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @OnClose
    public synchronized void onClose() {
        if (userId == null) return;
        List<ChatServer> chatServers = map.get(userId);
        chatServers.remove(this);
        if (chatServers.isEmpty())
            map.remove(userId);
    }

    @OnMessage
    public void onMessage(String srcMessageString) {
        //使用json获取，返回参数
        try {
            Message message = JSON.parseObject(srcMessageString, Message.class);
            message.setGmtCreate(DateUtil.getUnixTime());
            message.setSrcId(userId);
            message.setUnread(true);
            Long dstId = message.getDstId();
            if (message.getDstId() != null && !userId.equals(dstId)) {
                List<ChatServer> chatServers = map.get(dstId);
                String dstMessageString = JSON.toJSONString(message);
                session.getBasicRemote().sendText(dstMessageString);
                if (chatServers != null) {
                    message.setUnread(false);
                    for (ChatServer chatServer : chatServers) {
                        chatServer.session.getBasicRemote().sendText(dstMessageString);
                    }
                }
                messageService.saveMessage(message);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void setUserService(UserService userService) {
        ChatServer.userService = userService;
    }

    public static void setMessageService(MessageService messageService) {
        ChatServer.messageService = messageService;
    }
}
