package com.ruoyi.runda.repository;

import com.ruoyi.runda.domain.DataQuery212;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DataQuery212OVRepository {

    Page<DataQuery212> findByDeviceId(String deviceId, Pageable pageable);

    Page<DataQuery212> findByCreateDateBetween(long startTimestamp, long endTimestamp, Pageable pageable);

    Page<DataQuery212> findByDeviceIdAndCreateDateBetween(String deviceId, long startTimestamp, long endTimestamp, Pageable pageable);
}