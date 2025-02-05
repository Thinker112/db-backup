package org.example.dbbackup.demos.repository;

import org.example.dbbackup.demos.entity.BackupConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BackupConfigRepository extends JpaRepository<BackupConfig, Long> {
}