package com.ruoyi.runda.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 大气环境监测数据实体类
 */
@Document(collection = "devicedata")
public class AirDataHour {
    private static final long serialVersionUID = 1L;

    @Id
    private ObjectId id;

    /** 空气质量指数 */
    @Field( "aqi")
    private Double aqi;

    /** 城市名称 */
    @Field(name = "城市名称")
    private String area;

    /** 监测站code */
    @Field(name = "监测站code")
    private String stationCode;

    /** 监测点名称 */
    @Field(name = "监测点名称")
    private String positionName;

    /** CO浓度 */
    @Field("coThickness")
    private Double coThickness;

    /** CO浓度/24h */
    @Field(name = "co浓度/24h")
    private Double co24h;

    /** NO2浓度 */
    @Field("noThickness")
    private Double noThickness;

    /** NO2浓度/24h */
    @Field(name = "no2浓度/24h")
    private Double no224h;

    /** O3 浓度 */
    @Field("co3Thickness")
    private Double co3Thickness;

    /** O3 浓度/24h */
    @Field(name = "o3 浓度/24h")
    private Double o324h;

    /** O3 8h 浓度 */
    @Field(name = "o3 8h 浓度")
    private Double o38h;

    /** PM10浓度 */
    @Field( "pm10")
    private Double pm10;

    /** PM10浓度/24h */
    @Field(name = "pm10浓度/24h")
    private Double pm1024h;

    /** PM2.5浓度 */
    @Field("dust")
    private Double pm25;

    /** PM2.5浓度/24h */
    @Field(name = "pm2_5浓度/24h")
    private Double pm2524h;

    /** SO2浓度 */
    @Field( "so2Thickness")
    private Double so2Thickness;

    /** SO2浓度/24h */
    @Field(name = "so2浓度/24h")
    private Double so224h;

    /** 首要污染物 */
    @Field( "primaryPollutant")
    private String primaryPollutant;

    /** 空气质量指数类别 */
    @Field(name = "空气质量指数")
    private String quality; // 有“优、良、轻度污染、中度污染、重度污染、严重污染”6类

    /** 创建时间 */
    @Field("createDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    /** 修改时间 */
    @Field("modifyDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyDate;

    /** 设备id */
    @Field(name = "deviceId")
    private String deviceId;

    /** 部门id */
    @Field("deptId")
    private String deptId;

    /** 日期 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    /** 经度 */
    @Field("longitude")
    private Double longitude;

    /** 纬度 */
    @Field("latitude")
    private Double latitude;

    /** 污染等级 */
    @Field("level")
    private String level;

    /** 颜色 */
    @Field("color")
    private String color;

    /** 排名 */
    @Field("ranking")
    private Integer ranking;

    /** 小时 */
    private int hour;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getAqi() {
        return aqi;
    }

    public void setAqi(Double aqi) {
        this.aqi = aqi;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Double getCo() {
        return coThickness;
    }

    public void setCoThickness(Double coThickness) {
        this.coThickness = coThickness;
    }

    public Double getCo24h() {
        return co24h;
    }

    public void setCo24h(Double co24h) {
        this.co24h = co24h;
    }

    public Double getNo2Thickness() {
        return noThickness;
    }

    public void setNo2Thickness(Double noThickness) {
        this.noThickness = noThickness;
    }

    public Double getNo224h() {
        return no224h;
    }

    public void setNo224h(Double no224h) {
        this.no224h = no224h;
    }

    public Double getCo3Thickness() {
        return co3Thickness;
    }

    public void setCo3Thickness(Double co3Thickness) {
        this.co3Thickness = co3Thickness;
    }

    public Double getO324h() {
        return o324h;
    }

    public void setO324h(Double o324h) {
        this.o324h = o324h;
    }

    public Double getO38h() {
        return o38h;
    }

    public void setO38h(Double o38h) {
        this.o38h = o38h;
    }

    public Double getPm10() {
        return pm10;
    }

    public void setPm10(Double pm10) {
        this.pm10 = pm10;
    }

    public Double getPm1024h() {
        return pm1024h;
    }

    public void setPm1024h(Double pm1024h) {
        this.pm1024h = pm1024h;
    }

    public Double getPm25() {
        return pm25;
    }

    public void setPm25(Double pm25) {
        this.pm25 = pm25;
    }

    public Double getPm2524h() {
        return pm2524h;
    }

    public void setPm2524h(Double pm2524h) {
        this.pm2524h = pm2524h;
    }

    public Double getSo2Thickness() {
        return so2Thickness;
    }

    public void setSo2Thickness(Double so2Thickness) {
        this.so2Thickness = so2Thickness;
    }

    public Double getSo224h() {
        return so224h;
    }

    public void setSo224h(Double so224h) {
        this.so224h = so224h;
    }

    public String getPrimaryPollutant() {
        return primaryPollutant;
    }

    public void setPrimaryPollutant(String primaryPollutant) {
        this.primaryPollutant = primaryPollutant;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("deviceId", getDeviceId())
                .append("deptId", getDeptId())
                .append("date", getDate())
                .append("aqi", getAqi())
                .append("area", getArea())
                .append("stationCode", getStationCode())
                .append("positionName", getPositionName())
                .append("co", getCo())
                .append("co24h", getCo24h())
                .append("no2", getNo2Thickness())
                .append("no224h", getNo224h())
                .append("o3", getCo3Thickness())
                .append("o324h", getO324h())
                .append("o38h", getO38h())
                .append("pm10", getPm10())
                .append("pm1024h", getPm1024h())
                .append("pm25", getPm25())
                .append("pm2524h", getPm2524h())
                .append("so2", getSo2Thickness())
                .append("so224h", getSo224h())
                .append("primaryPollutant", getPrimaryPollutant())
                .append("quality", getQuality())
                .append("createDate", getCreateDate())
                .append("modifyDate", getModifyDate())
                .append("longitude", getLongitude())
                .append("latitude", getLatitude())
                .append("level", getLevel())
                .append("color", getColor())
                .append("ranking", getRanking())
                .append("hour", getHour())
                .toString();
    }
}