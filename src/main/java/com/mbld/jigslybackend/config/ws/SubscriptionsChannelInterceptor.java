package com.mbld.jigslybackend.config.ws;

import com.mbld.jigslybackend.entities.dto.RoomUserDto;
import com.mbld.jigslybackend.entities.wsmessages.room.RoomUserLeftDto;
import com.mbld.jigslybackend.entities.wsmessages.SocketEventType;
import com.mbld.jigslybackend.entities.wsmessages.SocketMessage;
import com.mbld.jigslybackend.services.RoomService;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SubscriptionsChannelInterceptor implements ChannelInterceptor {

    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, Set<String>> userSubscriptions = new ConcurrentHashMap<>();

    public SubscriptionsChannelInterceptor(@Lazy SimpMessagingTemplate messagingTemplate, RoomService roomService) {
        this.messagingTemplate = messagingTemplate;
        this.roomService = roomService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
            handleDisconnect(Objects.requireNonNull(accessor.getUser()).getName());
        }
        else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) && accessor.getDestination() != null) {
            handleSubscribe(Objects.requireNonNull(accessor.getUser()).getName(), accessor.getDestination());
        }
        else if (StompCommand.UNSUBSCRIBE.equals(accessor.getCommand()) && accessor.getFirstNativeHeader("id") != null) {
            String destination = accessor.getFirstNativeHeader("id");
            handleUnsubscribe(Objects.requireNonNull(accessor.getUser()).getName(), destination);
        }

        return ChannelInterceptor.super.preSend(message, channel);
    }

    private void handleDisconnect(String username) {
        userSubscriptions.computeIfPresent(username, (key, subscriptions) -> {
            for (String destination : subscriptions) {
                try {
                    unsubscribe(username, destination);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        });
    }
    private void unsubscribe(String username, String destination) throws Exception {
        if(destination.startsWith("/topic/room/")) {
            String roomId = destination.split("/")[3];
            roomService.removeUserFromRoom(roomId, username);

            messagingTemplate.convertAndSend(destination,
                    SocketMessage.<RoomUserLeftDto>builder()
                            .event(SocketEventType.Room_UserLeft)
                            .body(RoomUserLeftDto.builder()
                                    .user(RoomUserDto.builder()
                                            .username(username)
                                            .colorId(0)
                                            .build())
                                    .build())
                            .build());
        }
    }

    private void handleSubscribe(String username, String destination) {
        userSubscriptions.put(username, userSubscriptions.getOrDefault(username, new HashSet<>()));
        userSubscriptions.get(username).add(destination);
    }
    private void handleUnsubscribe(String username, String destination) {
        userSubscriptions.computeIfPresent(username, (key, subscriptions) -> {
            try {
                unsubscribe(username, destination);
            } catch (Exception e) {
                e.printStackTrace();
            }

            subscriptions.remove(destination);
            if (subscriptions.isEmpty()) {
                return null;
            }
            return subscriptions;
        });
    }
}
