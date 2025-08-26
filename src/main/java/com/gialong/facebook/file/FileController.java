package com.gialong.facebook.file;

import com.gialong.facebook.exception.AppException;
import com.gialong.facebook.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        FileResponse response = fileService.saveFile(file);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {

        try {
            Path filePath = Paths.get("uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found or not readable");
            }

            // Xác định MIME type
            MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);

            long fileLength = resource.contentLength();

            // Với video -> trả full file luôn
            if (mediaType.getType().equals("video")) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("video/mp4"))
                        .contentLength(fileLength)
                        .body(resource);
            }

            // Với ảnh hoặc file khác
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .contentLength(fileLength)
                    .body(resource);
        } catch (Exception e) {
            throw new AppException(ErrorCode.FILE_TYPE_NOT_SUPPORTED);
        }
    }
}