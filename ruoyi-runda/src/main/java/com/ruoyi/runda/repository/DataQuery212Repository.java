package com.ruoyi.runda.repository;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.DataQuery212;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface DataQuery212Repository extends MongoRepository<DataQuery212, String> {

    Page<DataQuery212> findByDeviceId(String deviceId, Pageable pageable);


    Page<DataQuery212> findByCreateDateBetween(long startTimestamp, long endTimestamp, Pageable pageable);

    Page<DataQuery212> findAll(Pageable pageable);

}



