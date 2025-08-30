package com.gialong.facebook.file;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
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
    public void getFile(
            @PathVariable String filename,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            Path filePath = Paths.get("uploads").resolve(filename).normalize();
            File file = filePath.toFile();

            if (!file.exists() || !file.canRead()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }
            response.setContentType(mimeType);

            long fileLength = file.length();
            String rangeHeader = request.getHeader("Range");

            long start = 0;
            long end = fileLength - 1;

            if (rangeHeader != null) {
                // Ví dụ: "Range: bytes=1000-"
                String[] ranges = rangeHeader.replace("bytes=", "").split("-");
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
            }

            long contentLength = end - start + 1;
            response.setHeader("Accept-Ranges", "bytes");
            response.setHeader("Content-Length", String.valueOf(contentLength));
            response.setHeader("Content-Range", "bytes " + start + "-" + end + "/" + fileLength);

            try (RandomAccessFile raf = new RandomAccessFile(file, "r");
                 OutputStream os = response.getOutputStream()) {

                raf.seek(start);

                byte[] buffer = new byte[8192];
                long bytesToRead = contentLength;
                int len;

                while ((len = raf.read(buffer)) != -1 && bytesToRead > 0) {
                    if (bytesToRead < len) {
                        os.write(buffer, 0, (int) bytesToRead);
                        break;
                    }
                    os.write(buffer, 0, len);
                    bytesToRead -= len;
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}