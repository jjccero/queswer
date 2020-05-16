package com.gzu.queswer.configuration;

import com.gzu.queswer.service.ActivityService;
import com.gzu.queswer.service.UserService;
import com.gzu.queswer.websocket.ChatServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfiguration {
    @Bean
    public ServerEndpointExporter serverEndpointExporter(UserService userService, ActivityService activityService) {
        ChatServer.setUserService(userService);
        return new ServerEndpointExporter();
    }
}
