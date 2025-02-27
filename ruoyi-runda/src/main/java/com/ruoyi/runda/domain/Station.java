package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监测站点管理对象 station
 *
 * @author runda
 * @date 2025-01-10
 */
public class Station extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 地址 */
    @Excel(name = "地址")
    private String stationAddr;

    /** 监测站 */
    @Excel(name = "监测站")
    private String stationName;

    /** 河流名称 */
    private String riverName;

    /** 河流代码 */
    private String riverCode;

    /** 断面名称（水质监测） */
    private String sectionName;

    /** 断面代码 */
    private String sectionCode;

    /** 断面所在河流级别：1：干流；2：平原区；3：一级支流 */
    private Long riverLevel;

    /** 汇入水体（河、湖、库、渠） */
    private String inwardWater;

    /** 河流断面属性 */
    private String sectionProperties;

    /** 监测方式（1：自动站；2：手工站） */
    private Long monitoringWay;

    /** 断面控制级别 */
    private Long sectionControlLevel;

    /** 省 */
    private Long province;

    /** 省简称 */
    private String provinceCn;

    /** 市 */
    private Long city;

    /** 市简称 */
    private String cityCn;

    /** 区/县 */
    private Long county;

    /** 区/县简称 */
    private String countyCn;

    /** 乡/镇 */
    private Long town;

    /** 乡/镇简称 */
    private String townCn;

    /** 部门ID */
    private Long userId;

    /** 操作员 */
    private Long systemUserId;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /** 最后修改时间 */
    private Date lastUpdatedTime;

    /** 经度 */
    private Double longitude;

    /** 纬度 */
    private Double latitude;

    /** 监测站类型（1：空气监测；2：水质监测；3：土壤监测） */
    @Excel(name = "监测站类型", readConverterExp = "1=：空气监测；2：水质监测；3：土壤监测")
    private Long type;

    /** 状态 */
    @Excel(name = "状态")
    private Long stationStatus;

    /** 电话 */
    private String phone;

    /** 联系人 */
    private String linkMan;

    /** 国控站名称 */
    private String countryName;

    /** 国控站ID */
    private Long countryId;

    /** 占地面积 */
    @Excel(name = "占地面积")
    private Long floorSpace;

    /** 施工许可证 */
    @Excel(name = "施工许可证")
    private String licensNumber;

    /** 数据来源（0:本平台；1:第三方） */
    private Long fromResource;

    /** 建设单位名称 */
    private String jsdwmc;

    /** 建设单位信用代码 */
    private String jstsyhydm;

    /** 施工单位名称 */
    private String sgdwmc;

    /** 施工单位统一社会信用代码 */
    private String sgtshyhdym;

    /** 监管部门 */
    private String jgbm;

    /** 监管责任人 */
    private String jgzr;

    /** 监管电话 */
    private String jgzrdh;

    /** 施工阶段 */
    private String sjjd;

    /** PM10是否安装 */
    private String sfazjcsb;

    /** 统计时间 */
    private Date jgrq;

    /** 设备id */
    private Long deviceId;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setStationAddr(String stationAddr)
    {
        this.stationAddr = stationAddr;
    }

    public String getStationAddr()
    {
        return stationAddr;
    }
    public void setStationName(String stationName)
    {
        this.stationName = stationName;
    }

    public String getStationName()
    {
        return stationName;
    }
    public void setRiverName(String riverName)
    {
        this.riverName = riverName;
    }

    public String getRiverName()
    {
        return riverName;
    }
    public void setRiverCode(String riverCode)
    {
        this.riverCode = riverCode;
    }

    public String getRiverCode()
    {
        return riverCode;
    }
    public void setSectionName(String sectionName)
    {
        this.sectionName = sectionName;
    }

    public String getSectionName()
    {
        return sectionName;
    }
    public void setSectionCode(String sectionCode)
    {
        this.sectionCode = sectionCode;
    }

    public String getSectionCode()
    {
        return sectionCode;
    }
    public void setRiverLevel(Long riverLevel)
    {
        this.riverLevel = riverLevel;
    }

    public Long getRiverLevel()
    {
        return riverLevel;
    }
    public void setInwardWater(String inwardWater)
    {
        this.inwardWater = inwardWater;
    }

    public String getInwardWater()
    {
        return inwardWater;
    }
    public void setSectionProperties(String sectionProperties)
    {
        this.sectionProperties = sectionProperties;
    }

    public String getSectionProperties()
    {
        return sectionProperties;
    }
    public void setMonitoringWay(Long monitoringWay)
    {
        this.monitoringWay = monitoringWay;
    }

    public Long getMonitoringWay()
    {
        return monitoringWay;
    }
    public void setSectionControlLevel(Long sectionControlLevel)
    {
        this.sectionControlLevel = sectionControlLevel;
    }

    public Long getSectionControlLevel()
    {
        return sectionControlLevel;
    }
    public void setProvince(Long province)
    {
        this.province = province;
    }

    public Long getProvince()
    {
        return province;
    }
    public void setProvinceCn(String provinceCn)
    {
        this.provinceCn = provinceCn;
    }

    public String getProvinceCn()
    {
        return provinceCn;
    }
    public void setCity(Long city)
    {
        this.city = city;
    }

    public Long getCity()
    {
        return city;
    }
    public void setCityCn(String cityCn)
    {
        this.cityCn = cityCn;
    }

    public String getCityCn()
    {
        return cityCn;
    }
    public void setCounty(Long county)
    {
        this.county = county;
    }

    public Long getCounty()
    {
        return county;
    }
    public void setCountyCn(String countyCn)
    {
        this.countyCn = countyCn;
    }

    public String getCountyCn()
    {
        return countyCn;
    }
    public void setTown(Long town)
    {
        this.town = town;
    }

    public Long getTown()
    {
        return town;
    }
    public void setTownCn(String townCn)
    {
        this.townCn = townCn;
    }

    public String getTownCn()
    {
        return townCn;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setSystemUserId(Long systemUserId)
    {
        this.systemUserId = systemUserId;
    }

    public Long getSystemUserId()
    {
        return systemUserId;
    }
    public void setCreatedTime(Date createdTime)
    {
        this.createdTime = createdTime;
    }

    public Date getCreatedTime()
    {
        return createdTime;
    }
    public void setLastUpdatedTime(Date lastUpdatedTime)
    {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public Date getLastUpdatedTime()
    {
        return lastUpdatedTime;
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
    public void setType(Long type)
    {
        this.type = type;
    }

    public Long getType()
    {
        return type;
    }
    public void setStationStatus(Long stationStatus)
    {
        this.stationStatus = stationStatus;
    }

    public Long getStationStatus()
    {
        return stationStatus;
    }
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPhone()
    {
        return phone;
    }
    public void setLinkMan(String linkMan)
    {
        this.linkMan = linkMan;
    }

    public String getLinkMan()
    {
        return linkMan;
    }
    public void setCountryName(String countryName)
    {
        this.countryName = countryName;
    }

    public String getCountryName()
    {
        return countryName;
    }
    public void setCountryId(Long countryId)
    {
        this.countryId = countryId;
    }

    public Long getCountryId()
    {
        return countryId;
    }
    public void setFloorSpace(Long floorSpace)
    {
        this.floorSpace = floorSpace;
    }

    public Long getFloorSpace()
    {
        return floorSpace;
    }
    public void setLicensNumber(String licensNumber)
    {
        this.licensNumber = licensNumber;
    }

    public String getLicensNumber()
    {
        return licensNumber;
    }
    public void setFromResource(Long fromResource)
    {
        this.fromResource = fromResource;
    }

    public Long getFromResource()
    {
        return fromResource;
    }
    public void setJsdwmc(String jsdwmc)
    {
        this.jsdwmc = jsdwmc;
    }

    public String getJsdwmc()
    {
        return jsdwmc;
    }
    public void setJstsyhydm(String jstsyhydm)
    {
        this.jstsyhydm = jstsyhydm;
    }

    public String getJstsyhydm()
    {
        return jstsyhydm;
    }
    public void setSgdwmc(String sgdwmc)
    {
        this.sgdwmc = sgdwmc;
    }

    public String getSgdwmc()
    {
        return sgdwmc;
    }
    public void setSgtshyhdym(String sgtshyhdym)
    {
        this.sgtshyhdym = sgtshyhdym;
    }

    public String getSgtshyhdym()
    {
        return sgtshyhdym;
    }
    public void setJgbm(String jgbm)
    {
        this.jgbm = jgbm;
    }

    public String getJgbm()
    {
        return jgbm;
    }
    public void setJgzr(String jgzr)
    {
        this.jgzr = jgzr;
    }

    public String getJgzr()
    {
        return jgzr;
    }
    public void setJgzrdh(String jgzrdh)
    {
        this.jgzrdh = jgzrdh;
    }

    public String getJgzrdh()
    {
        return jgzrdh;
    }
    public void setSjjd(String sjjd)
    {
        this.sjjd = sjjd;
    }

    public String getSjjd()
    {
        return sjjd;
    }
    public void setSfazjcsb(String sfazjcsb)
    {
        this.sfazjcsb = sfazjcsb;
    }

    public String getSfazjcsb()
    {
        return sfazjcsb;
    }
    public void setJgrq(Date jgrq)
    {
        this.jgrq = jgrq;
    }

    public Date getJgrq()
    {
        return jgrq;
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
                .append("stationAddr", getStationAddr())
                .append("stationName", getStationName())
                .append("riverName", getRiverName())
                .append("riverCode", getRiverCode())
                .append("sectionName", getSectionName())
                .append("sectionCode", getSectionCode())
                .append("riverLevel", getRiverLevel())
                .append("inwardWater", getInwardWater())
                .append("sectionProperties", getSectionProperties())
                .append("monitoringWay", getMonitoringWay())
                .append("sectionControlLevel", getSectionControlLevel())
                .append("province", getProvince())
                .append("provinceCn", getProvinceCn())
                .append("city", getCity())
                .append("cityCn", getCityCn())
                .append("county", getCounty())
                .append("countyCn", getCountyCn())
                .append("town", getTown())
                .append("townCn", getTownCn())
                .append("userId", getUserId())
                .append("systemUserId", getSystemUserId())
                .append("createdTime", getCreatedTime())
                .append("lastUpdatedTime", getLastUpdatedTime())
                .append("longitude", getLongitude())
                .append("latitude", getLatitude())
                .append("type", getType())
                .append("remark", getRemark())
                .append("stationStatus", getStationStatus())
                .append("phone", getPhone())
                .append("linkMan", getLinkMan())
                .append("countryName", getCountryName())
                .append("countryId", getCountryId())
                .append("floorSpace", getFloorSpace())
                .append("licensNumber", getLicensNumber())
                .append("fromResource", getFromResource())
                .append("jsdwmc", getJsdwmc())
                .append("jstsyhydm", getJstsyhydm())
                .append("sgdwmc", getSgdwmc())
                .append("sgtshyhdym", getSgtshyhdym())
                .append("jgbm", getJgbm())
                .append("jgzr", getJgzr())
                .append("jgzrdh", getJgzrdh())
                .append("sjjd", getSjjd())
                .append("sfazjcsb", getSfazjcsb())
                .append("jgrq", getJgrq())
                .append("deviceId", getDeviceId())
                .toString();
    }
}
