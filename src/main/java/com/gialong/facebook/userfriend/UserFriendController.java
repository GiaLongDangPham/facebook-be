package com.gialong.facebook.userfriend;

import com.gialong.facebook.auth.AuthService;
import com.gialong.facebook.base.PageResponse;
import com.gialong.facebook.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID userId = userService.getUserByUsername(username).getId();
        return ResponseEntity.ok(userFriendService.getFriends(userId, page, size));
    }
}