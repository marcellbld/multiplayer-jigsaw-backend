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
public class PuzzleChannelInterceptor implements ChannelInterceptor {
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    public PuzzleChannelInterceptor(@Lazy SimpMessagingTemplate messagingTemplate, RoomService roomService) {
        this.messagingTemplate = messagingTemplate;
        this.roomService = roomService;
    }
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.MESSAGE.equals(accessor.getCommand()) && accessor.getDestination() != null) {
            String[] destinationTokens = accessor.getDestination().split("/");

            if(destinationTokens.length >= 6 &&
                    destinationTokens[2].equals("room") &&
                    destinationTokens[4].equals("puzzle")) {
                String roomId = destinationTokens[3];

                RoomDto room;
                try {
                    room = roomService.getRoomDto(roomId);
                } catch (Exception e) {
                    return null;
                }

                if(room.users().stream().noneMatch(
                        user -> user.username().equals(Objects.requireNonNull(accessor.getUser()).getName()))
                ) {
                    return null;
                }
            }
        }
        return ChannelInterceptor.super.preSend(message, channel);
    }
}
