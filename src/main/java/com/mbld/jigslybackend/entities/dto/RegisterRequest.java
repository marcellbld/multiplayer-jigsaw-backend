package com.mbld.jigslybackend.entities.dto;

import lombok.Builder;

@Builder
public record RegisterRequest (String username, String password) {

}
