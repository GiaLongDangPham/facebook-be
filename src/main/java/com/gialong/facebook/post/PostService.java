package com.gialong.facebook.post;

import com.gialong.facebook.auth.AuthService;
import com.gialong.facebook.base.PageResponse;
import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import com.gialong.facebook.postcomment.PostCommentService;
import com.gialong.facebook.postmedia.PostMedia;
import com.gialong.facebook.postmedia.PostMediaResponse;
import com.gialong.facebook.user.User;
import com.gialong.facebook.user.UserMapper;
import com.gialong.facebook.user.UserRepository;
import com.gialong.facebook.userfriend.UserFriendRepository;
import com.gialong.facebook.userprofile.UserProfile;
import com.gialong.facebook.userprofile.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserProfileRepository userProfileRepository;
    private final AuthService authService;
    private final PostCommentService postCommentService;
    private final UserFriendRepository userFriendRepository;

    @Transactional
    public PostResponse createPost(UUID userId, PostRequest request) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Post post = Post.builder()
                .author(author)
                .content(request.getContent())
                .privacy(PostPrivacy.valueOf(request.getPrivacy()))
                .commentLocked(request.getCommentLocked())
                .build();

        if (request.getMediaList() != null && !request.getMediaList().isEmpty()) {
            List<PostMedia> medias = request.getMediaList().stream()
                    .map(m -> PostMedia.builder()
                            .post(post)
                            .url(m.getUrl())
                            .mediaType(m.getMediaType())
                            .width(m.getWidth())
                            .height(m.getHeight())
                            .durationSec(m.getDurationSec())
                            .position(m.getPosition())
                            .thumbnailUrl(m.getThumbnailUrl())
                            .build())
                    .toList();
            post.setMediaList(medias);
        }

        Post saved = postRepository.save(post);
        return mapToResponse(saved);
    }

    public PageResponse<PostResponse> getPostsByUser(String currentUsername, int page, int size) {
        UUID currentUserId = authService.getMyInfo();
        UserProfile profile = userProfileRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts;

        if (currentUserId.equals(profile.getUserId())) {
            // Chính chủ -> lấy tất cả post
            posts = postRepository.findByAuthorId(profile.getUserId(), pageable);
        } else {
            // Người khác -> bỏ post ONLY_ME
            posts = postRepository.findByAuthorIdAndPrivacyNot(profile.getUserId(), PostPrivacy.ONLY_ME, pageable);
        }

        List<PostResponse> content = posts.stream()
                .map(this::mapToResponse)
                .toList();

        return PageResponse.<PostResponse>builder()
                .content(content)
                .page(posts.getNumber())
                .size(posts.getSize())
                .totalElements(posts.getTotalElements())
                .totalPages(posts.getTotalPages())
                .last(posts.isLast())
                .build();
    }

    public PostResponse getPostById(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        return mapToResponse(post);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostResponse> getAllPosts(UUID currentUserId, int page, int size) {
        // Lấy danh sách bạn bè đã accepted
        List<UUID> friendIds = new ArrayList<>(userFriendRepository.findAllFriends(currentUserId));

        // Thêm chính mình vào danh sách
        friendIds.add(currentUserId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // Lấy post của mình + bạn bè (không phải ONLY_ME)
        Page<Post> posts = postRepository.findByAuthorIdInAndPrivacyNot(friendIds, PostPrivacy.ONLY_ME, pageable);
//        Page<Post> posts = postRepository.findAllByPrivacyNot(PostPrivacy.ONLY_ME, pageable);

        List<PostResponse> content = posts.stream()
                .map(this::mapToResponse)
                .toList();
        return PageResponse.<PostResponse>builder()
                .content(content)
                .page(posts.getNumber())
                .size(posts.getSize())
                .totalElements(posts.getTotalElements())
                .totalPages(posts.getTotalPages())
                .last(posts.isLast())
                .build();
    }

    public void deletePost(UUID id) {
        if (!postRepository.existsById(id)) {
            throw new AppException(ErrorCode.POST_NOT_EXISTED);
        }
        postRepository.deleteById(id);
    }

    public PostResponse updatePrivacyPost(UUID id, String privacy) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        post.setPrivacy(PostPrivacy.valueOf(privacy));
        Post updated = postRepository.save(post);
        return mapToResponse(updated);
    }

    public PostResponse updateCommentBlockPost(UUID id, Boolean commentLocked) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        post.setCommentLocked(commentLocked);
        Post updated = postRepository.save(post);
        return mapToResponse(updated);
    }

    private PostResponse mapToResponse(Post post) {
        long commentCount = postCommentService.countComments(post.getId());
        return PostResponse.builder()
                .id(post.getId())
                .author(userMapper.toUserResponse(post.getAuthor()))
                .content(post.getContent())
                .privacy(post.getPrivacy().name())
                .commentLocked(post.getCommentLocked())
                .mediaList(post.getMediaList().stream()
                        .map(m -> PostMediaResponse.builder()
                                .id(m.getId())
                                .postId(post.getId())
                                .url(m.getUrl())
                                .mediaType(m.getMediaType())
                                .width(m.getWidth())
                                .height(m.getHeight())
                                .durationSec(m.getDurationSec())
                                .position(m.getPosition())
                                .thumbnailUrl(m.getThumbnailUrl())
                                .build())
                        .toList())
                .commentCount(commentCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}