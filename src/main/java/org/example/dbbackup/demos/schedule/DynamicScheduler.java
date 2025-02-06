package org.example.dbbackup.demos.schedule;

import lombok.extern.slf4j.Slf4j;
import org.example.dbbackup.demos.entity.BackupConfig;
import org.example.dbbackup.demos.repository.BackupConfigRepository;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

/**
 * @author yueyubo
 * @date 2025-02-05
 */

@Slf4j
@Service
public class DynamicScheduler {

    private final TaskScheduler taskScheduler;
    private final BackupService backupService;
    private final BackupConfigRepository backupConfigRepository;
    private ScheduledFuture<?> scheduledTask;

    public DynamicScheduler(TaskScheduler taskScheduler, BackupService backupService,
                            BackupConfigRepository backupConfigRepository) {
        this.taskScheduler = taskScheduler;
        this.backupService = backupService;
        this.backupConfigRepository = backupConfigRepository;
    }

    public void reschedule() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }

        BackupConfig config = backupConfigRepository.findById(1L).get();
        String cron = config.getScheduleCron();
        log.info("[backup-scheduler]-cron: {}", cron);
        scheduledTask = taskScheduler.schedule(backupService::executeBackup, new CronTrigger(cron));
    }
}
