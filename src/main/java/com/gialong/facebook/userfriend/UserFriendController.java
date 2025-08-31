package com.gialong.facebook.userfriend;

import com.gialong.facebook.auth.AuthService;
import com.gialong.facebook.base.PageResponse;
import com.gialong.facebook.user.User;
import com.gialong.facebook.user.UserResponse;
import com.gialong.facebook.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/friends")
@RequiredArgsConstructor
public class UserFriendController {

    private final UserFriendService userFriendService;
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/request")
    public ResponseEntity<UserFriendResponse> sendRequest(@RequestParam UUID addresseeId) {
        UUID currentUserId = authService.getMyInfo();
        return ResponseEntity.ok(userFriendService.sendFriendRequest(currentUserId, addresseeId));
    }

    @PostMapping("/accept")
    public ResponseEntity<UserFriendResponse> accept(@RequestParam UUID requesterId) {
        UUID currentUserId = authService.getMyInfo();
        return ResponseEntity.ok(userFriendService.acceptFriendRequest(currentUserId, requesterId));
    }

    @DeleteMapping("/cancel-request")
    public void cancelRequest(@RequestParam UUID addresseeId) {
        UUID currentUserId = authService.getMyInfo();
        userFriendService.cancelRequest(addresseeId, currentUserId);
    }

    @DeleteMapping("/reject")
    public void reject(@RequestParam UUID requesterId) {
        UUID currentUserId = authService.getMyInfo();
        userFriendService.rejectFriendRequest(requesterId, currentUserId);
    }

    @DeleteMapping("/unfriend")
    public void unfriend(@RequestParam UUID otherUserId) {
        UUID currentUserId = authService.getMyInfo();
        userFriendService.unfriend(otherUserId, currentUserId);
    }

    @GetMapping("/{username}")
    public ResponseEntity<PageResponse<UserFriendResponse>> listFriends(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchKeyword
    ) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(userFriendService.getFriends(user, page, size, searchKeyword));
    }

    @GetMapping("/{userId}/requests")
    public ResponseEntity<PageResponse<UserFriendResponse>> getFriendRequests(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userFriendService.getFriendRequests(userId, page, size));
    }

    @GetMapping("/{userId}/suggests")
    public ResponseEntity<PageResponse<UserFriendResponse>> getFriendSuggests(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userFriendService.getFriendSuggests(userId, page, size));
    }

    @GetMapping("/{currentUsername}/status")
    public ResponseEntity<Map<String, String>> getFriendStatus(@PathVariable String currentUsername) {
        UUID currentUserId = authService.getMyInfo();
        User user = userService.getUserByUsername(currentUsername);
        String status = userFriendService.getFriendStatus(currentUserId, user);
        return ResponseEntity.ok(Map.of("status", status));
    }

    @GetMapping("/{currentUsername}/mutual-friends")
    public ResponseEntity<List<UserResponse>> getMutualFriends(@PathVariable String currentUsername) {
        UUID currentUserId = authService.getMyInfo();
        UUID targetId = userService.getUserByUsername(currentUsername).getId();
        return ResponseEntity.ok(userFriendService.getMutualFriends(currentUserId, targetId));
    }
}