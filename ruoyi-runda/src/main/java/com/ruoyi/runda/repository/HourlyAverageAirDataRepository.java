package com.ruoyi.runda.repository;

import com.ruoyi.runda.domain.HourlyAverageAirData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HourlyAverageAirDataRepository extends JpaRepository<HourlyAverageAirData, Long> {


}