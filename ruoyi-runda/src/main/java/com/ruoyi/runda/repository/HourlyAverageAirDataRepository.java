package com.ruoyi.runda.repository;

import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.runda.domain.HourlyAverageAirData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface HourlyAverageAirDataRepository extends JpaRepository<HourlyAverageAirData, Long> {

        @Query("SELECT h FROM HourlyAverageAirData h WHERE h.deviceId = :deviceId AND DATE(h.createdAt) = :date ORDER BY h.createdAt DESC")
        HourlyAverageAirData findByDateAndId(@Param("date") Date date, @Param("deviceId") String deviceId);

        @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM HourlyAverageAirData h WHERE h.deviceId = :deviceId AND h.createdAt = :createdAt")
        boolean existsByDeviceIdAndCreatedAtCustom(@Param("deviceId") String deviceId, @Param("createdAt") Date createdAt);

        @Query("SELECT h FROM HourlyAverageAirData h WHERE h.deviceId = :deviceId AND DATE(h.createdAt) = :date ORDER BY h.createdAt DESC")
        List<HourlyAverageAirData> findByDateAndDeviceId(@Param("date") Date date, @Param("deviceId") String deviceId);

        @Query("SELECT h FROM HourlyAverageAirData h WHERE h.deviceId = :deviceId AND h.createdAt = :createdAt ORDER BY h.createdAt DESC")
        HourlyAverageAirData findByDeviceIdAndCreatedAt(@Param("deviceId") String deviceId, @Param("createdAt") Date createdAt);

        //根据日期条件查询数据信息
        @Query("SELECT h FROM HourlyAverageAirData h WHERE h.createdAt = :createdAt ORDER BY h.createdAt DESC")
        List<HourlyAverageAirData> findByDateTime(@Param("createdAt") Date dateTimeStr);
        @Query("SELECT h FROM HourlyAverageAirData h WHERE h.updatedAt BETWEEN :beginTime AND :endTime ORDER BY h.updatedAt DESC")
        List<HourlyAverageAirData> findByDate(@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);


}

