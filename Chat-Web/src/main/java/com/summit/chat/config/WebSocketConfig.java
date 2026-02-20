package com.summit.chat.config;


import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketConfig {
    @Bean
    public SocketIOServer configOfSocket() {
        com.corundumstudio.socketio.Configuration configuration = new com.corundumstudio.socketio.Configuration();
        configuration.setTransports(Transport.WEBSOCKET, Transport.POLLING);

        configuration.setHostname("0.0.0.0");
        configuration.setPingTimeout(100000);
        configuration.setPingInterval(10000);
        configuration.setOrigin("*");
        configuration.setPort(9090);
        configuration.setContext("/chat-io");
        configuration.setAllowCustomRequests(true);
        return new SocketIOServer(configuration);
    }

}