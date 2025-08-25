package com.gialong.facebook.post;

import com.gialong.facebook.base.PageResponse;
import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import com.gialong.facebook.postmedia.PostMedia;
import com.gialong.facebook.postmedia.PostMediaResponse;
import com.gialong.facebook.user.User;
import com.gialong.facebook.user.UserMapper;
import com.gialong.facebook.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

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

    @Transactional(readOnly = true)
    public PostResponse getPost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        return mapToResponse(post);
    }

    public PageResponse<PostResponse> getPostsByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Post> posts = postRepository.findByAuthorId(userId, pageable);

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

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deletePost(UUID id) {
        if (!postRepository.existsById(id)) {
            throw new AppException(ErrorCode.POST_NOT_EXISTED);
        }
        postRepository.deleteById(id);
    }

    private PostResponse mapToResponse(Post post) {
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
                .likeCount(post.getLikes().size())
                .commentCount(post.getComments().size())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }


}