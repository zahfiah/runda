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
}
