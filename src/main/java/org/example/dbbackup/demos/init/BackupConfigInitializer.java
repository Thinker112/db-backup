package org.example.dbbackup.demos.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dbbackup.demos.entity.BackupConfig;
import org.example.dbbackup.demos.repository.BackupConfigRepository;
import org.example.dbbackup.demos.schedule.DynamicScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author yueyubo
 * @date 2025-02-05
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BackupConfigInitializer implements CommandLineRunner {

    private final BackupConfigRepository backupConfigRepository;
    private final DynamicScheduler dynamicScheduler;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;


    @Override
    public void run(String... args) {
        if (backupConfigRepository.count() == 0) {
            BackupConfig defaultConfig = new BackupConfig();
            defaultConfig.setDbUser(datasourceUsername);
            defaultConfig.setDbPassword(datasourcePassword);
            defaultConfig.setBackupDir("/tmp/backup/mysql");
            defaultConfig.setScheduleCron("0 0 2 * * ?");
            defaultConfig.setRetentionDays(7);
            defaultConfig.setMaxBackupFiles(10);
            defaultConfig.setMaxDiskUsageGb(10);
            // **解析 spring.datasource.url 以获取数据库信息**
            parseDatabaseUrl(datasourceUrl, defaultConfig);
            backupConfigRepository.save(defaultConfig);
            log.info("✅ 默认备份配置已插入");
        } else {
            log.info("✅ 备份配置已存在，无需初始化");
        }

        dynamicScheduler.reschedule();
    }

    private void parseDatabaseUrl(String url, BackupConfig config) {
        // spring.datasource.url 格式:
        // jdbc:mysql://localhost:3306/mydatabase?useSSL=false&serverTimezone=UTC
        try {
            url = url.substring(url.indexOf("//") + 2); // 去掉 jdbc:mysql://
            String[] parts = url.split("/");
            String hostPort = parts[0]; // "localhost:3306"
            String dbName = parts[1].split("\\?")[0]; // "mydatabase"

            String[] hostPortParts = hostPort.split(":");
            String host = hostPortParts[0];
            int port = hostPortParts.length > 1 ? Integer.parseInt(hostPortParts[1]) : 3306;

            config.setDbHost(host);
            config.setDbPort(port);
            config.setDbName(dbName);
        } catch (Exception e) {
            log.error("解析 spring.datasource.url 失败: {}", url);
            config.setDbHost("localhost");
            config.setDbPort(3306);
            config.setDbName("rsvmcsdb");
        }
    }
}