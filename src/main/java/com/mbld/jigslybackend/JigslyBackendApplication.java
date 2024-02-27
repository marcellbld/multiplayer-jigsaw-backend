package com.mbld.jigslybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.DependsOn;

@SpringBootApplication
@DependsOn("jedisConnectionFactory")
public class JigslyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JigslyBackendApplication.class, args);
    }
}
