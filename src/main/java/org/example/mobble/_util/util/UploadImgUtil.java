package org.example.mobble._util.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UploadImgUtil {
    // 지원하는 타입 매핑
    private static final Map<String, String> EXT_BY_MIME = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/gif", "gif",
            "image/webp", "webp"
    );

    public static class SaveOptions {
        public long maxSizeBytes = 5L * 1024 * 1024;  // 5MB
        public Set<String> allowedMime = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
        public String subDir = null;                  // 예: "avatars" (null이면 날짜 경로 사용)
        public String fixedFileName = null;           // 예: "123.jpg" (고정 파일명 쓰고 싶을 때)
        public boolean overwrite = true;              // fixedFileName일 때만 의미 있음
        public boolean makeDateDirs = true;           // 날짜 기반(y/M/d) 하위 폴더 생성
    }

    public static class SaveResult {
        public final String relativePath;  // 예: "uploads/avatars/2025/09/15/uuid.jpg"
        public final String publicUrl;     // 예:   "/uploads/avatars/2025/09/15/uuid.jpg"
        public final String filename;      // 파일명만
        public final long sizeBytes;
        public final Integer width;        // ImageIO가 지원하면 픽셀 크기, 아니면 null
        public final Integer height;

        public SaveResult(String relativePath, String publicUrl, String filename, long sizeBytes, Integer width, Integer height) {
            this.relativePath = relativePath;
            this.publicUrl = publicUrl;
            this.filename = filename;
            this.sizeBytes = sizeBytes;
            this.width = width;
            this.height = height;
        }
    }

    /**
     * 이미지 저장
     *
     * @param file          업로드된 파일(MultipartFile)
     * @param uploadRoot    로컬 저장 루트 (예: Paths.get("uploads"))  - 실제 파일이 저장되는 물리 경로
     * @param publicBaseUrl 정적 리소스 매핑된 공개 베이스 URL (예: "/uploads")
     * @param options       저장 옵션
     */
    public static SaveResult saveImage(MultipartFile file,
                                       Path uploadRoot,
                                       String publicBaseUrl,
                                       SaveOptions options) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드 파일이 비어 있습니다.");
        }

        // MIME 검증
        String contentType = Optional.ofNullable(file.getContentType()).orElse("");
        if (!options.allowedMime.contains(contentType)) {
            throw new IllegalArgumentException("허용되지 않은 이미지 타입입니다: " + contentType);
        }

        // 용량 검증
        if (file.getSize() > options.maxSizeBytes) {
            throw new IllegalArgumentException("파일이 너무 큽니다. 최대 " + options.maxSizeBytes + " bytes까지 허용됩니다.");
        }

        // 확장자 결정
        String ext = EXT_BY_MIME.getOrDefault(contentType, "bin");

        // 저장 디렉터리 구성
        Path baseDir = uploadRoot;
        StringBuilder rel = new StringBuilder();
        rel.append(uploadRoot.getFileName() != null ? uploadRoot.getFileName().toString() : "uploads");

        if (options.subDir != null && !options.subDir.isBlank()) {
            baseDir = baseDir.resolve(options.subDir);
            rel.append("/").append(options.subDir);
        }

        if (options.makeDateDirs && options.fixedFileName == null) {
            LocalDate now = LocalDate.now();
            baseDir = baseDir.resolve(String.valueOf(now.getYear()))
                    .resolve(String.format("%02d", now.getMonthValue()))
                    .resolve(String.format("%02d", now.getDayOfMonth()));
            rel.append("/").append(now.getYear())
                    .append("/").append(String.format("%02d", now.getMonthValue()))
                    .append("/").append(String.format("%02d", now.getDayOfMonth()));
        }

        Files.createDirectories(baseDir);

        // 파일명
        String filename;
        Path destPath;

        if (options.fixedFileName != null && !options.fixedFileName.isBlank()) {
            filename = options.fixedFileName;
            if (!filename.contains(".")) {
                filename += "." + ext;
            }
            destPath = baseDir.resolve(filename);

            if (!options.overwrite && Files.exists(destPath)) {
                throw new FileAlreadyExistsException("파일이 이미 존재합니다: " + destPath);
            }
        } else {
            filename = UUID.randomUUID().toString() + "." + ext;
            destPath = baseDir.resolve(filename);
        }

        // 실제 저장
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, destPath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 이미지 크기 읽기 (webp는 JVM 기본 ImageIO 미지원 → null일 수 있음)
        Integer w = null, h = null;
        try {
            BufferedImage img = ImageIO.read(destPath.toFile());
            if (img != null) {
                w = img.getWidth();
                h = img.getHeight();
            }
        } catch (Exception ignore) {
        }

        // 상대 경로 & 공개 URL
        String relativePath = rel + "/" + filename;
        String publicUrl = joinUrl(publicBaseUrl, relativePath.substring(relativePath.indexOf('/') + 1));
        // ↑ uploadRoot가 "uploads"라면 relativePath는 "uploads/..." 형태.
        // 공개 URL은 보통 "/uploads/..." 형태를 원하므로 앞의 "uploads" 이후를 붙여줍니다.

        return new SaveResult(relativePath, publicUrl, filename, Files.size(destPath), w, h);
    }

    /**
     * 기존 파일 삭제 (예: 아바타 교체 시)
     *
     * @param uploadRoot   저장 루트 (saveImage에 썼던 것과 동일)
     * @param relativePath 저장 시 받은 relativePath
     */
    public static boolean deleteQuietly(Path uploadRoot, String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return false;
        try {
            // relativePath가 "uploads/avatars/..." 형태라면, uploadRoot의 부모 명과 중복될 수 있음
            // 안전하게 uploadRoot의 부모를 기준으로 resolve 하지 말고, uploadRoot로부터의 하위만 취급
            Path p;
            if (relativePath.startsWith(uploadRoot.getFileName().toString())) {
                // "uploads/..." → "uploads" 이후 부분만 추출
                String sub = relativePath.substring(uploadRoot.getFileName().toString().length());
                if (sub.startsWith("/")) sub = sub.substring(1);
                p = uploadRoot.resolve(sub);
            } else {
                p = uploadRoot.resolve(relativePath);
            }
            return Files.deleteIfExists(p);
        } catch (Exception e) {
            return false;
        }
    }

    private static String joinUrl(String base, String path) {
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        if (!path.startsWith("/")) path = "/" + path;
        return base + path;
    }
}
