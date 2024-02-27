package com.mbld.jigslybackend.entities.wsmessages;

import lombok.Builder;

@Builder
public record SocketMessage<T>(SocketEventType event, T body) {
}
