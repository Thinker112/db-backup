package org.example.dbbackup.demos.controller;

import lombok.RequiredArgsConstructor;
import org.example.dbbackup.demos.entity.BackupConfig;
import org.example.dbbackup.demos.repository.BackupConfigRepository;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;
import java.util.List;
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
    public List<String> listBackupFiles() {
        BackupConfig backupConfig = backupConfigRepository.findById(1L).get();
        File file = new File(backupConfig.getBackupDir());

        if (!file.exists()) return List.of();
        return Arrays.stream(file.listFiles())
                .map(File::getName)
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
}