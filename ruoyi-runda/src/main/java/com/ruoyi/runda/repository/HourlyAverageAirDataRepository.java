package com.ruoyi.runda.repository;

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

        @Query("SELECT h FROM HourlyAverageAirData h WHERE DATE(h.createdAt) = :createdAt AND h.deviceId IN :deviceIds")
        List<HourlyAverageAirData> findByDateAndDeviceIds(@Param("createdAt") Date createdAt, @Param("deviceIds") Set<String> deviceIds);

        // 或者使用 @Query 注解
        @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM HourlyAverageAirData h WHERE h.deviceId = :deviceId AND h.createdAt = :createdAt")
        boolean existsByDeviceIdAndCreatedAtCustom(@Param("deviceId") String deviceId, @Param("createdAt") Date createdAt);
}
