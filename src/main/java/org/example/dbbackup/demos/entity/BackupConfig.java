package org.example.dbbackup.demos.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author yueyubo
 * @date 2025-02-05
 */
@Getter
@Setter
@Entity
@Table(name = "backup_config")
public class BackupConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dbName;
    private String dbUser;
    private String dbPassword;
    private String dbHost;
    private Integer dbPort;
    private String backupDir;
    private String scheduleCron;

    private Integer retentionDays;  // 备份保留天数
    private Integer maxBackupFiles; // 最大备份文件数
    private Integer maxDiskUsageGb; // 最大磁盘占用
}