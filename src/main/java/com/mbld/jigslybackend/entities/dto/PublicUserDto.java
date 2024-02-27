package com.mbld.jigslybackend.entities.dto;

import com.mbld.jigslybackend.entities.Role;
import lombok.Builder;

@Builder
public record PublicUserDto (Long id, String username, Role role){
}
