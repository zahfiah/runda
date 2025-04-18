package com.ruoyi.runda.repository;

import com.ruoyi.runda.domain.AirDataHour;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
public interface DeviceDataRepository extends MongoRepository<AirDataHour, String> {

    @Query(value = "{ createDate: { $gte: ?0, $lte: ?1 } }", sort = "{ createDate: -1 }")
    List<AirDataHour> findByCreateDateBetween(long startTimestamp, long endTimestamp);
    @Query(value = "{ deviceId: ?0, createDate: { $gte: ?1, $lte: ?2 } }", sort = "{  createDate: -1 }")
    List<AirDataHour> findByDeviceIdAndCreateDateBetween(String deviceId, long startTimestamp, long endTimestamp, Pageable pageable);
}
