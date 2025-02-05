CREATE TABLE `backup_config` (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `db_name` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                                 `db_user` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                                 `db_password` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
                                 `db_host` varchar(255) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'localhost',
                                 `db_port` int NOT NULL DEFAULT '3306',
                                 `backup_dir` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '/tmp/backup/mysql' COMMENT '备份文件目录',
                                 `schedule_cron` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0 0 2 * * ?' COMMENT '定时备份cron表达式',
                                 `retention_days` int DEFAULT '7' COMMENT '备份文件保留天数',
                                 `max_backup_files` int DEFAULT '10' COMMENT '最大备份数量',
                                 `max_disk_usage_gb` int DEFAULT '10' COMMENT '备份文件最大磁盘占用',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;