package com.ruoyi.runda.repository;

import com.ruoyi.runda.domain.AirDataHour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface AirDataHourRepository extends MongoRepository<AirDataHour, String> {
    /**
     * 查找在指定时间范围内的空气质量小时数据，并分页。
     *
     * @param startDate 开始时间戳（毫秒）
     * @param endDate 结束时间戳（毫秒）
     * @param pageable 分页参数
     * @return 符合条件的空气质量小时数据分页结果
     */
    @Query("{ 'createDate' : { $gte : ?0, $lte : ?1 } }")
    Page<AirDataHour> findCustomByCreateDateBetweenTimestamps(long startDate, long endDate, Pageable pageable);
    /**
     * 查找在指定时间范围内且属于特定设备的所有空气质量小时数据。
     *
     * @param startDate 开始时间戳（毫秒）
     * @param endDate 结束时间戳（毫秒）
     * @param deviceId 设备ID
     * @return 符合条件的空气质量小时数据列表
     */
    @Query("{ 'createDate' : { $gte : ?0, $lte : ?1 }, 'deviceId': ?2 }")
    List<AirDataHour> findByCreateDateBetweenAndDeviceId(long startDate, long endDate, String deviceId);
    /**
     * 查找在指定时间范围内的所有空气质量小时数据。
     *
     * @param startDate 开始时间戳（毫秒）
     * @param endDate 结束时间戳（毫秒）
     * @return 符合条件的空气质量小时数据列表
     */
    @Query("{ 'createDate' : { $gte : ?0, $lte : ?1 } }")
    List<AirDataHour> findByCreateDateBetween(long startDate, long endDate);

    @Query("{ 'createDate' : { $gte : ?0, $lte : ?1 } }")
    List<AirDataHour> findByCreateDateBetween2(long startDate, long endDate);

}