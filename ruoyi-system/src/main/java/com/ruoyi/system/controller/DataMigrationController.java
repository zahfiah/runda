package com.ruoyi.system.controller;

import com.ruoyi.system.service.DataMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/migrate")
public class DataMigrationController {

    @Autowired
    private DataMigrationService dataMigrationService;
    /**
     * t_b_site_info
     * @return
     */
    @Scheduled(cron = "0 0 * * * ?")
    @GetMapping("/info")
    public String migrate() {
        return dataMigrationService.migrateData();
    }

    /**
     *t_d_site_state
     * @return
     */
    @Scheduled(cron = "0 0 * * * ?")
    @GetMapping("/state")
    public String state() {
        return dataMigrationService.migrateStateData();
    }

    @Scheduled(cron = "0 0 * * * ?")
    @GetMapping("/monitor")

    public String monitor() {
        return dataMigrationService.migrateMonitorData();
    }
    @Scheduled(cron = "0 0 * * * ?")
    @GetMapping("/monitor-info")
    public String monitorInfo() {
        return dataMigrationService.migrateMonitorInfoData();
    }
    @Scheduled(cron = "0 5 * * * ?")
    @GetMapping("/monitor-hour")
    public String monitorHour() {
        return dataMigrationService.migrateMonitorHourData();
    }
}