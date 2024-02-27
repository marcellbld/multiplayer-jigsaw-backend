package com.mbld.jigslybackend.entities.wsmessages;

import lombok.Builder;

@Builder
public record ErrorMessageDto(String message) {
}
