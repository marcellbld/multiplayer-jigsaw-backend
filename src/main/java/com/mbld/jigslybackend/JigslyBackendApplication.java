package com.mbld.jigslybackend;

import com.mbld.jigslybackend.entities.dto.RoomDto;
import com.mbld.jigslybackend.entities.wsmessages.lobby.LobbyCreateRoomDto;
import com.mbld.jigslybackend.services.PuzzleService;
import com.mbld.jigslybackend.services.RoomService;
import com.mbld.jigslybackend.services.ws.LobbyWSService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@SpringBootApplication
@DependsOn("jedisConnectionFactory")
@RequiredArgsConstructor
public class JigslyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JigslyBackendApplication.class, args);
    }

    private final RoomService roomService;
    private final ResourceLoader resourceLoader;
    private final LobbyWSService lobbyWSService;
    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            createRoom(50, 3, "default001.jpg");
            createRoom(100, 2, "default002.jpg");
            createRoom(50, 4, "default003.jpg");
            createRoom(500, 1, "default004.jpg");
            createRoom(75, 3, "default005.jpg");
            createRoom(250, 5, "default006.jpg");
            createRoom(125, 3, "default007.jpg");
        };
    }

    private void createRoom(int pieces, int userCapacity, String fileName) throws Exception {
        Resource resource = resourceLoader.getResource("classpath:static/default_images/"+fileName);

        String prefix = "data:image/jpeg;base64,";
        RoomDto roomDto = roomService.createRoom(new LobbyCreateRoomDto(pieces, userCapacity), prefix + Base64.getEncoder().encodeToString(resource.getContentAsByteArray()));

        lobbyWSService.notifyLobby_LobbyRoomCreated(roomDto);
    }
}
