package com.mbld.jigslybackend.entities.dto;

import lombok.Builder;

@Builder
public record LoginRequest (String username, String password) {
}
