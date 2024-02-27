package com.mbld.jigslybackend.entities.dto;

import lombok.Builder;

@Builder
public record AuthResponse (String token) {
}
