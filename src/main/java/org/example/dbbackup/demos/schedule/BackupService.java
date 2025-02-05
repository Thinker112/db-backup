package org.example.dbbackup.demos.schedule;

import lombok.extern.slf4j.Slf4j;
import org.example.dbbackup.demos.entity.BackupConfig;
import org.example.dbbackup.demos.repository.BackupConfigRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * @author yueyubo
 * @date 2025-02-05
 */
@Service
@Slf4j
public class BackupService{

    private final BackupConfigRepository backupConfigRepository;

    public BackupService(BackupConfigRepository backupConfigRepository) {
        this.backupConfigRepository = backupConfigRepository;
    }

    public void executeBackup() {
        Optional<BackupConfig> configOpt = backupConfigRepository.findById(1L);
        if (configOpt.isEmpty()) {
            log.error("[backup]未配置数据库信息，无法备份");
            return;
        }

        BackupConfig config = configOpt.get();
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String backupFile = config.getBackupDir() + "/" + config.getDbName() + "_" + timestamp + ".sql.gz";

        new File(config.getBackupDir()).mkdirs();
        String command = String.format("mysqldump -h %s -P %d -u%s -p%s %s | gzip > %s",
                config.getDbHost(), config.getDbPort(), config.getDbUser(), config.getDbPassword(),
                config.getDbName(), backupFile);

        try {
            Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
            log.info("[backup]数据库备份成功: {}", backupFile);
        } catch (Exception e) {
            log.error("[backup]备份失败: {}", e.getMessage());
        }
    }

//    @Scheduled(cron = "0 0 2 * * ?")
//    public void scheduledBackup() {
//        executeBackup();
//    }
}
