package com.ruoyi.system.service;

import org.springframework.stereotype.Service;

@Service
public interface DataMigrationService {
    String migrateData();

    String migrateStateData();

    String migrateMonitorData();

    String migrateMonitorInfoData();

    String migrateMonitorHourData();
}
