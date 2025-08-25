package com.gialong.facebook.file;

import com.gialong.facebook.auth.AuthService;
import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import com.gialong.facebook.user.User;
import com.gialong.facebook.user.UserService;
import com.gialong.facebook.userphoto.UserPhotoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {
    private static final String UPLOAD_DIR = "uploads"; // relative to working dir

    private final UserService userService;
    private final AuthService authService;
    private final UserPhotoService userPhotoService;

    public FileService(UserService userService, AuthService authService, UserPhotoService userPhotoService) {
        this.userService = userService;
        this.authService = authService;
        this.userPhotoService = userPhotoService;
    }

    public FileResponse saveFile(MultipartFile file) {
        try {
            // Lấy content type
            String contentType = file.getContentType();
            if (contentType == null) {
                throw new AppException(ErrorCode.FILE_NOT_AVAILABLE);
            }

            // Chỉ lấy phần chính (image / video)
            String type = contentType.split("/")[0];
            if (!type.equals("image") && !type.equals("video")) {
                throw new AppException(ErrorCode.FILE_NOT_AVAILABLE);
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/files/")
                    .path(filename)
                    .toUriString();

            UUID userId = authService.getMyInfo();
            User user = userService.getUserById(userId);
            userPhotoService.savePhoto(user, fileDownloadUri);

            return new FileResponse(fileDownloadUri, type);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file", e);
        }
    }
}
