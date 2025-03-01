package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import javax.persistence.*;

/**
 * 监测小时报表对象 hourly_average_air_data
 * 
 * @author runda
 * @date 2025-02-08
 */
@Entity
public class HourlyAverageAirData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 设备id */
    @Excel(name = "设备id")
    @Column(name = "device_id")
    private String deviceId;

//    /** 查询时间 */
//    @JsonFormat(pattern = "yyyy-MM-dd")
//    @Column(name = "query_time")
//    @Excel(name = "查询时间", width = 30, dateFormat = "yyyy-MM-dd")
//    private Date queryTime;

    /** 平均aqi */
    @Excel(name = "平均aqi")
    @Column(name = "average_aqi")
    private Long averageAqi;

    /** 平均so2 */
    @Excel(name = "平均so2")
    @Column(name = "average_so2")
    private Long averageSo2;

    /** 平均no2 */
    @Excel(name = "平均no2")
    @Column(name = "average_no2")
    private Long averageNo2;

    /** 平均o3 */
    @Excel(name = "平均o3")
    @Column(name = "average_o3")
    private Long averageO3;

    /** 平均pm25 */
    @Excel(name = "平均pm25")
    @Column(name = "average_pm2_5")
    private Double averagePm25;

    /** 平均pm25/24 */
    @Excel(name = "平均pm25/24")
    @Column(name = "average_pm2_5_24")
    private Long averagePm25_24;

    /** 平均pm10 */
    @Excel(name = "平均pm10")
    @Column(name = "average_pm10")
    private Double averagePm10;

    /** 平均pm10 */
    @Excel(name = "平均pm10/24")
    @Column(name = "average_pm10_24")
    private Long averagePm10_24;

    /** 级别 */
    @Excel(name = "级别")
    @Column(name = "aqi_level")
    private String aqiLevel;

    /** 质量 */
    @Excel(name = "质量")
    @Column(name = "aqi_quality")
    private String aqiQuality;

    /** 颜色 */
    @Excel(name = "颜色")
    @Column(name = "aqi_color")
    private String aqiColor;

    /** 主要污染物 */
    @Excel(name = "主要污染物")
    @Column(name = "primary_pollutant")
    private String primaryPollutant;

    @Excel(name = "站点id")
    @Column(name = "station_id")
    private String stationId;

    @Excel(name = "部门id")
    @Column(name = "dept_id")
    private String deptId;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "created_At")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(name = "updated_At")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updatedAt;

    /** 设备名称 */
    @Excel(name = "设备名称")
    @Column(name = "device_name")
    private String deviceName;

    /** 站点名称 */
    @Excel(name = "站点名称")
    @Column(name = "station_name")
    private String stationName;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setDeviceId(String deviceId) 
    {
        this.deviceId = deviceId;
    }

    public String getDeviceId() 
    {
        return deviceId;
    }

    public void setDeptId(String deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptId()
    {
        return deptId;
    }

    public void setAverageAqi(Long averageAqi) 
    {
        this.averageAqi = averageAqi;
    }

    public Long getAverageAqi()
    {
        return averageAqi;
    }
    public void setAverageSo2(Long averageSo2) 
    {
        this.averageSo2 = averageSo2;
    }

    public Long getAverageSo2() 
    {
        return averageSo2;
    }
    public void setAverageNo2(Long averageNo2) 
    {
        this.averageNo2 = averageNo2;
    }

    public Long getAverageNo2() 
    {
        return averageNo2;
    }
    public void setAverageO3(Long averageO3) 
    {
        this.averageO3 = averageO3;
    }

    public Long getAverageO3() 
    {
        return averageO3;
    }
    public void setAveragePm25(Double averagePm25)
    {
        this.averagePm25 = averagePm25;
    }

    public Double getAveragePm25()
    {
        return averagePm25;
    }

    public void setAveragePm25_24(Long averagePm25_24)
    {
        this.averagePm25_24 = averagePm25_24;
    }

    public Long getAveragePm25_24()
    {
        return averagePm25_24;
    }
    public void setAveragePm10(Double averagePm10)
    {
        this.averagePm10 = averagePm10;
    }

    public Double getAveragePm10()
    {
        return averagePm10;
    }

    public void setAveragePm10_24(Long averagePm10_24)
    {
        this.averagePm10_24 = averagePm10_24;
    }

    public Long getAveragePm10_24()
    {
        return averagePm10_24;
    }
    public void setAqiLevel(String aqiLevel) 
    {
        this.aqiLevel = aqiLevel;
    }

    public String getAqiLevel() 
    {
        return aqiLevel;
    }
    public void setAqiQuality(String aqiQuality) 
    {
        this.aqiQuality = aqiQuality;
    }

    public String getAqiQuality() 
    {
        return aqiQuality;
    }
    public void setAqiColor(String aqiColor) 
    {
        this.aqiColor = aqiColor;
    }

    public String getAqiColor() 
    {
        return aqiColor;
    }
    public void setPrimaryPollutant(String primaryPollutant) 
    {
        this.primaryPollutant = primaryPollutant;
    }

    public String getPrimaryPollutant() 
    {
        return primaryPollutant;
    }

    public void setStationId(String stationId)
    {
        this.stationId = stationId;
    }

    public String getStationId()
    {
        return stationId;
    }

    public void setCreatedAt(Date createdAt) 
    {
        this.createdAt = createdAt;
    }

    public Date getCreatedAt() 
    {
        return createdAt;
    }
    public void setUpdatedAt(Date updatedAt) 
    {
        this.updatedAt = updatedAt;
    }

    public Date getUpdatedAt() 
    {
        return updatedAt;
    }

    public void setStationName(String stationName)
    {
        this.stationName = stationName;
    }

    public String getStationName()
    {
        return stationName;
    }

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceId", getDeviceId())
                .append("stationId", getStationId())
//            .append("queryTime", getQueryTime())
            .append("averageAqi", getAverageAqi())
            .append("averageSo2", getAverageSo2())
            .append("averageNo2", getAverageNo2())
            .append("averageO3", getAverageO3())
            .append("averagePm25", getAveragePm25())
            .append("averagePm10", getAveragePm10())
            .append("aqiLevel", getAqiLevel())
            .append("aqiQuality", getAqiQuality())
            .append("aqiColor", getAqiColor())
            .append("primaryPollutant", getPrimaryPollutant())
            .append("createdAt", getCreatedAt())
            .append("updatedAt", getUpdatedAt())
            .toString();
    }
}
