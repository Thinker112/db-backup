package org.example.dbbackup.demos.controller;

import lombok.RequiredArgsConstructor;
import org.example.dbbackup.demos.entity.BackupConfig;
import org.example.dbbackup.demos.repository.BackupConfigRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yueyubo
 * @date 2025-02-05
 */
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class BackupFileController {
    private final BackupConfigRepository backupConfigRepository;

    @GetMapping
    public List<Map<String, String>> listBackupFiles() {
        BackupConfig backupConfig = backupConfigRepository.findById(1L).get();
        File dir = new File(backupConfig.getBackupDir());

        if (!dir.exists()) return List.of();
        return Arrays.stream(dir.listFiles())
                .map(file -> Map.of(
                        "name", file.getName(),
                        "size", String.format("%.1f GB", file.length() / 1e9),
                        "date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified())),
                        "database", backupConfig.getDbName()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        BackupConfig configOpt = backupConfigRepository.findById(1L).get();
        File file = new File(configOpt.getBackupDir(), fileName);
        if (!file.exists()) return ResponseEntity.notFound().build();

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteBackupFile(@PathVariable String fileName) {
        BackupConfig config = backupConfigRepository.findById(1L).orElseThrow();
        File file = new File(config.getBackupDir(), fileName);
        if (file.exists() && file.delete()) {
            return ResponseEntity.ok("✅ 备份文件已删除");
        } else {
            return ResponseEntity.status(500).body("❌ 删除失败");
        }
    }
}