package com.gialong.facebook.postcomment;

import com.gialong.facebook.base.PageResponse;
import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import com.gialong.facebook.mention.Mention;
import com.gialong.facebook.mention.MentionRepository;
import com.gialong.facebook.post.Post;
import com.gialong.facebook.post.PostRepository;
import com.gialong.facebook.user.User;
import com.gialong.facebook.user.UserMapper;
import com.gialong.facebook.user.UserRepository;
import com.gialong.facebook.user.UserService;
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
public class PostCommentService {
    private final PostCommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final MentionRepository mentionRepository;

    @Transactional
    public PostCommentResponse addComment(
            UUID postId, UUID authorId, String content, UUID parentId, List<UUID> mentionedUserIds) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXISTED));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PostComment comment = PostComment.builder()
                .post(post)
                .author(author)
                .content(content)
                .build();
        // Set parent comment if it's a reply
        if (parentId != null) {
            PostComment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_EXISTED));
            comment.setParent(parent);
        }
        commentRepository.save(comment);

        // Handle mentions
        if (mentionedUserIds != null && !mentionedUserIds.isEmpty()) {
            List<Mention> mentions = mentionedUserIds.stream()
                    .map(userId -> {
                        User mentionedUser = userRepository.findById(userId)
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
                        return Mention.builder()
                                .post(post)
                                .comment(comment)
                                .mentionedUser(mentionedUser)
                                .build();
                    })
                    .toList();
            mentionRepository.saveAll(mentions);
        }

        return toResponse(comment);
    }

    public long countComments(UUID postId) {
        return commentRepository.countByPostId(postId);
    }

    public PageResponse<PostCommentResponse> getComments(UUID postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostComment> comments = commentRepository.findByPostIdAndParentIsNull(postId, pageable);

        List<PostCommentResponse> content = comments.getContent().stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<PostCommentResponse>builder()
                .content(content)
                .page(comments.getNumber())
                .size(comments.getSize())
                .totalElements(comments.getTotalElements())
                .totalPages(comments.getTotalPages())
                .last(comments.isLast())
                .build();
    }

    public PageResponse<PostCommentResponse> findByParentId(UUID commentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PostComment> comments = commentRepository.findByParentId(commentId, pageable);

        List<PostCommentResponse> content = comments.getContent().stream()
                .map(this::toResponse)
                .toList();

        return PageResponse.<PostCommentResponse>builder()
                .content(content)
                .page(comments.getNumber())
                .size(comments.getSize())
                .totalElements(comments.getTotalElements())
                .totalPages(comments.getTotalPages())
                .last(comments.isLast())
                .build();
    }

    private PostCommentResponse toResponse(PostComment comment) {
        User user = userService.getUserById(comment.getAuthor().getId());
        return PostCommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .author(userMapper.toUserResponse(user))
                .content(comment.getContent())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .repliesCount(commentRepository.countByPostIdAndParentId(comment.getPost().getId(), comment.getId()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }


}