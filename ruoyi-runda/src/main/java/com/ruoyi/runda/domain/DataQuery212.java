package com.ruoyi.runda.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 大气数据查询212对象 data_query212
 *
 * @author runda
 * @date 2025-01-07
 */
@Document(collection = "devicedata")
public class DataQuery212 extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键id */
    @Id
    private String id;
    /** 设备型号 */
    @Field("sn")
    private String sn;

    /** 日期 */
    @Field("date")
    private Date date;
    /** 完整数据*/
    @Field("fullData")
    private String fullData;//完整数据
    /** 主板温度*/
    @Field("主板温度")
    private Double deviceTemperature;//主板温度

    /** 温度 */
    @Field("temperature")
    private Double temperature;

    /** 湿度 */
    @Field("humidity")
    private Double humidity;

    /** 风速 */
    @Field("windSpeed")
    private Double windSpeed;

    /** 风向 */
    @Field("windDirection")
    private Double windDirection;//风向

    /** 风向 */
    @Field("windDirectionString")
    private String windDirectionString;


    /** 压力 */
    @Field("pressure")
    private Double pressure;


    /** 噪音 */
    @Field("noise")
    private Double noise;

    /** 粉尘pm2.5 */
    @Field("dust")
    private Double dust;//粉尘pm2.5

    /** pm10浓度 */
    @Field("pm10")
    private Double pm10;

    /** pm浓度小于100μm的颗粒物*/
    @Field("tsp")
    private Double tsp;


    /** vocs浓度 */
    @Field("voc")
    private Double voc;

    /** so2浓度 */
    @Field("so2Thickness")
    private Double so2Thickness;

    /** no2浓度 */
    @Field("no2Thickness")
    private Double no2Thickness;

    /** co浓度 */
    @Field("coThickness")
    private Double coThickness;

    /** o3 浓度 */
    @Field("co3Thickness")
    private Double co3Thickness;

    /** no浓度 */
    @Field("noThickness")
    private Double noThickness;


    /** 粉尘pm0.3颗粒以上 */
    @Field("pm03Above")
    private String pm03above;

    /** 粉尘pm0.5颗粒以上 */
    @Field("pm05Above")
    private String pm05above;

    /** 粉尘pm1.0颗粒以上 */
    @Field("pm1Count")
    private String pm1Count;

    /** 粉尘pm2.5颗粒数 */
    @Field("pm25Count")
    private String pm25Count;

    /** 粉尘pm10颗粒数 */
    @Field("pm10Count")
    private String pm10Count;


    /** 粉尘pm1.0颗粒数 */
    @Field("pm1Above")
    private String pm1above;

    /** 粉尘pm2.5颗粒以上 */
    @Field("pm25Above")
    private String pm25above;

    /** 粉尘pm5颗粒以上 */
    @Field("pm5Above")
    private String pm5above;

    /** pm10Above浓度 */
    @Field("pm10Above")
    private String pm10above;

    /** 经度 */
    @Field("longitude")
    private Double longitude;

    /** 纬度 */
    @Field("latitude")
    private Double latitude;

    /** 空气质量指数 */
    @Field("aqi")
    private Double aqi;

    /** 首要污染物 */
    @Field("primaryPollutant")
    private String primaryPollutant;


    /** 创建时间 */
    @Field("createDate")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;


    /** 站点_id */
    @Field("stationId")
    private Long stationId;

    /** 设备_id */
    @Field("deviceId")
    private Long deviceId;

    /** 设备名称 */
    @Field("deviceName")
    private String deviceName;
    /** 站点名称 */
    @Field("stationName")
    private String stationName;

    public void DeviceData() {
        // TODO Auto-generated constructor stub
    }

    public void DeviceData(Long deviceId, String mn, String deviceName,
                           Integer status, Long deptId, String path, Long stationId,
                           String stationName, Date createDate, Date date,
                           Double longitude, Double latitude) {
        setDeviceId(deviceId);
        setSn(mn);
        setDeviceName(deviceName);
        setStationId(stationId);
        setStationName(stationName);
        setLongitude(longitude);
        setLatitude(latitude);
        setCreateDate(createDate);
        setDate(date);
//		this
    }

    // Getters and Setters

    public String getPm03above() {
        return pm03above;
    }
    public void setPm03above(String pm03above) {
        this.pm03above = pm03above;
    }
    public String getPm05above() {
        return pm05above;
    }
    public void setPm05above(String pm05above) {
        this.pm05above = pm05above;
    }
    public String getPm1Count() {
        return pm1Count;
    }
    public void setPm1Count(String pm1Count) {
        this.pm1Count = pm1Count;
    }
    public String getPm25Count() {
        return pm25Count;
    }
    public void setPm25Count(String pm25Count) {
        this.pm25Count = pm25Count;
    }
    public String getPm10Count() {
        return pm10Count;
    }
    public void setPm10Count(String pm10Count) {
        this.pm10Count = pm10Count;
    }
    public String getPm1above() {
        return pm1above;
    }
    public void setPm1above(String pm1above) {
        this.pm1above = pm1above;
    }
    public String getPm25above() {
        return pm25above;
    }
    public void setPm25above(String pm25above) {
        this.pm25above = pm25above;
    }
    public String getPm5above() {
        return pm5above;
    }
    public void setPm5above(String pm5above) {
        this.pm5above = pm5above;
    }
    public String getPm10above() {
        return pm10above;
    }
    public void setPm10above(String pm10above) {
        this.pm10above = pm10above;
    }
    public Long getStationId() {
        return stationId;
    }
    public void setStationId(Long stationId) {
        this.stationId = stationId;
    }
    public Long getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Double getTsp() {
        return tsp;
    }
    public void setTsp(Double tsp) {
        this.tsp = tsp;
    }
    public Double getVoc() {
        return voc;
    }
    public void setVoc(Double voc) {
        this.voc = voc;
    }

    public String getStationName() {
        return stationName;
    }
    public void setStationName(String stationName) {
        this.stationName = stationName;
    }
    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Date getDate(){
        return this.date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public String getFullData(){
        return this.fullData;
    }
    public void setFullData(String fullData){
        this.fullData=fullData;
    }
    public Double getDeviceTemperature(){
        return this.deviceTemperature;
    }
    public void setDeviceTemperature(Double deviceTemperature){
        this.deviceTemperature=deviceTemperature;
    }
    public Double getTemperature(){
        return this.temperature;
    }
    public void setTemperature(Double temperature){
        this.temperature=temperature;
    }
    public Double getWindSpeed(){
        return this.windSpeed;
    }
    public void setWindSpeed(Double windSpeed){
        this.windSpeed=windSpeed;
    }
    public Double getWindDirection(){
        return this.windDirection;
    }
    public void setWindDirection(Double windDirection){
        this.windDirection=windDirection;
    }
    public Double getDust(){
        return this.dust;
    }
    public void setDust(Double dust){
        this.dust=dust;
    }
    public Double getHumidity(){
        return this.humidity;
    }
    public void setHumidity(Double humidity){
        this.humidity=humidity;
    }
    public Double getPressure(){
        return this.pressure;
    }
    public void setPressure(Double pressure){
        this.pressure=pressure;
    }
    public Double getSo2Thickness(){
        return this.so2Thickness;
    }
    public void setSo2Thickness(Double so2Thickness){
        this.so2Thickness=so2Thickness;
    }
    public Double getNo2Thickness(){
        return this.no2Thickness;
    }
    public void setNo2Thickness(Double no2Thickness){
        this.no2Thickness=no2Thickness;
    }
    public Double getCoThickness(){
        return this.coThickness;
    }
    public void setCoThickness(Double coThickness){
        this.coThickness=coThickness;
    }
    public Double getCo3Thickness(){
        return this.co3Thickness;
    }
    public void setCo3Thickness(Double co3Thickness){
        this.co3Thickness=co3Thickness;
    }
    public Double getLongitude(){
        return this.longitude;
    }
    public void setLongitude(Double longitude){
        this.longitude=longitude;
    }
    public Double getLatitude(){
        return this.latitude;
    }
    public void setLatitude(Double latitude){
        this.latitude=latitude;
    }
    public String getPrimaryPollutant() {
        return primaryPollutant;
    }
    public void setPrimaryPollutant(String primaryPollutant) {
        this.primaryPollutant = primaryPollutant;
    }
    public Double getAqi() {
        return aqi;
    }
    public void setAqi(Double aqi) {
        this.aqi = aqi;
    }

    public Double getNoThickness() {
        return noThickness;
    }
    public void setNoThickness(Double noThickness) {
        this.noThickness = noThickness;
    }
    public Double getPm10() {
        return pm10;
    }
    public void setPm10(Double pm10) {
        this.pm10 = pm10;
    }
    public String getSn() {
        return sn;
    }
    public void setSn(String sn) {
        this.sn = sn;
    }
    public Double getNoise() {
        return noise;
    }
    public void setNoise(Double noise) {
        this.noise = noise;
    }

    public String getWindDirectionString() {
        Double data=this.windDirection;
        if (data == null){
            return "";
        }
        if (data >= 348.76 || data <= 11.25) {
            this.windDirectionString = "北";
        } else if (data >= 11.26 && data <= 78.75) {
            this.windDirectionString = "东北";
        } else if (data >= 78.76 && data <= 101.25) {
            this.windDirectionString = "东";
        } else if (data >= 101.26 && data <= 168.75) {
            this.windDirectionString = "东南";
        } else if (data >= 168.76 && data <= 191.25) {
            this.windDirectionString = "南";
        } else if (data >= 191.26 &&  data <= 258.75) {
            this.windDirectionString = "西南";
        } else if (data >= 258.76 && data <= 281.25) {
            this.windDirectionString = "西";
        } else if (data >= 281.26 &&  data <= 348.75) {
            this.windDirectionString = "西北";
        }
        return windDirectionString;
    }
    public void setWindDirectionString(String windDirectionString) {
        this.windDirectionString = windDirectionString;
    }



}