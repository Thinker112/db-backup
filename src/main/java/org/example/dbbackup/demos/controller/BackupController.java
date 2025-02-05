package org.example.dbbackup.demos.controller;

import lombok.RequiredArgsConstructor;
import org.example.dbbackup.demos.entity.BackupConfig;
import org.example.dbbackup.demos.repository.BackupConfigRepository;
import org.example.dbbackup.demos.schedule.BackupService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author yueyubo
 * @date 2025-02-05
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/backup")
public class BackupController {

    private final BackupConfigRepository backupConfigRepository;
    private final BackupService backupService;

    @GetMapping
    public String backupPage(Model model) {
        Optional<BackupConfig> configOpt = backupConfigRepository.findById(1L);
        BackupConfig config = configOpt.orElseGet(() -> {
            BackupConfig defaultConfig = new BackupConfig();
            defaultConfig.setDbHost("localhost");
            defaultConfig.setDbPort(3306);
            defaultConfig.setDbName("rsvmcsdb");
            defaultConfig.setDbUser("root");
            defaultConfig.setDbPassword("1qaz!QAZ");
            defaultConfig.setBackupDir("/opt/backups");
            defaultConfig.setScheduleCron("0 0 2 * * ?");
            defaultConfig.setRetentionDays(7);
            defaultConfig.setMaxBackupFiles(10);
            defaultConfig.setMaxDiskUsageGb(10);
            return defaultConfig;
        });
        model.addAttribute("backupConfig", config);
        return "backup";
    }

    @PostMapping("/execute")
    @ResponseBody
    public ResponseEntity<String> executeBackup() {
        try {
            backupService.executeBackup();
            return ResponseEntity.ok("✅ 备份成功！");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ 备份失败: " + e.getMessage());
        }
    }
}