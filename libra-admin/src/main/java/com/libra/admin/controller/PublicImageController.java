package com.libra.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/public")
@Tag(name = "公共资源", description = "无需登录的公共资源接口")
public class PublicImageController {

    private static final Duration TIMEOUT = Duration.ofSeconds(8);
    private static final long MAX_BYTES = 8L * 1024L * 1024L;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(TIMEOUT)
            .build();

    @GetMapping("/image-proxy")
    @Operation(summary = "图片代理（用于外链封面防盗链）")
    public ResponseEntity<byte[]> imageProxy(@RequestParam String url) throws Exception {
        URI uri = parseAndValidate(url);

        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(TIMEOUT)
                .header("User-Agent", USER_AGENT)
                .header("Referer", "https://book.douban.com/")
                .header("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8")
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return ResponseEntity.status(response.statusCode()).build();
        }

        OptionalLong contentLength = response.headers().firstValueAsLong("Content-Length");
        if (contentLength.isPresent() && contentLength.getAsLong() > MAX_BYTES) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }
        if (response.body() != null && response.body().length > MAX_BYTES) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).build();
        }

        MediaType mediaType = resolveMediaType(response.headers().firstValue("Content-Type").orElse(null));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic());

        return new ResponseEntity<>(response.body(), headers, HttpStatus.OK);
    }

    private static URI parseAndValidate(String rawUrl) {
        URI uri;
        try {
            uri = new URI(rawUrl);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("invalid url");
        }

        if (uri.getScheme() == null || uri.getHost() == null) {
            throw new IllegalArgumentException("invalid url");
        }

        String scheme = uri.getScheme().toLowerCase(Locale.ROOT);
        if (!scheme.equals("http") && !scheme.equals("https")) {
            throw new IllegalArgumentException("invalid url");
        }

        String host = uri.getHost().toLowerCase(Locale.ROOT);
        if (!host.endsWith(".doubanio.com")) {
            throw new IllegalArgumentException("invalid url");
        }

        return uri;
    }

    private static MediaType resolveMediaType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return MediaType.IMAGE_JPEG;
        }

        String normalized = contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "image/png" -> MediaType.IMAGE_PNG;
            case "image/gif" -> MediaType.IMAGE_GIF;
            case "image/webp" -> MediaType.parseMediaType("image/webp");
            case "image/jpeg", "image/jpg" -> MediaType.IMAGE_JPEG;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}

