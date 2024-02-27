package com.mbld.jigslybackend.config.ws;

import com.mbld.jigslybackend.entities.dto.RoomDto;
import com.mbld.jigslybackend.entities.wsmessages.ErrorMessageDto;
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

import java.util.Objects;

@Component
public class RoomChannelInterceptor implements ChannelInterceptor {
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;

    public RoomChannelInterceptor(@Lazy SimpMessagingTemplate messagingTemplate, RoomService roomService) {
        this.messagingTemplate = messagingTemplate;
        this.roomService = roomService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()) && accessor.getDestination() != null) {
            String[] destinationTokens = accessor.getDestination().split("/");

            if(destinationTokens.length >= 4 && destinationTokens[2].equals("room")) {
                String roomId = destinationTokens[3];

                RoomDto room;
                try {
                    room = roomService.getRoomDto(roomId);
                } catch (Exception e) {
                    sendFailedToJoinMessageToUser(accessor.getUser().getName(), "Room doesn't exist.");
                    return null;
                }

                if(room.users().stream().anyMatch(
                        user -> user.username().equals(Objects.requireNonNull(accessor.getUser()).getName()))
                ) {
                    sendFailedToJoinMessageToUser(accessor.getUser().getName(), "Already joined to this room.");
                    return null;
                }

                if(room.users().size() >= room.userCapacity()) {
                    sendFailedToJoinMessageToUser(accessor.getUser().getName(), "Room is already filled.");
                    return null;
                }
            }
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }

    private void sendFailedToJoinMessageToUser(String user, String message) {
        messagingTemplate.convertAndSendToUser(user, "/topic/lobby",
                SocketMessage.builder()
                        .event(SocketEventType.UserLobby_FailedToJoinToRoom)
                        .body(ErrorMessageDto.builder()
                                .message("Failed to join: "+ message)
                                .build())
                        .build());
    }
}
