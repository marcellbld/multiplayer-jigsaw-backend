package com.mbld.jigslybackend.config.ws;

import com.mbld.jigslybackend.entities.Role;
import com.mbld.jigslybackend.entities.User;
import com.mbld.jigslybackend.services.JwtService;
import com.mbld.jigslybackend.utils.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        assert accessor != null;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            final var tokenHeader = accessor.getFirstNativeHeader("Authorization");
            if(tokenHeader != null) {
                String token = tokenHeader.substring(7);

                String username = jwtService.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                accessor.setUser(usernamePasswordAuthenticationToken);
            } else {
                final var nameHeader = accessor.getFirstNativeHeader("Player-Name");
                String playerName = (nameHeader == null || nameHeader.isBlank()) ? "Anonymous" : nameHeader;

                String randomNumber = String.format("%4s", Random.getRandomNumber(1,9999)).replace(' ', '0');
                String username = "%s#%s".formatted(playerName, randomNumber);
                UserDetails userDetails = User.builder()
                        .username(username)
                        .role(Role.USER)
                        .build();

                String randomKey = "RANDOMKEY";
                AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken(randomKey,userDetails, List.of(new SimpleGrantedAuthority(Role.USER.name())));
                SecurityContextHolder.getContext().setAuthentication(anonymousAuthenticationToken);

                accessor.setUser(anonymousAuthenticationToken);
            }
        }

        return message;
    }
}
