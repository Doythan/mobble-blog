package org.example.mobble._util.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImgUtil {
    public static final Path STATIC_IMG_DIR = Paths.get("src/main/resources/static/img");

    private static final Pattern DATA_URL = Pattern.compile("^data:(image/[a-zA-Z0-9.+-]+);base64,(.+)$");
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    // 기본 썸네일 이미지 경로 (static/img/thumbnail.png)
    private static final String DEFAULT_THUMBNAIL_URL = "/img/thumbnail.png";

    private static String extFromMime(String mime) {
        return switch (mime) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".bin";
        };
    }

    public static void deleteAllImagesForPost(String username, Integer boardId) {
        try {
            if (!Files.exists(STATIC_IMG_DIR)) return;
            try (var files = Files.list(STATIC_IMG_DIR)) {
                String prefix = (username + "-" + boardId + "-");
                files.filter(p -> p.getFileName().toString().startsWith(prefix))
                        .forEach(p -> {
                            try {
                                Files.deleteIfExists(p);
                            } catch (IOException ignored) {
                            }
                        });
            }
        } catch (IOException ignored) {
        }
    }

    public static Result replaceDataUrlsWithSavedFiles(String sanitizedHtml,
                                                       String username,
                                                       Integer boardId,
                                                       LocalDateTime createdAt) throws IOException {
        Document doc = Jsoup.parseBodyFragment(sanitizedHtml);
        Elements imgs = doc.select("img[src^=data:image]");


        // --- 내부 URL(/img/...) 처리 추가 ---
        Elements internalImgs = doc.select("img[src^=/img/]");

        Files.createDirectories(STATIC_IMG_DIR);

        int idx = 0;
        String firstUrl = null;
        List<String> savedUrls = new ArrayList<>();
        String ts = TS_FMT.format(createdAt != null ? createdAt : LocalDateTime.now());

        for (Element img : imgs) {
            String dataUrl = img.attr("src");
            Matcher m = DATA_URL.matcher(dataUrl);
            if (!m.matches()) {
                img.remove();
                continue;
            }
            String mime = m.group(1);
            String base64 = m.group(2);

            byte[] bytes;
            try {
                bytes = Base64.getDecoder().decode(base64);
            } catch (IllegalArgumentException e) {
                img.remove();
                continue;
            }

            String ext = extFromMime(mime);
            String fileName = String.format("%s-%d-%d-%s%s",
                    username, boardId, ++idx, ts, ext);
            Path savePath = STATIC_IMG_DIR.resolve(fileName);
            Files.write(savePath, bytes, StandardOpenOption.CREATE_NEW);

            String url = "/img/" + fileName;
            img.attr("src", url);

            if (firstUrl == null) firstUrl = url;
            savedUrls.add(url);
        }
        // --- 내부 /img/ 처리 ---
        for (Element img : internalImgs) {
            String url = img.attr("src");
            if (url.startsWith("/img/")) {
                savedUrls.add(url);
                if (firstUrl == null) firstUrl = url;
            } else {
                img.remove(); // 혹시 변칙적인 경우는 삭제
            }
        }

        // 이미지가 없으면 기본 썸네일 사용
        if (firstUrl == null) {
            firstUrl = DEFAULT_THUMBNAIL_URL;
        }

        return new Result(doc.body().html(), firstUrl, savedUrls);
    }

    public record Result(String html, String firstImageUrl, List<String> allImageUrls) {
    }
}
