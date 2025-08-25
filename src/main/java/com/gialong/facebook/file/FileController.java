package com.gialong.facebook.file;

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

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename,
                                            @RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {

        Path filePath = Paths.get("uploads").resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("File not found or not readable");
        }

        // Xác định MIME type
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        // Nếu là video và có header Range, trả về partial content để stream
        if (mediaType.getType().equals("video") && rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            long fileLength = resource.contentLength();
            long rangeStart = 0;
            long rangeEnd = fileLength - 1;

            String[] ranges = rangeHeader.substring(6).split("-");
            rangeStart = Long.parseLong(ranges[0]);
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            }
            if (rangeEnd > fileLength - 1) {
                rangeEnd = fileLength - 1;
            }

            long contentLength = rangeEnd - rangeStart + 1;

            return ResponseEntity.status(206)
                    .header("Content-Type", mediaType.toString())
                    .header("Accept-Ranges", "bytes")
                    .header("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength)
                    .header("Content-Length", String.valueOf(contentLength))
                    .body(new UrlResource(filePath.toUri()));
        }

        // Nếu không phải video hoặc không có Range header → trả full file
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);
    }
}