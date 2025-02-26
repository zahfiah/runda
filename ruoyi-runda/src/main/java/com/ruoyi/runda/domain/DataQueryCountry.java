package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 国控数据对象 data_query_country
 *
 * @author ruoyi
 * @date 2025-02-25
 */
public class DataQueryCountry extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 监测点昵称 */
    @Excel(name = "监测点昵称")
    private String name;

    /** 空气质量指数aqi */
    @Excel(name = "空气质量指数aqi")
    private Double aqi;

    /** pm2.5浓度 */
    @Excel(name = "pm2.5浓度")
    private Double pm;

    /** pm10浓度 */
    @Excel(name = "pm10浓度")
    private Double pm10;

    /** so2浓度 */
    @Excel(name = "so2浓度")
    private Double so2Thickness;

    /** no2浓度 */
    @Excel(name = "no2浓度")
    private Double no2Thickness;

    /** co浓度 */
    @Excel(name = "co浓度")
    private Double coThickness;

    /** o3 浓度 */
    @Excel(name = "o3 浓度")
    private Double co3Thickness;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    /** 经度 */
    @Excel(name = "经度")
    private Double longitude;

    /** 纬度 */
    @Excel(name = "纬度")
    private Double latitude;

    /** 站点编号 */
    @Excel(name = "站点编号")
    private Double stationId;

    /** 设备编号 */
    @Excel(name = "设备编号")
    private Long deviceId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setAqi(Double aqi)
    {
        this.aqi = aqi;
    }

    public Double getAqi()
    {
        return aqi;
    }
    public void setPm(Double pm)
    {
        this.pm = pm;
    }

    public Double getPm()
    {
        return pm;
    }
    public void setPm10(Double pm10)
    {
        this.pm10 = pm10;
    }

    public Double getPm10()
    {
        return pm10;
    }
    public void setSo2Thickness(Double so2Thickness)
    {
        this.so2Thickness = so2Thickness;
    }

    public Double getSo2Thickness()
    {
        return so2Thickness;
    }
    public void setNo2Thickness(Double no2Thickness)
    {
        this.no2Thickness = no2Thickness;
    }

    public Double getNo2Thickness()
    {
        return no2Thickness;
    }
    public void setCoThickness(Double coThickness)
    {
        this.coThickness = coThickness;
    }

    public Double getCoThickness()
    {
        return coThickness;
    }
    public void setCo3Thickness(Double co3Thickness)
    {
        this.co3Thickness = co3Thickness;
    }

    public Double getCo3Thickness()
    {
        return co3Thickness;
    }
    public void setTime(Date time)
    {
        this.time = time;
    }

    public Date getTime()
    {
        return time;
    }
    public void setLongitude(Double longitude)
    {
        this.longitude = longitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }
    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    public Double getLatitude()
    {
        return latitude;
    }
    public void setStationId(Double stationId)
    {
        this.stationId = stationId;
    }

    public Double getStationId()
    {
        return stationId;
    }
    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public Long getDeviceId()
    {
        return deviceId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("aqi", getAqi())
                .append("pm", getPm())
                .append("pm10", getPm10())
                .append("so2Thickness", getSo2Thickness())
                .append("no2Thickness", getNo2Thickness())
                .append("coThickness", getCoThickness())
                .append("co3Thickness", getCo3Thickness())
                .append("time", getTime())
                .append("longitude", getLongitude())
                .append("latitude", getLatitude())
                .append("stationId", getStationId())
                .append("deviceId", getDeviceId())
                .toString();
    }
}
