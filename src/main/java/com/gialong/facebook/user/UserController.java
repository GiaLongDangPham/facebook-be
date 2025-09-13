package com.gialong.facebook.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        UserResponse userResponse = userMapper.toUserResponse(userService.getUserByUsername(username));
        return ResponseEntity.ok().body(userResponse);
    }

    @MessageMapping("/user/connect") // Receives message from clients sending to /app/user/connect
    @SendTo("/topic/active") // Send the response to all clients subscribe to /topic/active
    public List<UserResponse> connect(@Payload UserResponse userResponse) {
        userService.setUserOnline(userResponse);
        return userService.getOnlineUsers();
    }

    @MessageMapping("/user/disconnect")
    @SendTo("/topic/active")
    public List<UserResponse> disconnect(@Payload UserResponse userResponse) {
        userService.setUserOffline(userResponse.getId());
        return userService.getOnlineUsers();
    }

    @GetMapping("/online")
    public ResponseEntity<List<UserResponse>> getOnlineUsers() {
        return ResponseEntity.ok().body(userService.getOnlineUsers());
    }
}
