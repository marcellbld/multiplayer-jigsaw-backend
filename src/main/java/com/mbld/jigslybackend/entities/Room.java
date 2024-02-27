package com.mbld.jigslybackend.entities;

import com.mbld.jigslybackend.entities.dto.PublicUserDto;
import com.mbld.jigslybackend.entities.dto.RoomUserDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RedisHash("rooms")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    private String id;

    @NotNull
    @Size(min = 1, max = 5)
    private Integer userCapacity;

    private List<RoomUserDto> users = new CopyOnWriteArrayList<>();

    @Reference
    private Puzzle puzzle;

    public RoomUserDto joinUser(PublicUserDto user) throws Exception {
        if(users.stream().anyMatch(u -> u.username().equals(user.username())) || isFull())
            throw new Exception("Could not join to room.");

        int colorId = -1;
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            if(users.stream().noneMatch(u -> u.colorId().equals(finalI))) {
                colorId = finalI;
                break;
            }
        }

        if(colorId == -1) throw new Exception("Could not assign a color for user: " + user.username());

        RoomUserDto userDto = RoomUserDto.builder()
                .username(user.username())
                .colorId(colorId)
                .build();

        users.add(userDto);

        return userDto;
    }

    public void detachUser(String username) {
        users.removeIf(u -> u.username().equals(username));
    }

    public boolean isFull(){
        return this.users.size() >= this.userCapacity;
    }
    public boolean isEmpty(){
        return this.users.isEmpty();
    }

}
