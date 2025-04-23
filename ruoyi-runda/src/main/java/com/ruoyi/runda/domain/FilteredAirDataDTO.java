package com.ruoyi.runda.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Date;

public class FilteredAirDataDTO {
    private String mn;
    private Double pm10;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    // Getters and Setters
    public String getSn() {
        return mn;
    }

    public void setSn(String mn) {
        this.mn = mn;
    }

    public Double getAveragePm10() {
        return pm10;
    }

    public void setAveragePm10(Double pm10) {
        this.pm10 = pm10;
    }

    public Date getCreatedAt() {
        return time;
    }

    public void setCreatedAt(Date time) {
        this.time = time;
    }
}
