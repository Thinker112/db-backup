package org.example.dbbackup.demos.controller;

import lombok.RequiredArgsConstructor;
import org.example.dbbackup.demos.entity.BackupConfig;
import org.example.dbbackup.demos.repository.BackupConfigRepository;
import org.example.dbbackup.demos.schedule.DynamicScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * @author yueyubo
 * @date 2025-02-05
 */
@Controller
@RequestMapping("/config")
@RequiredArgsConstructor
public class ConfigController {

    private final BackupConfigRepository backupConfigRepository;
    private final DynamicScheduler scheduler;


    @GetMapping
    public String getConfigPage(Model model) {
        Optional<BackupConfig> config = backupConfigRepository.findById(1L);
        model.addAttribute("config", config.orElse(new BackupConfig()));
        return "config";
    }

    @PostMapping("/save")
    public String saveConfig(@ModelAttribute BackupConfig config) {
        config.setId(1L); // 只允许一个配置
        backupConfigRepository.save(config);
        scheduler.reschedule();
        return "redirect:/config?success";
    }
}
