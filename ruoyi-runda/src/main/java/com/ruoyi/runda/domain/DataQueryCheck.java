package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 手动矫正对象 data_query_check
 * 
 * @author runda
 * @date 2025-01-07
 */
public class DataQueryCheck extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 设备id */
    @Excel(name = "设备id")
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String deviceName;

    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date date;

    /** 空气质量指数 */
    @Excel(name = "空气质量指数")
    private Long aqi;

    /** 温度 */
    private Long temperature;

    /** 风速 */
    private Long windSpeed;

    /** 风向 */
    private Long windDirection;

    /** 湿度 */
    private Long humidity;

    /** 压力 */
    private Long pressure;

    /** 噪音 */
    private Long noise;

    /** pm2.5浓度 */
    @Excel(name = "pm2.5浓度")
    private Long pm;

    /** pm10浓度 */
    @Excel(name = "pm10浓度")
    private Long pm10;

    /** so2浓度 */
    @Excel(name = "so2浓度")
    private Long so2Thickness;

    /** no2浓度 */
    @Excel(name = "no2浓度")
    private Long no2Thickness;

    /** co浓度 */
    @Excel(name = "co浓度")
    private Long coThickness;

    /** o3 浓度 */
    @Excel(name = "o3 浓度")
    private Long co3Thickness;

    /** vocs浓度 */
    @Excel(name = "vocs浓度")
    private Long voscThickness;

    /** 粉尘pm0.3颗粒以上 */
    private Long pm03Above;

    /** 粉尘pm0.5颗粒以上 */
    private Long pm05Above;

    /** 粉尘pm1.0颗粒数 */
    private Long pm1;

    /** 粉尘pm2.5颗粒数 */
    private Long pm25;


    /** 粉尘pm1.0颗粒以上 */
    private Long pm1Above;

    /** 粉尘pm2.5颗粒以上 */
    private Long pm25Above;

    /** 粉尘pm5颗粒以上 */
    private Long pm5Above;

    /** 粉尘pm10颗粒以上 */
    private Long pm10Above;

    /** 经度 */
    private Long longitude;

    /** 纬度 */
    private Long latitude;

    /** pm100浓度 */
    private Long tsp;

    /** 首要污染物 */
    private String primaryPollutant;

    /** 站点_id */
    @Excel(name = "站点_id")
    private Long stationId;

    /** 设备型号 */
    private String sn;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setDeviceId(Long deviceId) 
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId() 
    {
        return deviceId;
    }
    public void setDeviceName(String deviceName) 
    {
        this.deviceName = deviceName;
    }

    public String getDeviceName() 
    {
        return deviceName;
    }
    public void setDate(Date date) 
    {
        this.date = date;
    }

    public Date getDate() 
    {
        return date;
    }
    public void setAqi(Long aqi) 
    {
        this.aqi = aqi;
    }

    public Long getAqi() 
    {
        return aqi;
    }
    public void setTemperature(Long temperature) 
    {
        this.temperature = temperature;
    }

    public Long getTemperature() 
    {
        return temperature;
    }
    public void setWindSpeed(Long windSpeed) 
    {
        this.windSpeed = windSpeed;
    }

    public Long getWindSpeed() 
    {
        return windSpeed;
    }
    public void setWindDirection(Long windDirection) 
    {
        this.windDirection = windDirection;
    }

    public Long getWindDirection() 
    {
        return windDirection;
    }
    public void setHumidity(Long humidity) 
    {
        this.humidity = humidity;
    }

    public Long getHumidity() 
    {
        return humidity;
    }
    public void setPressure(Long pressure) 
    {
        this.pressure = pressure;
    }

    public Long getPressure() 
    {
        return pressure;
    }
    public void setNoise(Long noise) 
    {
        this.noise = noise;
    }

    public Long getNoise() 
    {
        return noise;
    }
    public void setPm(Long pm) 
    {
        this.pm = pm;
    }

    public Long getPm() 
    {
        return pm;
    }



    public void setSo2Thickness(Long so2Thickness) 
    {
        this.so2Thickness = so2Thickness;
    }

    public Long getSo2Thickness() 
    {
        return so2Thickness;
    }
    public void setNo2Thickness(Long no2Thickness) 
    {
        this.no2Thickness = no2Thickness;
    }

    public Long getNo2Thickness() 
    {
        return no2Thickness;
    }
    public void setCoThickness(Long coThickness) 
    {
        this.coThickness = coThickness;
    }

    public Long getCoThickness() 
    {
        return coThickness;
    }
    public void setCo3Thickness(Long co3Thickness) 
    {
        this.co3Thickness = co3Thickness;
    }

    public Long getCo3Thickness() 
    {
        return co3Thickness;
    }
    public void setVoscThickness(Long voscThickness) 
    {
        this.voscThickness = voscThickness;
    }

    public Long getVoscThickness() 
    {
        return voscThickness;
    }
    public void setPm03Above(Long pm03Above) 
    {
        this.pm03Above = pm03Above;
    }

    public Long getPm03Above() 
    {
        return pm03Above;
    }
    public void setPm05Above(Long pm05Above) 
    {
        this.pm05Above = pm05Above;
    }

    public Long getPm05Above() 
    {
        return pm05Above;
    }
    public void setPm1(Long pm1) 
    {
        this.pm1 = pm1;
    }

    public Long getPm1() 
    {
        return pm1;
    }
    public void setPm25(Long pm25) 
    {
        this.pm25 = pm25;
    }

    public Long getPm25() 
    {
        return pm25;
    }
    public void setPm10(Long pm10) 
    {
        this.pm10 = pm10;
    }

    public Long getPm10() 
    {
        return pm10;
    }
    public void setPm1Above(Long pm1Above) 
    {
        this.pm1Above = pm1Above;
    }

    public Long getPm1Above() 
    {
        return pm1Above;
    }
    public void setPm25Above(Long pm25Above) 
    {
        this.pm25Above = pm25Above;
    }

    public Long getPm25Above() 
    {
        return pm25Above;
    }
    public void setPm5Above(Long pm5Above) 
    {
        this.pm5Above = pm5Above;
    }

    public Long getPm5Above() 
    {
        return pm5Above;
    }
    public void setPm10Above(Long pm10Above) 
    {
        this.pm10Above = pm10Above;
    }

    public Long getPm10Above() 
    {
        return pm10Above;
    }
    public void setLongitude(Long longitude) 
    {
        this.longitude = longitude;
    }

    public Long getLongitude() 
    {
        return longitude;
    }
    public void setLatitude(Long latitude) 
    {
        this.latitude = latitude;
    }

    public Long getLatitude() 
    {
        return latitude;
    }
    public void setTsp(Long tsp) 
    {
        this.tsp = tsp;
    }

    public Long getTsp() 
    {
        return tsp;
    }
    public void setPrimaryPollutant(String primaryPollutant) 
    {
        this.primaryPollutant = primaryPollutant;
    }

    public String getPrimaryPollutant() 
    {
        return primaryPollutant;
    }
    public void setStationId(Long stationId) 
    {
        this.stationId = stationId;
    }

    public Long getStationId() 
    {
        return stationId;
    }
    public void setSn(String sn) 
    {
        this.sn = sn;
    }

    public String getSn() 
    {
        return sn;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deviceId", getDeviceId())
            .append("deviceName", getDeviceName())
            .append("date", getDate())
            .append("aqi", getAqi())
            .append("temperature", getTemperature())
            .append("windSpeed", getWindSpeed())
            .append("windDirection", getWindDirection())
            .append("humidity", getHumidity())
            .append("pressure", getPressure())
            .append("noise", getNoise())
            .append("pm", getPm())
            .append("pm10", getPm10())
            .append("so2Thickness", getSo2Thickness())
            .append("no2Thickness", getNo2Thickness())
            .append("coThickness", getCoThickness())
            .append("co3Thickness", getCo3Thickness())
            .append("voscThickness", getVoscThickness())
            .append("pm03Above", getPm03Above())
            .append("pm05Above", getPm05Above())
            .append("pm1", getPm1())
            .append("pm25", getPm25())
            .append("pm10", getPm10())
            .append("pm1Above", getPm1Above())
            .append("pm25Above", getPm25Above())
            .append("pm5Above", getPm5Above())
            .append("pm10Above", getPm10Above())
            .append("longitude", getLongitude())
            .append("latitude", getLatitude())
            .append("tsp", getTsp())
            .append("primaryPollutant", getPrimaryPollutant())
            .append("stationId", getStationId())
            .append("sn", getSn())
            .toString();
    }
}
