package com.ngaso.Ngaso.Controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Value("${app.upload.root}")
    private String uploadRoot;

    @GetMapping("/uploads")
    public ResponseEntity<Map<String, Object>> uploads() {
        Path resolved = Paths.get(uploadRoot).toAbsolutePath().normalize();
        boolean exists = Files.exists(resolved);
        boolean writable = false;
        try {
            writable = Files.isWritable(resolved) || (!exists && Files.isWritable(resolved.getParent()));
        } catch (Exception ignored) { }

        Map<String, Object> body = Map.of(
                "uploadRoot", resolved.toString(),
                "exists", exists,
                "writable", writable,
                "publicBase", "/uploads/"
        );
        return ResponseEntity.ok(body);
    }
}
