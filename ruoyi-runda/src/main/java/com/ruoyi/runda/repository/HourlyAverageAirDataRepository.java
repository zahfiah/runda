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

        @Query("SELECT h FROM HourlyAverageAirData h WHERE DATE(h.createdAt) = :createdAt AND h.deviceId IN :deviceIds ORDER BY h.createdAt DESC")
        List<HourlyAverageAirData> findByDateAndDeviceIds(@Param("createdAt") Date createdAt, @Param("deviceIds") Set<String> deviceIds);

        @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM HourlyAverageAirData h WHERE h.deviceId = :deviceId AND h.createdAt = :createdAt")
        boolean existsByDeviceIdAndCreatedAtCustom(@Param("deviceId") String deviceId, @Param("createdAt") Date createdAt);

        @Query("SELECT h FROM HourlyAverageAirData h WHERE h.deviceId = :deviceId AND DATE(h.createdAt) = :date ORDER BY h.createdAt DESC")
        List<HourlyAverageAirData> findByDateAndDeviceId(@Param("date") Date date, @Param("deviceId") String deviceId);

        @Query("SELECT h FROM HourlyAverageAirData h WHERE h.deviceId = :deviceId AND h.createdAt = :createdAt ORDER BY h.createdAt DESC")
        HourlyAverageAirData findByDeviceIdAndCreatedAt(@Param("deviceId") String deviceId, @Param("createdAt") Date createdAt);
}
