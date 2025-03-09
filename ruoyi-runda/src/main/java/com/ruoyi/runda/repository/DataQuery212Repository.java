package com.ruoyi.runda.repository;

import com.ruoyi.runda.domain.AirDataResult;
import com.ruoyi.runda.domain.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DataQuery212Repository extends MongoRepository<AirDataResult, String> {

    // 根据 deviceId 查询，按 deviceId 升序排序
    @Query(value = "{ deviceId: ?0 }", sort = "{ deviceId: 1 }")
    Page<AirDataResult> findByDeviceId(String deviceId, Pageable pageable);

    // 根据时间范围查询，按 dateTime 降序排序
    @Query(value = "{ dataTime: { $gte: ?0, $lte: ?1 } }", sort = "{ dataTime: -1 }")
    Page<AirDataResult> findByCreateDateBetween(long startTimestamp, long endTimestamp, Pageable pageable);

    // 根据 deviceId 和时间范围联合查询，按 deviceId 升序和 dateTime 降序排序
    @Query(value = "{ deviceId: ?0, dataTime: { $gte: ?1, $lte: ?2 } }", sort = "{ deviceId: 1, dataTime: -1 }")
    Page<AirDataResult> findByDeviceIdAndCreateDateBetween(String deviceId, long startTimestamp, long endTimestamp, Pageable pageable);


}