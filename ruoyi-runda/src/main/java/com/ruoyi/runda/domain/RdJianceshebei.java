package com.ruoyi.runda.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 设备对象 rd_jianceshebei
 * 
 * @author ruoyi
 * @date 2024-12-28
 */
public class RdJianceshebei extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** 设备名称(测站名称) */
    @Excel(name = "设备名称(测站名称)")
    private String name;

    /** 设备号 */
    @Excel(name = "设备号")
    private String sn;

    /** 经度 */
    @Excel(name = "经度")
    private Long longitude;

    /** 纬度 */
    @Excel(name = "纬度")
    private Long latitude;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createdTime;

    /** 最后修改时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后修改时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastUpdatedTime;

    /** 状态(1：正常，2：停用，3：删除) */
    @Excel(name = "状态(1：正常，2：停用，3：删除)")
    private Long status;

    /** 机构ID */
    @Excel(name = "机构ID")
    private Long orgId;

    /** 运行状态(1：运行正常，2：异常) */
    @Excel(name = "运行状态(1：运行正常，2：异常)")
    private Long runStatus;

    /** 省 */
    @Excel(name = "省")
    private Long province;

    /** 省昵称 */
    @Excel(name = "省昵称")
    private String provinceCn;

    /** 市 */
    @Excel(name = "市")
    private Long city;

    /** 市昵称 */
    @Excel(name = "市昵称")
    private String cityCn;

    /** 区/县 */
    @Excel(name = "区/县")
    private Long county;

    /** 区/县昵称 */
    @Excel(name = "区/县昵称")
    private String countyCn;

    /** 乡/镇 */
    @Excel(name = "乡/镇")
    private Long town;

    /** 乡/镇昵称 */
    @Excel(name = "乡/镇昵称")
    private String townCn;

    /** SIM卡号 */
    @Excel(name = "SIM卡号")
    private String phoneNumber;

    /** 制造商 */
    @Excel(name = "制造商")
    private String manufacturer;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long departmentId;

    /** 操作员 */
    @Excel(name = "操作员")
    private Long systemUserId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long fromResource;

    /** 设备类型(1：空气检测设备，2：水质检测设备) */
    @Excel(name = "设备类型(1：空气检测设备，2：水质检测设备)")
    private Long type;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String ip;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String port;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String userName;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String password;

    /** 是否是标准站0.不是、1.是、 */
    @Excel(name = "是否是标准站0.不是、1.是、")
    private Long isStandard;

    /** 国控战站点code */
    @Excel(name = "国控战站点code")
    private String guid;

    /** 站点id */
    @Excel(name = "站点id")
    private Long stationId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String addr;

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
    public void setRunStatus(Long runStatus) 
    {
        this.runStatus = runStatus;
    }

    public Long getRunStatus() 
    {
        return runStatus;
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
    public void setIsStandard(Long isStandard) 
    {
        this.isStandard = isStandard;
    }

    public Long getIsStandard() 
    {
        return isStandard;
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
            .append("runStatus", getRunStatus())
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
            .append("isStandard", getIsStandard())
            .append("guid", getGuid())
            .append("stationId", getStationId())
            .append("addr", getAddr())
            .toString();
    }
}
