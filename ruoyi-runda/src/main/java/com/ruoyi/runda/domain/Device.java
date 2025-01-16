package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 监测设备管理设备对象 device
 * 
 * @author runda
 * @date 2025-01-11
 */
public class Device extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 设备名称 */
    @Excel(name = "设备名称")
    private String name;

    /** 设备号 */
    @Excel(name = "设备号")
    private String sn;

    /** 经度 */
    private Double longitude;

    /** 纬度 */
    private Double latitude;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /** 最后修改时间 */
    private Date lastUpdatedTime;

    /** 状态 */
    @Excel(name = "状态")
    private Long status;

    /** 机构ID */
    private Long orgId;

    /** 工地状态 */
    private Long buildStatus;

    /** 省 */
    private Long province;

    /** 省昵称 */
    private String provinceCn;

    /** 市 */
    private Long city;

    /** 市昵称 */
    private String cityCn;

    /** 区/县 */
    private Long county;

    /** 县名称 */
    private String countyCn;

    /** 乡/镇 */
    private Long town;

    /** 乡/镇昵称 */
    private String townCn;

    /** SIM卡号 */
    @Excel(name = "SIM卡号")
    private String phoneNumber;

    /** 制造商 */
    private String manufacturer;

    /** 部门ID */
    private Long departmentId;

    /** 操作员 */
    private Long systemUserId;

    /** 数据来源 */
    private Long fromResource;

    /** 设备类型 */
    private Long type;

    /** ip */
    private String ip;

    /** 端口 */
    private String port;

    /** 用户名 */
    private String userName;

    /** 用户密码 */
    private String password;

    /** 是否运维 */
    @Excel(name = "是否运维")
    private Long isYunwei;

    /** 国控站code */
    private String guid;

    /** 站点id */
    @Excel(name = "站点id")
    private Long stationId;

    /** 地址 */
    private String addr;

    /** 县级id */
    private Long userId;

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
    public void setSn(String sn) 
    {
        this.sn = sn;
    }

    public String getSn() 
    {
        return sn;
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
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }
    public void setOrgId(Long orgId) 
    {
        this.orgId = orgId;
    }

    public Long getOrgId() 
    {
        return orgId;
    }
    public void setBuildStatus(Long buildStatus) 
    {
        this.buildStatus = buildStatus;
    }

    public Long getBuildStatus() 
    {
        return buildStatus;
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
    public void setPhoneNumber(String phoneNumber) 
    {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() 
    {
        return phoneNumber;
    }
    public void setManufacturer(String manufacturer) 
    {
        this.manufacturer = manufacturer;
    }

    public String getManufacturer() 
    {
        return manufacturer;
    }
    public void setDepartmentId(Long departmentId) 
    {
        this.departmentId = departmentId;
    }

    public Long getDepartmentId() 
    {
        return departmentId;
    }
    public void setSystemUserId(Long systemUserId) 
    {
        this.systemUserId = systemUserId;
    }

    public Long getSystemUserId() 
    {
        return systemUserId;
    }
    public void setFromResource(Long fromResource) 
    {
        this.fromResource = fromResource;
    }

    public Long getFromResource() 
    {
        return fromResource;
    }
    public void setType(Long type) 
    {
        this.type = type;
    }

    public Long getType() 
    {
        return type;
    }
    public void setIp(String ip) 
    {
        this.ip = ip;
    }

    public String getIp() 
    {
        return ip;
    }
    public void setPort(String port) 
    {
        this.port = port;
    }

    public String getPort() 
    {
        return port;
    }
    public void setUserName(String userName) 
    {
        this.userName = userName;
    }

    public String getUserName() 
    {
        return userName;
    }
    public void setPassword(String password) 
    {
        this.password = password;
    }

    public String getPassword() 
    {
        return password;
    }
    public void setIsYunwei(Long isYunwei) 
    {
        this.isYunwei = isYunwei;
    }

    public Long getIsYunwei() 
    {
        return isYunwei;
    }
    public void setGuid(String guid) 
    {
        this.guid = guid;
    }

    public String getGuid() 
    {
        return guid;
    }
    public void setStationId(Long stationId) 
    {
        this.stationId = stationId;
    }

    public Long getStationId() 
    {
        return stationId;
    }
    public void setAddr(String addr) 
    {
        this.addr = addr;
    }

    public String getAddr() 
    {
        return addr;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("sn", getSn())
            .append("longitude", getLongitude())
            .append("latitude", getLatitude())
            .append("createdTime", getCreatedTime())
            .append("lastUpdatedTime", getLastUpdatedTime())
            .append("status", getStatus())
            .append("orgId", getOrgId())
            .append("buildStatus", getBuildStatus())
            .append("province", getProvince())
            .append("provinceCn", getProvinceCn())
            .append("city", getCity())
            .append("cityCn", getCityCn())
            .append("county", getCounty())
            .append("countyCn", getCountyCn())
            .append("town", getTown())
            .append("townCn", getTownCn())
            .append("remark", getRemark())
            .append("phoneNumber", getPhoneNumber())
            .append("manufacturer", getManufacturer())
            .append("departmentId", getDepartmentId())
            .append("systemUserId", getSystemUserId())
            .append("fromResource", getFromResource())
            .append("type", getType())
            .append("ip", getIp())
            .append("port", getPort())
            .append("userName", getUserName())
            .append("password", getPassword())
            .append("isYunwei", getIsYunwei())
            .append("guid", getGuid())
            .append("stationId", getStationId())
            .append("addr", getAddr())
            .append("userId", getUserId())
            .toString();
    }
}
