package org.example.dbbackup.demos.schedule;

import lombok.extern.slf4j.Slf4j;
import org.example.dbbackup.demos.entity.BackupConfig;
import org.example.dbbackup.demos.repository.BackupConfigRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yueyubo
 * @date 2025-02-05
 */
@Service
@Slf4j
public class BackupCleanupService {

    private final BackupConfigRepository backupConfigRepository;

    public BackupCleanupService(BackupConfigRepository backupConfigRepository) {
        this.backupConfigRepository = backupConfigRepository;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOldBackups() {
        Optional<BackupConfig> configOpt = backupConfigRepository.findById(1L);
        if (configOpt.isEmpty()) {
            log.error("[clean]未找到备份配置，跳过清理任务");
            return;
        }

        BackupConfig config = configOpt.get();
        File backupDir = new File(config.getBackupDir());

        if (!backupDir.exists() || !backupDir.isDirectory()) {
            log.error("[clean]备份目录不存在: {}", config.getBackupDir());
            return;
        }

        File[] backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".sql.gz"));
        if (backupFiles == null || backupFiles.length == 0) {
            log.info("[clean]没有需要清理的备份文件");
            return;
        }

        // 按时间排序
        Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified));

        // **1. 删除超过保留天数的备份**
        Instant retentionThreshold = Instant.now().minusSeconds(config.getRetentionDays() * 86400);
        for (File file : backupFiles) {
            try {
                BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                Instant fileTime = attr.creationTime().toInstant();
                if (fileTime.isBefore(retentionThreshold)) {
                    log.info("[clean]删除过期备份: {}", file.getName());
                    file.delete();
                }
            } catch (Exception e) {
                log.error("[clean]无法读取文件信息: {}", file.getName());
            }
        }

        // **2. 仅保留最新的 maxBackupFiles 个备份**
        backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".sql.gz"));
        if (backupFiles != null && backupFiles.length > config.getMaxBackupFiles()) {
            Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified));
            for (int i = 0; i < backupFiles.length - config.getMaxBackupFiles(); i++) {
                log.info("[clean]删除超出数量限制的备份: {}", backupFiles[i].getName());
                backupFiles[i].delete();
            }
        }

        // **3. 确保磁盘占用不超过 maxDiskUsageGb**
        long totalSize = Arrays.stream(Objects.requireNonNull(backupDir.listFiles()))
                .mapToLong(File::length)
                .sum() / (1024 * 1024 * 1024); // 转换为 GB

        while (totalSize > config.getMaxDiskUsageGb()) {
            backupFiles = backupDir.listFiles((dir, name) -> name.endsWith(".sql.gz"));
            if (backupFiles == null || backupFiles.length == 0) break;

            Arrays.sort(backupFiles, Comparator.comparingLong(File::lastModified));
            log.info("[clean]删除超出磁盘限制的备份: {}", backupFiles[0].getName());
            totalSize -= backupFiles[0].length() / (1024 * 1024 * 1024);
            backupFiles[0].delete();
        }
    }
}